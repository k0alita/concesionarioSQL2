package com.concesionario.service;

import com.concesionario.ConexionBD;
import com.concesionario.model.Coche;
import com.concesionario.model.Propietario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

/**
 * Contiene las operaciones de negocio y consultas SQL del concesionario.
 */
public class ConcesionarioService {

    private final ConexionBD conexionBD;

    public ConcesionarioService(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    /**
     * Crea las tablas propietarios, coches y traspasos si no existen.
     * En MySQL crea también el procedimiento almacenado sp_coches_por_marca.
     */
    public void inicializarTablas() {
        String idAuto = conexionBD.esMySQL()
                ? "INT AUTO_INCREMENT PRIMARY KEY"
                : "INTEGER PRIMARY KEY AUTOINCREMENT";

        String sqlPropietarios = """
                CREATE TABLE IF NOT EXISTS propietarios (
                    id_propietario %s,
                    dni VARCHAR(10) NOT NULL UNIQUE,
                    nombre VARCHAR(100) NOT NULL,
                    apellidos VARCHAR(150) NOT NULL,
                    telefono VARCHAR(15)
                )
                """.formatted(idAuto);

        String sqlCoches = """
                CREATE TABLE IF NOT EXISTS coches (
                    matricula VARCHAR(10) PRIMARY KEY,
                    marca VARCHAR(50) NOT NULL,
                    modelo VARCHAR(50) NOT NULL,
                    extras VARCHAR(255),
                    precio DECIMAL(10,2) NOT NULL,
                    id_propietario INT,
                    FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
                )
                """;

        String sqlTraspasos = """
                CREATE TABLE IF NOT EXISTS traspasos (
                    id_traspaso %s,
                    matricula_coche VARCHAR(10) NOT NULL,
                    id_vendedor INT,
                    id_comprador INT NOT NULL,
                    monto_economico DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
                    FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario),
                    FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)
                )
                """.formatted(idAuto);

        try (Connection conexion = conexionBD.getConnection();
             Statement statement = conexion.createStatement()) {

            statement.executeUpdate(sqlPropietarios);
            statement.executeUpdate(sqlCoches);
            statement.executeUpdate(sqlTraspasos);

            if (conexionBD.esMySQL()) {
                statement.execute("DROP PROCEDURE IF EXISTS sp_coches_por_marca");
                statement.execute("""
                        CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50))
                        BEGIN
                            SELECT matricula, marca, modelo, extras, precio, id_propietario
                            FROM coches
                            WHERE marca = p_marca;
                        END
                        """);
            }

            System.out.println("Tablas inicializadas correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al inicializar las tablas: " + e.getMessage());
        }
    }

    /**
     * Inserta un nuevo propietario en la base de datos.
     */
    public void registrarPropietario(Propietario propietario) {
        String sql = "INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES (?, ?, ?, ?)";
        try (Connection conexion = conexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, propietario.getDni());
            ps.setString(2, propietario.getNombre());
            ps.setString(3, propietario.getApellidos());
            ps.setString(4, propietario.getTelefono());
            ps.executeUpdate();
            System.out.println("Propietario registrado correctamente.");
        } catch (SQLException e) {
            if (esDuplicado(e)) {
                System.out.println("Error: Ya existe un propietario con ese DNI.");
            } else {
                System.out.println("Error al registrar propietario: " + e.getMessage());
            }
        }
    }

    /**
     * Inserta un nuevo coche perteneciente al concesionario (sin propietario).
     */
    public void insertarCoche(Coche coche) {
        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio, id_propietario) VALUES (?, ?, ?, ?, ?, NULL)";
        try (Connection conexion = conexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, coche.getMatricula());
            ps.setString(2, coche.getMarca());
            ps.setString(3, coche.getModelo());
            ps.setString(4, coche.getExtras());
            ps.setDouble(5, coche.getPrecio());
            ps.executeUpdate();
            System.out.println("Coche insertado correctamente.");
        } catch (SQLException e) {
            if (esDuplicado(e)) {
                System.out.println("Error: Ya existe un coche con esa matrícula.");
            } else {
                System.out.println("Error al insertar coche: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra por consola todos los coches que no tienen propietario.
     */
    public void listarCochesConcesionario() {
        String sql = "SELECT matricula, marca, modelo, extras, precio FROM coches WHERE id_propietario IS NULL";
        try (Connection conexion = conexionBD.getConnection();
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            boolean hayResultados = false;
            System.out.println("\n--- Coches del Concesionario ---");
            while (rs.next()) {
                hayResultados = true;
                Coche coche = new Coche(
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("extras"),
                        rs.getDouble("precio")
                );
                System.out.println(coche);
            }
            if (!hayResultados) {
                System.out.println("No hay coches en el concesionario.");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar coches: " + e.getMessage());
        }
    }

    /**
     * Muestra por consola todos los coches que tienen propietario indicando su DNI.
     */
    public void listarCochesPropietarios() {
        String sql = """
                SELECT c.matricula, c.marca, c.modelo, c.extras, c.precio, p.dni
                FROM coches c
                JOIN propietarios p ON c.id_propietario = p.id_propietario
                """;
        try (Connection conexion = conexionBD.getConnection();
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            boolean hayResultados = false;
            System.out.println("\n--- Coches de Propietarios ---");
            while (rs.next()) {
                hayResultados = true;
                Coche coche = new Coche(
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("extras"),
                        rs.getDouble("precio")
                );
                System.out.println(coche + " | DNI Propietario: " + rs.getString("dni"));
            }
            if (!hayResultados) {
                System.out.println("No hay coches asignados a propietarios.");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar coches de propietarios: " + e.getMessage());
        }
    }

    /**
     * Modifica los datos de un coche existente a partir de su matrícula.
     */
    public void modificarCoche(Coche coche) {
        String sql = "UPDATE coches SET marca = ?, modelo = ?, extras = ?, precio = ? WHERE matricula = ?";
        try (Connection conexion = conexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, coche.getMarca());
            ps.setString(2, coche.getModelo());
            ps.setString(3, coche.getExtras());
            ps.setDouble(4, coche.getPrecio());
            ps.setString(5, coche.getMatricula());

            if (ps.executeUpdate() > 0) {
                System.out.println("Coche modificado correctamente.");
            } else {
                System.out.println("No se encontró ningún coche con matrícula " + coche.getMatricula());
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar coche: " + e.getMessage());
        }
    }

    /**
     * Elimina un coche de la base de datos a partir de su matrícula.
     */
    public void borrarCoche(String matricula) {
        String sql = "DELETE FROM coches WHERE matricula = ?";
        try (Connection conexion = conexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, matricula);
            if (ps.executeUpdate() > 0) {
                System.out.println("Coche eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún coche con matrícula " + matricula);
            }
        } catch (SQLException e) {
            System.out.println("Error al borrar coche (puede tener traspasos asociados): " + e.getMessage());
        }
    }

    private boolean esDuplicado(SQLException e) {
        return e instanceof SQLIntegrityConstraintViolationException
                || (e.getMessage() != null && e.getMessage().contains("UNIQUE"));
    }
}

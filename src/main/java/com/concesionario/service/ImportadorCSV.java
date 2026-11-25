package com.concesionario.service;

import com.concesionario.ConexionBD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Importa coches desde un fichero CSV de forma transaccional (todos o ninguno).
 */
public class ImportadorCSV {

    private final ConexionBD conexionBD;

    /**
     * Crea el importador asociado al gestor de conexiones.
     *
     * @param conexionBD gestor de conexiones a la base de datos
     */
    public ImportadorCSV(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    /**
     * Lee el CSV configurado en config.properties e inserta los coches en una sola transacción.
     */
    public void importar() {
        String ruta = conexionBD.getPropiedad("ruta.csv");
        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio, id_propietario) VALUES (?, ?, ?, ?, ?, NULL)";

        try (Connection conexion = conexionBD.getConnection()) {
            conexion.setAutoCommit(false);

            try (BufferedReader br = new BufferedReader(new FileReader(ruta));
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                br.readLine(); // Saltar cabecera
                String linea;
                int contador = 0;

                while ((linea = br.readLine()) != null) {
                    if (linea.isBlank()) continue;

                    String[] campos = linea.split(";");
                    if (campos.length != 5) {
                        throw new IllegalArgumentException("Formato incorrecto en la línea: " + linea);
                    }

                    ps.setString(1, campos[0].trim());
                    ps.setString(2, campos[1].trim());
                    ps.setString(3, campos[2].trim());
                    ps.setString(4, campos[3].trim());
                    ps.setDouble(5, Double.parseDouble(campos[4].trim().replace(',', '.')));
                    ps.executeUpdate();
                    contador++;
                }

                conexion.commit();
                System.out.println("Importación completada: " + contador + " coches insertados.");
            } catch (IOException | SQLException | IllegalArgumentException e) {
                conexion.rollback();
                System.out.println("Error en la importación (rollback realizado, no se insertó ningún coche): " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión al importar CSV: " + e.getMessage());
        }
    }
}

package com.concesionario.ui;

import com.concesionario.ConexionBD;
import com.concesionario.model.Coche;
import com.concesionario.model.Propietario;
import com.concesionario.service.ConcesionarioService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Menú de texto por consola para gestionar el concesionario.
 */
public class MenuConsola {

    private final ConexionBD conexionBD;
    private final ConcesionarioService service;
    private final Scanner sc = new Scanner(System.in);

    public MenuConsola(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
        this.service = new ConcesionarioService(conexionBD);
    }

    /**
     * Muestra el menú en bucle hasta que el usuario selecciona salir.
     */
    public void iniciar() {
        int opcion = -1;
        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
                continue;
            }

            switch (opcion) {
                case 1 -> conectarBD();
                case 2 -> {
                    if (estaConectado()) service.inicializarTablas();
                }
                case 3 -> {
                    if (estaConectado()) registrarPropietario();
                }
                case 4 -> {
                    if (estaConectado()) insertarCoche();
                }
                case 5 -> System.out.println("Pendiente: Importar Coches (CSV)");
                case 6 -> {
                    if (estaConectado()) service.listarCochesConcesionario();
                }
                case 7 -> {
                    if (estaConectado()) service.listarCochesPropietarios();
                }
                case 8 -> {
                    if (estaConectado()) modificarCoche();
                }
                case 9 -> {
                    if (estaConectado()) borrarCoche();
                }
                case 10 -> System.out.println("Pendiente: Realizar Traspaso");
                case 11 -> System.out.println("Pendiente: Ejecutar Procedimiento Almacenado");
                case 12 -> System.out.println("Pendiente: Generar Informe Resumen");
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
        sc.close();
    }

    private void mostrarMenu() {
        String estado = (conexionBD.getMotor() != null) ? conexionBD.getMotor() : "Sin conectar";
        System.out.println("\n--- CONCESIONARIO (" + estado + ") ---");
        System.out.println("1. Conectar BBDD");
        System.out.println("2. Inicializar/Crear Tablas");
        System.out.println("3. Registrar Propietario");
        System.out.println("4. Insertar Coche");
        System.out.println("5. Importar Coches (CSV)");
        System.out.println("6. Listar Coches (Concesionario)");
        System.out.println("7. Listar Coches (Propietarios)");
        System.out.println("8. Modificar Coche");
        System.out.println("9. Borrar Coche");
        System.out.println("10. Realizar Traspaso");
        System.out.println("11. Ejecutar Procedimiento Almacenado (Solo MySQL)");
        System.out.println("12. Generar Informe Resumen");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
    }

    private void conectarBD() {
        if (conexionBD.getMotor() != null) {
            System.out.println("Ya estás conectado a " + conexionBD.getMotor()
                    + ". Reinicia el programa para cambiar de BBDD.");
            return;
        }

        System.out.println("1. MySQL");
        System.out.println("2. SQLite");
        System.out.print("Elige motor: ");
        String eleccion = sc.nextLine();

        String motor = eleccion.equals("1") ? "MYSQL" : eleccion.equals("2") ? "SQLITE" : null;
        if (motor == null) {
            System.out.println("Opción no válida.");
            return;
        }

        conexionBD.setMotor(motor);
        try (Connection conexion = conexionBD.getConnection()) {
            System.out.println("Conectado correctamente a " + motor);
        } catch (SQLException e) {
            conexionBD.setMotor(null);
            System.out.println("No se pudo conectar a " + motor + ": " + e.getMessage());
        }
    }

    private void registrarPropietario() {
        System.out.print("DNI: ");
        String dni = sc.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Apellidos: ");
        String apellidos = sc.nextLine().trim();
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine().trim();

        service.registrarPropietario(new Propietario(dni, nombre, apellidos, telefono));
    }

    private void insertarCoche() {
        System.out.print("Matrícula: ");
        String matricula = sc.nextLine().trim();
        Coche coche = pedirDatosCoche(matricula);
        if (coche != null) {
            service.insertarCoche(coche);
        }
    }

    private void modificarCoche() {
        System.out.print("Matrícula del coche a modificar: ");
        String matricula = sc.nextLine().trim();
        Coche coche = pedirDatosCoche(matricula);
        if (coche != null) {
            service.modificarCoche(coche);
        }
    }

    private void borrarCoche() {
        System.out.print("Matrícula del coche a borrar: ");
        String matricula = sc.nextLine().trim();
        service.borrarCoche(matricula);
    }

    private Coche pedirDatosCoche(String matricula) {
        System.out.print("Marca: ");
        String marca = sc.nextLine().trim();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine().trim();
        System.out.print("Extras (separados por coma): ");
        String extras = sc.nextLine().trim();
        System.out.print("Precio: ");
        try {
            double precio = Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
            return new Coche(matricula, marca, modelo, extras, precio);
        } catch (NumberFormatException e) {
            System.out.println("Precio no válido.");
            return null;
        }
    }

    private boolean estaConectado() {
        if (conexionBD.getMotor() == null) {
            System.out.println("Primero debes conectarte a una BBDD (opción 1).");
            return false;
        }
        return true;
    }
}

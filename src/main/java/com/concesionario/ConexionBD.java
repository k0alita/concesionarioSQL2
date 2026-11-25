package com.concesionario;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Gestiona la conexión JDBC a MySQL o SQLite leyendo {@code config.properties}.
 */
public class ConexionBD {

    private final Properties properties = new Properties();
    private String motor;

    /**
     * Carga las propiedades de conexión y rutas desde {@code config.properties}.
     *
     * @throws IOException si ocurre un error al leer el fichero de configuración
     */
    public ConexionBD() throws IOException {
        try (FileReader fileReader = new FileReader("src/main/resources/config.properties")) {
            properties.load(fileReader);
        }
    }

    /**
     * Abre y devuelve una conexión a la base de datos según el motor seleccionado.
     * En SQLite habilita las claves foráneas en la conexión.
     *
     * @return conexión JDBC abierta
     * @throws SQLException si falla la conexión con la base de datos
     */
    public Connection getConnection() throws SQLException {
        if (esMySQL()) {
            return DriverManager.getConnection(
                    properties.getProperty("mysql.url"),
                    properties.getProperty("mysql.user"),
                    properties.getProperty("mysql.password"));
        }
        Connection conexion = DriverManager.getConnection(properties.getProperty("sqlite.url"));
        try (Statement statement = conexion.createStatement()) {
            // Activa la comprobación de claves foráneas en SQLite.
            statement.execute("PRAGMA foreign_keys = ON;");
        }
        return conexion;
    }

    /**
     * Establece el motor de base de datos activo.
     *
     * @param motor {@code "MYSQL"} o {@code "SQLITE"}
     */
    public void setMotor(String motor) {
        this.motor = motor;
    }

    /**
     * Devuelve el nombre del motor activo.
     *
     * @return {@code "MYSQL"}, {@code "SQLITE"} o {@code null} si aún no está conectado
     */
    public String getMotor() {
        return motor;
    }

    /**
     * Indica si el motor configurado actualmente es MySQL.
     *
     * @return {@code true} si el motor es MySQL; {@code false} en caso contrario
     */
    public boolean esMySQL() {
        return "MYSQL".equals(motor);
    }

    /**
     * Obtiene el valor de una propiedad definida en {@code config.properties}.
     *
     * @param clave nombre de la propiedad
     * @return valor asociado o {@code null} si no existe
     */
    public String getPropiedad(String clave) {
        return properties.getProperty(clave);
    }
}

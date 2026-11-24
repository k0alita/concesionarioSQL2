package com.concesionario;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Gestiona la conexión JDBC a MySQL o SQLite leyendo config.properties.
 */
public class ConexionBD {

    private final Properties properties = new Properties();
    private String motor;

    public ConexionBD() throws IOException {
        try (FileReader fileReader = new FileReader("src/main/resources/config.properties")) {
            properties.load(fileReader);
        }
    }

    /**
     * Abre y devuelve una conexión a la base de datos según el motor seleccionado.
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
            statement.execute("PRAGMA foreign_keys = ON;");
        }
        return conexion;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getMotor() {
        return motor;
    }

    public boolean esMySQL() {
        return "MYSQL".equals(motor);
    }

    public String getPropiedad(String clave) {
        return properties.getProperty(clave);
    }
}

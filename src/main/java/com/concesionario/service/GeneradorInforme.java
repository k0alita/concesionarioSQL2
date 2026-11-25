package com.concesionario.service;

import com.concesionario.ConexionBD;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class GeneradorInforme {

    private final ConexionBD conexionBD;

    /**
     * Crea el generador de informes asociado al gestor de conexiones.
     *
     * @param conexionBD gestor de conexiones a la base de datos
     */
    public GeneradorInforme(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    /**
     * Crea el fichero informe_concesionario.txt con el total de coches,
     * el listado agrupado por marca y el extra más repetido.
     */
    public void generarInforme() {
        String ruta = conexionBD.getPropiedad("ruta.informe");

        try (Connection conexion = conexionBD.getConnection();
             Statement statement = conexion.createStatement();
             PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {

            pw.println("=========================================");
            pw.println("     INFORME RESUMEN CONCESIONARIO");
            pw.println("=========================================\n");

            // 1. Número total de coches
            try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM coches")) {
                int total = rs.next() ? rs.getInt(1) : 0;
                pw.println("Número total de coches: " + total + "\n");
            }

            // 2. Listado de coches por marca
            pw.println("--- Coches agrupados por marca ---");
            String sqlMarcas = "SELECT marca, modelo, matricula, precio FROM coches ORDER BY marca, modelo";
            try (ResultSet rs = statement.executeQuery(sqlMarcas)) {
                String marcaActual = "";
                while (rs.next()) {
                    String marca = rs.getString("marca");
                    if (!marca.equals(marcaActual)) {
                        marcaActual = marca;
                        pw.println("\nMarca: " + marcaActual);
                    }
                    pw.println("  - " + rs.getString("matricula")
                            + " | " + rs.getString("modelo")
                            + " | " + rs.getDouble("precio") + "€");
                }
            }

            // 3. Extras mas repetidos
            Map<String, Integer> conteoExtras = new HashMap<>();
            try (ResultSet rs = statement.executeQuery("SELECT extras FROM coches WHERE extras IS NOT NULL")) {
                while (rs.next()) {
                    String extras = rs.getString("extras");
                    if (extras == null || extras.isBlank()) continue;

                    for (String extra : extras.split("[|,]")) {
                        String limpio = extra.trim();
                        if (!limpio.isEmpty()) {
                            conteoExtras.put(limpio, conteoExtras.getOrDefault(limpio, 0) + 1);
                        }
                    }
                }
            }

            pw.println("\n--- Extra más repetido ---");
            String extraMasRepetido = null;
            int maxRepeticiones = 0;
            for (Map.Entry<String, Integer> entry : conteoExtras.entrySet()) {
                if (entry.getValue() > maxRepeticiones) {
                    maxRepeticiones = entry.getValue();
                    extraMasRepetido = entry.getKey();
                }
            }

            if (extraMasRepetido != null) {
                pw.println(extraMasRepetido + " (" + maxRepeticiones + " veces)");
            } else {
                pw.println("No hay extras registrados.");
            }

            System.out.println("Informe generado correctamente en: " + ruta);
        } catch (SQLException e) {
            System.out.println("Error SQL al generar el informe: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error al escribir el fichero de informe: " + e.getMessage());
        }
    }
}

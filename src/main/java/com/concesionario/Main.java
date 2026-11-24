package com.concesionario;

import com.concesionario.ui.MenuConsola;

import java.io.IOException;

/**
 * Punto de entrada de la aplicación del concesionario.
 */
public class Main {

    public static void main(String[] args) {
        try {
            ConexionBD conexionBD = new ConexionBD();
            MenuConsola menu = new MenuConsola(conexionBD);
            menu.iniciar();
        } catch (IOException e) {
            System.out.println("Error al cargar config.properties: " + e.getMessage());
        }
    }
}

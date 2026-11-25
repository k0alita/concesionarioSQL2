package com.concesionario.model;

/**
 * Representa un propietario (cliente) del concesionario.
 */
public class Propietario {

    private int idPropietario;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;

    /**
     * Crea un propietario sin ID asignado (para altas nuevas).
     *
     * @param dni       DNI único del propietario
     * @param nombre    nombre del propietario
     * @param apellidos apellidos del propietario
     * @param telefono  teléfono de contacto
     */
    public Propietario(String dni, String nombre, String apellidos, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }

    /**
     * Crea un propietario con todos sus datos incluido su identificador.
     *
     * @param idPropietario identificador único en base de datos
     * @param dni           DNI único del propietario
     * @param nombre        nombre del propietario
     * @param apellidos     apellidos del propietario
     * @param telefono      teléfono de contacto
     */
    public Propietario(int idPropietario, String dni, String nombre, String apellidos, String telefono) {
        this(dni, nombre, apellidos, telefono);
        this.idPropietario = idPropietario;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "[" + idPropietario + "] " + dni + " - " + nombre + " " + apellidos + " (Tel: " + telefono + ")";
    }
}

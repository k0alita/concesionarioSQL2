package com.concesionario.model;

/**
 * Representa un coche registrado en el concesionario.
 */
public class Coche {

    private String matricula;
    private String marca;
    private String modelo;
    private String extras;
    private double precio;
    private Integer idPropietario;

    /**
     * Crea un coche perteneciente al concesionario.
     *
     * @param matricula matrícula única del coche
     * @param marca     marca del vehículo
     * @param modelo    modelo del vehículo
     * @param extras    equipamiento adicional
     * @param precio    precio del coche
     */
    public Coche(String matricula, String marca, String modelo, String extras, double precio) {
        this(matricula, marca, modelo, extras, precio, null);
    }

    /**
     * Crea un coche con todos sus datos incluido el propietario.
     *
     * @param matricula     matrícula única del coche
     * @param marca         marca del vehículo
     * @param modelo        modelo del vehículo
     * @param extras        equipamiento adicional
     * @param precio        precio del coche
     * @param idPropietario ID del propietario o {@code null} si pertenece al concesionario
     */
    public Coche(String matricula, String marca, String modelo, String extras, double precio, Integer idPropietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropietario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Integer getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(Integer idPropietario) {
        this.idPropietario = idPropietario;
    }

    @Override
    public String toString() {
        String textoExtras = (extras != null && !extras.isEmpty()) ? extras : "Ninguno";
        return matricula + " | " + marca + " " + modelo + " | Precio: " + precio + "€ | Extras: " + textoExtras;
    }
}

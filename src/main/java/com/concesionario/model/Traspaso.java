package com.concesionario.model;

/**
 * Representa un traspaso de titularidad de un coche.
 */
public class Traspaso {

    private int idTraspaso;
    private String matriculaCoche;
    private Integer idVendedor;
    private int idComprador;
    private double montoEconomico;

    /**
     * Crea un traspaso sin ID asignado.
     *
     * @param matriculaCoche matrícula del coche traspasado
     * @param idVendedor     ID del vendedor
     * @param idComprador    ID del comprador
     * @param montoEconomico importe económico de la operación
     */
    public Traspaso(String matriculaCoche, Integer idVendedor, int idComprador, double montoEconomico) {
        this.matriculaCoche = matriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }

    /**
     * Crea un traspaso con todos sus datos incluido su identificador.
     *
     * @param idTraspaso     identificador único en base de datos
     * @param matriculaCoche matrícula del coche traspasado
     * @param idVendedor     ID del vendedor
     * @param idComprador    ID del comprador
     * @param montoEconomico importe económico de la operación
     */
    public Traspaso(int idTraspaso, String matriculaCoche, Integer idVendedor, int idComprador, double montoEconomico) {
        this(matriculaCoche, idVendedor, idComprador, montoEconomico);
        this.idTraspaso = idTraspaso;
    }

    public int getIdTraspaso() {
        return idTraspaso;
    }

    public void setIdTraspaso(int idTraspaso) {
        this.idTraspaso = idTraspaso;
    }

    public String getMatriculaCoche() {
        return matriculaCoche;
    }

    public void setMatriculaCoche(String matriculaCoche) {
        this.matriculaCoche = matriculaCoche;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public double getMontoEconomico() {
        return montoEconomico;
    }

    public void setMontoEconomico(double montoEconomico) {
        this.montoEconomico = montoEconomico;
    }

    @Override
    public String toString() {
        String vendedor = (idVendedor != null) ? String.valueOf(idVendedor) : "Concesionario";
        return "Traspaso #" + idTraspaso + " | Coche: " + matriculaCoche
                + " | Vendedor: " + vendedor + " -> Comprador: " + idComprador
                + " | Importe: " + montoEconomico + "€";
    }
}

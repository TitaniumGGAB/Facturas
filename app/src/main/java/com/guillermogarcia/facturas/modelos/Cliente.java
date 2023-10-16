package com.guillermogarcia.facturas.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements Serializable {

    private int id;
    private String nombre;
    private String apellidos;
    //Los apellidos son opcional porque una empresa no tiene apellidos
    private String cif;
    private String direccion;

    private List<Factura> facturas;


    //El id tendrá que ser autonumérico
    //En el fragment de los clientes tiene que aparecer
    //un botón círculo abajo/derecha de agregar un cliente.
    // Y lo mismo en el fragment de facturas

    public Cliente(){

    }

    public Cliente(int id, String nombre, String apellidos, String cif, String direccion, List<Factura> facturas) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cif = cif;
        this.direccion = direccion;
        this.facturas = facturas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(ArrayList<Factura> facturas) {
        this.facturas = facturas;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", cif='" + cif + '\'' +
                ", direccion='" + direccion + '\'' +
                ", facturas=" + facturas +
                '}';
    }
}

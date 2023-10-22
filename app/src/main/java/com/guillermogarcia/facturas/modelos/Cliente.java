package com.guillermogarcia.facturas.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cliente implements Serializable {


    private int id;
    private String nombre;
    //Los apellidos son opcional porque una empresa no tiene apellidos
    private String apellidos;
    private String telefono;
    private String email;
    private String cif;
    private String direccion;
    private Date fecha_agregado;

    private List<Factura> facturas;


    //El id tendrá que ser autonumérico
    //En el fragment de los clientes tiene que aparecer
    //un botón círculo abajo/derecha de agregar un cliente.
    // Y lo mismo en el fragment de facturas

    public Cliente(){

    }

    public Cliente(int id, String nombre, String apellidos, String telefono, String email,  String cif, String direccion, Date fecha_agregado, List<Factura> facturas) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.cif = cif;
        this.direccion = direccion;
        this.fecha_agregado = fecha_agregado;
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

    public String getTelefono(){
        return telefono;
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
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

    public Date getFecha_agregado(){
        return fecha_agregado;
    }

    public void setFecha_agregado(Date fecha_agregado) {
        this.fecha_agregado = fecha_agregado;
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
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", cif='" + cif + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fecha agregado='" + fecha_agregado + '\'' +
                ", facturas=" + facturas +
                '}';
    }
}

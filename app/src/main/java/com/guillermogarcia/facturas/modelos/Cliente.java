package com.guillermogarcia.facturas.modelos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Cliente implements Serializable {

    private String identificador;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private String cif;
    private String direccion;
    private Date fecha_agregado;

    private List<String> facturas;



    public Cliente(){

    }

    public Cliente(String identificador, String nombre, String apellidos, String telefono, String email,  String cif, String direccion, Date fecha_agregado, List<String> facturas) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.cif = cif;
        this.direccion = direccion;
        this.fecha_agregado = fecha_agregado;
        this.facturas = facturas;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
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

    public List<String> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<String> facturas) {
        this.facturas = facturas;
    }

    @Override
    public String toString() {
        //El modificado el toString convencional ya que así sale
        // mejor en el spinner de clientes que hay en modificarFacturas
        return nombre + " " + apellidos + " " + cif;
    }
}

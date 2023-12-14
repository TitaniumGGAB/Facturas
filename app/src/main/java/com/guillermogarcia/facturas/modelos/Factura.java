package com.guillermogarcia.facturas.modelos;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;

public class Factura implements Serializable, Comparable<Factura> {


    private String identificador;
    private String numeroFactura;
    private Date fecha;
    private String descripcion;
    private double baseImponible;
    //El iva del precio y el total se tendrán que calcular automáticamente.
    private double ivaPrecio;
    private double precioTotal;
    private Date fechaModificacion;
    private boolean pagado;
    private boolean borrador;

    private String cliente;



    public Factura(){

    }

    public Factura(String identificador, String numeroFactura, Date fecha, String descripcion, double baseImponible, double ivaPrecio, double precioTotal, Date fechaModificacion, boolean pagado, boolean borrador, String cliente) {

        Log.d("Factura", "Número de factura: " + numeroFactura);
        this.identificador = identificador;
        this.numeroFactura = numeroFactura;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.baseImponible = baseImponible;
        this.ivaPrecio = ivaPrecio;
        this.precioTotal = precioTotal;
        this.fechaModificacion = fechaModificacion;
        this.pagado = pagado;
        this.borrador = borrador;
        this.cliente = cliente;

    }

    @Override
    public int compareTo(Factura o) {
        if (this.getPrecioTotal()>o.getPrecioTotal()) {
            return 1;
        }else if (this.getPrecioTotal()<o.getPrecioTotal()) {
            return -1;
        }else {
            return 0;
        }
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String id) {
        this.identificador = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(double baseImponible) {
        this.baseImponible = baseImponible;
    }

    public double getIvaPrecio() {
        return ivaPrecio;
    }

    public void setIvaPrecio(double ivaPrecio) {
        this.ivaPrecio = ivaPrecio;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }


    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public boolean isBorrador() {
        return borrador;
    }

    public void setBorrador(boolean borrador) {
        this.borrador = borrador;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getCliente(){
        return cliente;
    }


}

package com.guillermogarcia.facturas.modelos;

import java.io.Serializable;
import java.util.Date;
//import java.sql.Date;
import java.util.GregorianCalendar;
/*Factura:
id,
cliente,
numero_factura,
fecha_modificacion,
fecha_factura,
(Integer) estado(pagado, pendiente, presupuesto),
descripción,
base_imponible,
iva,
total.*/

public class Factura implements Serializable, Comparable<Factura> {

    private int id;
    private String numeroFactura;
    private GregorianCalendar fecha;
    private String descripcion;
    private double baseImponible;
    //El iva del precio y el total se tendrán que calcular automáticamente.
    private double ivaPrecio;
    private double precioTotal;
    private Date fechaModificacion;
    private boolean pagado;
    private boolean borrador;

    private Cliente cliente;


    public Factura(){

    }

    public Factura(int id, String numeroFactura, GregorianCalendar fecha, String descripcion, double baseImponible, double ivaPrecio, double precioTotal, Date fechaModificacion, boolean pagado, boolean borrador, Cliente cliente) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public GregorianCalendar getFecha() {
        return fecha;
    }

    public void setFecha(GregorianCalendar fecha) {
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

}

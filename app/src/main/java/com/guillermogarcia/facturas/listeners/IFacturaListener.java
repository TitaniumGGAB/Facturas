package com.guillermogarcia.facturas.listeners;

import com.guillermogarcia.facturas.modelos.Factura;

public interface IFacturaListener {
    void onFacturaSeleccionado(Factura factura);
}

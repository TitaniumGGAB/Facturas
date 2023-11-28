package com.guillermogarcia.facturas.listeners;

import com.google.firebase.firestore.DocumentSnapshot;
import com.guillermogarcia.facturas.modelos.Cliente;

public interface IClienteListener {
    //void onClienteSeleccionado(Cliente cliente);
    void onClienteListSelected(DocumentSnapshot documentSnapshot, int position);
}

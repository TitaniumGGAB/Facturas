package com.guillermogarcia.facturas.listeners;

import com.google.firebase.firestore.DocumentSnapshot;

public interface IClienteListener {
    void onClienteListSelected(DocumentSnapshot documentSnapshot, int position);
}

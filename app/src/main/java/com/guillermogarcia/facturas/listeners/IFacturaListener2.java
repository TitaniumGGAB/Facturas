package com.guillermogarcia.facturas.listeners;

import com.google.firebase.firestore.DocumentSnapshot;

public interface IFacturaListener2 {
    void onFactura2ListSelected(DocumentSnapshot documentSnapshot, int position);
}

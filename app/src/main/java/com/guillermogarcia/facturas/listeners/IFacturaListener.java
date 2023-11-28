package com.guillermogarcia.facturas.listeners;

import com.google.firebase.firestore.DocumentSnapshot;

public interface IFacturaListener {
    void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position);
}

package com.guillermogarcia.facturas.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.listeners.IClienteListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentAgregarCliente extends Fragment implements IClienteListener{

    private EditText etNombre, etApellidos, etTelefono, etEmail, etCif, etDireccion;
    private Button btnAgregarCliente;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference clientesRef = db.collection("clientes");

    private Context context;
    public FragmentAgregarCliente() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_cliente, container, false);

        etNombre = view.findViewById(R.id.agregarNombreCliente);
        etApellidos = view.findViewById(R.id.agregarApellidosCliente);
        etTelefono = view.findViewById(R.id.agregarTelefonoCliente);
        etEmail = view.findViewById(R.id.agregarEmailCliente);
        etCif = view.findViewById(R.id.agregarCifCliente);
        etDireccion = view.findViewById(R.id.agregarDireccionCliente);
        btnAgregarCliente = view.findViewById(R.id.buttonAgregarCliente);

        btnAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCliente();
            }
        });

        return view;
    }

    private void agregarCliente() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String cif = etCif.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();


        // Verificamos que los campos no estén vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty() || cif.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(getActivity(), context.getString(R.string.faltan_rellenar_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos un mapa con los datos del cliente. Aunque podríamos hacerlo también con creado un objeto cliente
        Map<String, Object> cliente = new HashMap<>();
        cliente.put("nombre", nombre);
        cliente.put("apellidos", apellidos);
        cliente.put("telefono", telefono);
        cliente.put("email", email);
        cliente.put("cif", cif);
        cliente.put("direccion", direccion);
        cliente.put("facturas", new ArrayList<>());
        cliente.put("fecha_agregado", new Timestamp(new Date()));


        // Agregamos el cliente a la colección "Clientes" con ID automático
        clientesRef.add(cliente)
                .addOnSuccessListener(documentReference -> {
                    // Obtenemos el ID asignado por Firestore
                    String idCliente = documentReference.getId();

                    // Creamos y rellenamos el campo "identificador" en el documento recién creado
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("identificador", idCliente);

                    documentReference.update(updateData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), context.getString(R.string.cliente_agregado_exito) + idCliente, Toast.LENGTH_SHORT).show();
                                FragmentClientes fragmentClientes = new FragmentClientes(this);
                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, fragmentClientes);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), context.getString(R.string.error_actualizar_identificador_cliente), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), context.getString(R.string.error_agregar_cliente), Toast.LENGTH_SHORT).show();
                    // Loguea o maneja el error según tus necesidades.
                });
    }


    @Override
    public void onClienteListSelected(DocumentSnapshot documentSnapshot, int position) {

    }
}

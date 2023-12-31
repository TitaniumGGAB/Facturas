package com.guillermogarcia.facturas.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.modelos.Cliente;

import java.util.List;

public class FragmentModificarCliente extends Fragment {

    private EditText modificarNombre, modificarApellidos, modificarTelefono, modificarEmail, modificarCif, modificarDireccion;
    private Button buttonClienteModificar, buttonClienteEliminar;
    private Cliente cliente;
    private Cliente clienteactualizar;
    private Context context;

    // Método para crear una nueva instancia del fragmento y pasar los datos del cliente como argumentos
    public static FragmentModificarCliente newInstance(Cliente cliente) {
        FragmentModificarCliente fragment = new FragmentModificarCliente();
        Bundle args = new Bundle();
        args.putSerializable("cliente", cliente);
        fragment.setArguments(args);
        return fragment;
    }

    //Necesitamos crear e inicilizar un contexto para el FragmentManager
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modificar_cliente, container, false);

        modificarNombre = view.findViewById(R.id.modificarNombreCliente);
        modificarApellidos = view.findViewById(R.id.modificarApellidosCliente);
        modificarTelefono = view.findViewById(R.id.modificarTelefonoCliente);
        modificarEmail = view.findViewById(R.id.modificarEmailCliente);
        modificarCif = view.findViewById(R.id.modificarCifCliente);
        modificarDireccion = view.findViewById(R.id.modificarDireccionCliente);

        buttonClienteModificar = view.findViewById(R.id.buttonClienteModificar);
        buttonClienteEliminar = view.findViewById(R.id.buttonClienteEliminar);


        Bundle bundle = getArguments();
        cliente = (Cliente) bundle.getSerializable("cliente");
        modificarNombre.setText(cliente.getNombre());
        modificarApellidos.setText(cliente.getApellidos());
        modificarTelefono.setText(cliente.getTelefono());
        modificarEmail.setText(cliente.getEmail());
        modificarCif.setText(cliente.getCif());
        modificarDireccion.setText(cliente.getDireccion());

        buttonClienteModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nuevoNombre = modificarNombre.getText().toString();
                String nuevosApellidos = modificarApellidos.getText().toString();
                String nuevoTelefono = modificarTelefono.getText().toString();
                String nuevoEmail = modificarEmail.getText().toString();
                String nuevoCif = modificarCif.getText().toString();
                String nuevaDireccion = modificarDireccion.getText().toString();

                //inicializamos clienteactualizar y lo rellenamos con los nuevos datos
                // para luego utilizarlo para actualizar el cliente
                clienteactualizar = new Cliente();

                clienteactualizar.setIdentificador(cliente.getIdentificador());
                clienteactualizar.setNombre(nuevoNombre);
                clienteactualizar.setApellidos(nuevosApellidos);
                clienteactualizar.setCif(nuevoCif);
                clienteactualizar.setTelefono(nuevoTelefono);
                clienteactualizar.setFacturas(cliente.getFacturas());
                clienteactualizar.setDireccion(nuevaDireccion);
                clienteactualizar.setEmail(nuevoEmail);
                clienteactualizar.setFecha_agregado(cliente.getFecha_agregado());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference clientesRef = db.collection("clientes");

                String idCliente = cliente.getIdentificador();  // Asegúrate de tener un método getId() en tu clase Cliente

                clientesRef.document(idCliente).set(clienteactualizar)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), context.getString(R.string.datos_cliente_actualizados), Toast.LENGTH_SHORT).show();

                                FragmentDetalleCliente fragmentDetalleCliente = new FragmentDetalleCliente();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("cliente", clienteactualizar);
                                fragmentDetalleCliente.setArguments(bundle);

                                // Reemplazar el fragmento actual con el fragmento de detalles del cliente
                                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.content_frame, fragmentDetalleCliente);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al actualizar
                                Toast.makeText(getContext(), context.getString(R.string.error_datos_cliente_actualizados), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        buttonClienteEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoConfirmacion();
            }
        });

        return view;
    }
    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(context.getString(R.string.confirmar_eliminacion));
        builder.setMessage(context.getString(R.string.seguro_eliminar_cliente));

        builder.setPositiveButton(context.getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarClienteYFacturas();
            }
        });

        builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarClienteYFacturas() {
        // Primero eliminamos las facturas asociadas
        eliminarFacturas();

        // Luego eliminamos la cliente.
        eliminarCliente();
    }

    // Método para eliminar las facturas asociadas al cliente
    private void eliminarFacturas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facturasRef = db.collection("Facturas");

        List<String> facturasCliente = cliente.getFacturas();

        // Iteramos sobre las facturas del cliente y eliminar cada una
        for (String idFactura : facturasCliente) {
            facturasRef.document(idFactura).delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar error al eliminar factura (opcional)
                            Log.e("Eliminar Factura", "Error al eliminar factura: " + idFactura, e);
                        }
                    });
        }
    }

    // Método para eliminar al cliente después de eliminar las facturas
    private void eliminarCliente() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference clientesRef = db.collection("clientes");

        String idCliente = cliente.getIdentificador();

        clientesRef.document(idCliente).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al eliminar cliente
                        Toast.makeText(getContext(), context.getString(R.string.cliente_facturas_eliminados), Toast.LENGTH_SHORT).show();
                        // Cerrar el fragmento después de eliminar
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar cliente
                        Toast.makeText(getContext(), R.string.error_eliminar_cliente, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

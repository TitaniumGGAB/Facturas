package com.guillermogarcia.facturas.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentAgregarFactura extends Fragment implements IFacturaListener {

    private EditText etNumeroFactura, etDescripcionFactura, etBaseImponible;
    private DatePicker datePicker;
    private Switch switchBorrador, switchPagado;
    private Button btnAgregarFactura;
    private TextView tvIva, tvPrecioTotal;

    TextInputLayout inputLayoutBaseImponible;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerClientes;
    private List<Cliente> listaClientes = new ArrayList<>();

    public FragmentAgregarFactura() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agregar_factura, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNumeroFactura = view.findViewById(R.id.agregarNumeroFactura);
        etDescripcionFactura = view.findViewById(R.id.agregarDescripcionFactura);
        etBaseImponible = view.findViewById(R.id.agregarBaseImponible);
        datePicker = view.findViewById(R.id.datePicker);
        switchBorrador = view.findViewById(R.id.switchBorradorAgregar);
        switchPagado = view.findViewById(R.id.switchPagadoAgregar);
        btnAgregarFactura = view.findViewById(R.id.buttonAgregarFactura);
        tvIva = view.findViewById(R.id.tvIvaAgregar);
        tvPrecioTotal = view.findViewById(R.id.tvPrecioTotalAgregar);
        spinnerClientes = view.findViewById(R.id.spinnerClientes);
        inputLayoutBaseImponible = view.findViewById(R.id.inputBaseImponible);

        setupSpinner();

        btnAgregarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarFactura();
            }
        });

        etBaseImponible.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario implementar este método
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar este método
            }

            @Override
            public void afterTextChanged(Editable s) {
                calcularIvaYPrecioTotal();
            }
        });

        switchBorrador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Cuando se marca/desmarca switchBorrador
                if (isChecked) {
                    // Si switchBorrador está marcado, ocultar switchPagado
                    switchPagado.setVisibility(View.GONE);
                } else {
                    // Si switchBorrador no está marcado, mostrar switchPagado
                    switchPagado.setVisibility(View.VISIBLE);
                }
            }
        });

        switchPagado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Cuando se marca/desmarca switchPagado
                if (isChecked) {
                    // Si switchPagado está marcado, ocultar switchBorrador
                    switchBorrador.setVisibility(View.GONE);
                } else {
                    // Si switchPagado no está marcado, mostrar switchBorrador
                    switchBorrador.setVisibility(View.VISIBLE);
                }

            }

        });

    }

    private void setupSpinner() {
        // Referencia a la colección de clientes en Firestore
        db.collection("clientes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Limpiar la lista de clientes antes de agregar los nuevos
                    listaClientes.clear();

                    // Iterar sobre los documentos de clientes y agregarlos a la lista
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        if (cliente != null) {
                            listaClientes.add(cliente);
                        }
                    }

                    // Crear un adaptador para el Spinner
                    ArrayAdapter<Cliente> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listaClientes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establecer el adaptador en el Spinner
                    spinnerClientes.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error en la obtención de datos desde Firestore
                    Toast.makeText(requireContext(), "Error al obtener la lista de clientes", Toast.LENGTH_SHORT).show();
                });
    }

    private void agregarFactura() {
        if (TextUtils.isEmpty(etNumeroFactura.getText())
                || TextUtils.isEmpty(etDescripcionFactura.getText())
                || TextUtils.isEmpty(etBaseImponible.getText())) {
            Toast.makeText(getActivity(), "Completa todos los campos antes de agregar la factura", Toast.LENGTH_SHORT).show();
            return;
        }
        Cliente clienteSeleccionado = (Cliente) spinnerClientes.getSelectedItem();if (clienteSeleccionado == null) {
            Toast.makeText(getActivity(), "Selecciona un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> facturaData = new HashMap<>();
        facturaData.put("numeroFactura", etNumeroFactura.getText().toString());
        facturaData.put("descripcion", etDescripcionFactura.getText().toString());
        //facturaData.put("baseImponible", Double.parseDouble(etBaseImponible.getText().toString()));
        try {
            double baseImponible = Double.parseDouble(etBaseImponible.getText().toString());
            facturaData.put("baseImponible", baseImponible);

            // Resto del código para calcular y agregar la factura

        } catch (NumberFormatException e) {
            // Captura la excepción y muestra un mensaje de error en el TextInputLayout
            inputLayoutBaseImponible.setError("Ingrese un valor válido para la base imponible");
            return;
        }
        facturaData.put("cliente", clienteSeleccionado.getIdentificador());
        facturaData.put("fecha", getSelectedDate());
        facturaData.put("fechaModificacion", new Date());
        facturaData.put("pagado", switchPagado.isChecked());
        facturaData.put("borrador", switchBorrador.isChecked());

        // Calcular el ivaPrecio y precioTotal
        double baseImponible = Double.parseDouble(etBaseImponible.getText().toString());
        double ivaPrecio = baseImponible * 0.21;
        BigDecimal bd = new BigDecimal(ivaPrecio);
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        ivaPrecio = bd.doubleValue ();
        double precioTotal = baseImponible + ivaPrecio;
        bd = new BigDecimal(precioTotal);
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        precioTotal = bd.doubleValue();

        facturaData.put("ivaPrecio", ivaPrecio);
        facturaData.put("precioTotal", precioTotal);

        db.collection("Facturas")
                .add(facturaData)
                .addOnSuccessListener(documentReference -> {
                    // Obtener el ID asignado por Firestore
                    String idFactura = documentReference.getId();

                    // Actualizar el campo "identificador" en el documento de factura
                    DocumentReference facturaRef = db.collection("Facturas").document(idFactura);
                    facturaRef.update("identificador", idFactura)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Factura agregada con éxito. ID: " + idFactura, Toast.LENGTH_SHORT).show();
                                actualizarClienteConFactura(clienteSeleccionado, idFactura);
                                limpiarCampos();
                                FragmentFacturas fragmentFacturas = new FragmentFacturas(this);
                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, fragmentFacturas);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Error al actualizar el identificador de la factura", Toast.LENGTH_SHORT).show();
                                // Loguea o maneja el error según tus necesidades.
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al agregar factura", Toast.LENGTH_SHORT).show();
                    // Loguea o maneja el error según tus necesidades.
                });
    }

    private void actualizarClienteConFactura(Cliente cliente, String idFactura) {
        // Actualizar el array de facturas en el cliente
        cliente.getFacturas().add(idFactura);

        // Actualizar el documento del cliente en Firestore
        db.collection("clientes")
            .document(cliente.getIdentificador())
            .update("facturas", cliente.getFacturas())
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(), "Cliente actualizado con la nueva factura", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Error al actualizar el cliente con la nueva factura", Toast.LENGTH_SHORT).show();
                // Loguea o maneja el error según tus necesidades.

        });

    }


    private void calcularIvaYPrecioTotal() {
        try {
            if (!etBaseImponible.getText().toString().isEmpty()) {
                String baseImponibleStr = etBaseImponible.getText().toString();

                double baseImponible = Double.parseDouble(baseImponibleStr);
                double ivaPorcentaje = 0.21;
                double ivaPrecio = baseImponible * ivaPorcentaje;
                BigDecimal bd = new BigDecimal(ivaPrecio);
                bd = bd.setScale (2, BigDecimal.ROUND_UP);
                ivaPrecio = bd.doubleValue ();
                double precioTotal = baseImponible + ivaPrecio;
                bd = new BigDecimal(precioTotal);
                bd = bd.setScale (2, BigDecimal.ROUND_UP);
                precioTotal = bd.doubleValue ();

                tvIva.setText("IVA (21%): " + ivaPrecio + "€");
                tvPrecioTotal.setText("Total: " + precioTotal + "€");
            }
            // Resto del código para calcular y agregar la factura

        } catch (NumberFormatException e) {
            // Captura la excepción y muestra un mensaje de error en el TextInputLayout
            inputLayoutBaseImponible.setError("Ingrese un valor válido para la base imponible");
        }

    }

    private Date getSelectedDate() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0); // Hora fija a las 00:00:00

        return calendar.getTime();
    }

    private void limpiarCampos() {
        etNumeroFactura.setText("");
        etDescripcionFactura.setText("");
        etNumeroFactura.setText("");
        etBaseImponible.setText("");
        tvIva.setText("");
        tvPrecioTotal.setText("");
        switchBorrador.setChecked(false);
        switchPagado.setChecked(false);
        inputLayoutBaseImponible.setError(null);
    }

    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }
}
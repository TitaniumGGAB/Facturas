package com.guillermogarcia.facturas.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.MainActivity;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorClientes;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturas;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentModificarFactura extends Fragment implements IFacturaListener {

    private EditText etNumeroFactura, etDescripcionFactura, etBaseImponible;
    private DatePicker datePicker;
    private Switch switchBorrador, switchPagado;
    private Button btnModificarFactura, btnEliminarFactura;
    private TextView tvIva, tvPrecioTotal;

    TextInputLayout inputLayoutBaseImponible;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerClientes;
    private List<Cliente> listaClientes = new ArrayList<>();

    private String idFacturaModificar; // Variable para almacenar el ID de la factura a modificar

    private Factura factura;

    private Context context;

    public FragmentModificarFactura() {
        // Required empty public constructor
    }

    public static FragmentModificarFactura newInstance(Factura factura) {
        FragmentModificarFactura fragment = new FragmentModificarFactura();
        Bundle args = new Bundle();
        args.putSerializable("factura", factura);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modificar_factura, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNumeroFactura = view.findViewById(R.id.agregarNumeroFacturaModificar);
        etDescripcionFactura = view.findViewById(R.id.agregarDescripcionFacturaModificar);
        etBaseImponible = view.findViewById(R.id.agregarBaseImponibleModificar);
        datePicker = view.findViewById(R.id.datePickerModificar);
        switchBorrador = view.findViewById(R.id.switchBorradorAgregarModificar);
        switchPagado = view.findViewById(R.id.switchPagadoAgregarModificar);
        btnModificarFactura = view.findViewById(R.id.buttonAgregarFacturaModificar);
        btnEliminarFactura = view.findViewById(R.id.buttonEliminarFacturaModificar);
        tvIva = view.findViewById(R.id.tvIvaAgregarModificar);
        tvPrecioTotal = view.findViewById(R.id.tvPrecioTotalAgregarModificar);
        spinnerClientes = view.findViewById(R.id.spinnerClientesModificar);
        inputLayoutBaseImponible = view.findViewById(R.id.inputBaseImponibleModificar);

        // Obtener el ID de la factura a modificar del argumento del fragmento
        Bundle bundle = getArguments();
        if (bundle != null) {
            factura = (Factura) bundle.getSerializable("factura");
        }
        idFacturaModificar = factura.getIdentificador();

        setupSpinner();

        // Llenar los datos de la factura actual en los campos
        llenarDatosFactura();

        btnModificarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarFactura();
            }
        });

        btnEliminarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
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

    private void llenarDatosFactura() {

        // Llenar los campos con los datos de la factura
        etNumeroFactura.setText(factura.getNumeroFactura());
        etDescripcionFactura.setText(factura.getDescripcion());
        etBaseImponible.setText(String.valueOf(factura.getBaseImponible()));
        tvIva.setText("IVA(21%): " + String.valueOf(factura.getIvaPrecio()) + "€");
        tvPrecioTotal.setText("Total: " + String.valueOf(factura.getPrecioTotal()) + "€");
        switchBorrador.setChecked(factura.isBorrador());
        switchPagado.setChecked( factura.isPagado());

        // Configurar la fecha en el DatePicker
        Calendar calendar = Calendar.getInstance();
        Date fecha = factura.getFecha();
        calendar.setTime(fecha);
        datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Configurar el cliente seleccionado en el Spinner
        String idCliente = factura.getCliente();
        seleccionarClienteEnSpinner(idCliente);

    }

    private void seleccionarClienteEnSpinner(String idClienteSeleccionado) {
        // Iterar sobre la lista de clientes y seleccionar el cliente con el ID correspondiente
        for (int i = 0; i < listaClientes.size(); i++) {
            Cliente cliente = listaClientes.get(i);
            if (cliente.getIdentificador().equals(idClienteSeleccionado)) {
                spinnerClientes.setSelection(i);
                break; // Terminar el bucle una vez que se haya seleccionado el cliente
            }
        }
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
                    seleccionarClienteEnSpinner(factura.getCliente());
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error en la obtención de datos desde Firestore
                    Toast.makeText(requireContext(), "Error al obtener la lista de clientes", Toast.LENGTH_SHORT).show();
                });
    }

    private void modificarFactura() {

        if (TextUtils.isEmpty(etNumeroFactura.getText())
                || TextUtils.isEmpty(etDescripcionFactura.getText())
                || TextUtils.isEmpty(etBaseImponible.getText())) {
            Toast.makeText(requireContext(), "Completa todos los campos antes de modificar la factura", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente clienteSeleccionado = (Cliente) spinnerClientes.getSelectedItem();
        if (clienteSeleccionado == null) {
            Toast.makeText(requireContext(), "Selecciona un cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        if(idFacturaModificar != null) {
            String idClienteAnterior = factura.getCliente();
            if (!clienteSeleccionado.getIdentificador().equals(idClienteAnterior)) {
                // Eliminar la factura del cliente anterior
                eliminarFacturaDeClienteAnterior(idClienteAnterior);

                // Añadir la factura al nuevo cliente
                añadirFacturaANuevoCliente(clienteSeleccionado);
            }

            Factura facturaActualizar = new Factura();
            facturaActualizar.setIdentificador(idFacturaModificar);
            facturaActualizar.setNumeroFactura(etNumeroFactura.getText().toString());
            facturaActualizar.setDescripcion(etDescripcionFactura.getText().toString());

            Map<String, Object> facturaData = new HashMap<>();
            facturaData.put("numeroFactura", etNumeroFactura.getText().toString());
            facturaData.put("descripcion", etDescripcionFactura.getText().toString());

            try {
                double baseImponible = Double.parseDouble(etBaseImponible.getText().toString());
                facturaData.put("baseImponible", baseImponible);

                facturaActualizar.setBaseImponible(baseImponible);
            } catch (NumberFormatException e) {
                inputLayoutBaseImponible.setError("Ingrese un valor válido para la base imponible");
                return;
            }

            facturaActualizar.setCliente(clienteSeleccionado.getIdentificador());
            facturaActualizar.setFecha(getSelectedDate());
            facturaActualizar.setFechaModificacion(new Date());
            facturaActualizar.setPagado(switchPagado.isChecked());
            facturaActualizar.setBorrador(switchBorrador.isChecked());


            facturaData.put("cliente", clienteSeleccionado.getIdentificador());
            facturaData.put("fecha", getSelectedDate());
            facturaData.put("fechaModificacion", new Date());
            facturaData.put("pagado", switchPagado.isChecked());
            facturaData.put("borrador", switchBorrador.isChecked());

            // Calcular el IVA y el precio total
            double baseImponible = Double.parseDouble(etBaseImponible.getText().toString());
            double ivaPrecio = baseImponible * 0.21;
            facturaData.put("ivaPrecio", ivaPrecio);
            facturaActualizar.setIvaPrecio(ivaPrecio);

            double precioTotal = baseImponible + ivaPrecio;
            facturaData.put("precioTotal", precioTotal);
            facturaActualizar.setPrecioTotal(precioTotal);

            // Actualizar la factura en la base de datos
            db.collection("Facturas")
                .document(idFacturaModificar)
                .update(facturaData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Factura modificada con éxito", Toast.LENGTH_SHORT).show();

                    FragmentDetalleFactura fragmentDetalleFactura = new FragmentDetalleFactura();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("factura", facturaActualizar);
                    fragmentDetalleFactura.setArguments(bundle);

                    // Reemplazar el fragmento actual con el fragmento de detalles del cliente
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.content_frame, fragmentDetalleFactura);
                    transaction.addToBackStack(null);
                    transaction.commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al modificar factura", Toast.LENGTH_SHORT).show();
                });
        }else{
            Toast.makeText(requireContext(), "Error: ID de factura nulo", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarFacturaDeClienteAnterior(String idClienteAnterior) {
        // Utilizar el contexto almacenado
        Context context = getContext();


        // Obtener el cliente anterior
        db.collection("clientes")
                .document(idClienteAnterior)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Cliente clienteAnterior = documentSnapshot.toObject(Cliente.class);
                    if (clienteAnterior != null) {
                        // Eliminar la factura del array de facturas del cliente
                        clienteAnterior.getFacturas().remove(idFacturaModificar);

                        // Actualizar el documento del cliente en Firestore
                        db.collection("clientes")
                                .document(clienteAnterior.getIdentificador())
                                .set(clienteAnterior)  // Utilizar set para reemplazar completamente el documento
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Cliente actualizado después de eliminar factura", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al actualizar el cliente después de eliminar factura", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error en la obtención de datos desde Firestore
                    Toast.makeText(context, "Error al obtener datos del cliente anterior", Toast.LENGTH_SHORT).show();
                });
    }

    private void añadirFacturaANuevoCliente(Cliente nuevoCliente) {
        // Añadir la factura al array de facturas del nuevo cliente
        nuevoCliente.getFacturas().add(idFacturaModificar);

        // Actualizar el documento del nuevo cliente en Firestore
        db.collection("clientes").document(nuevoCliente.getIdentificador())
                .update("facturas", nuevoCliente.getFacturas())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Factura añadida al nuevo cliente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al añadir factura al nuevo cliente", Toast.LENGTH_SHORT).show();
                });
    }

    private Date getSelectedDate() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0); // Hora fija a las 00:00:00

        return calendar.getTime();
    }

    private void mostrarDialogoConfirmacion() {
        // Crear y mostrar un diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("¿Está seguro de eliminar la factura?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarFactura();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada si el usuario elige "No"
                    }
                })
                .show();
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

    private void eliminarFactura() {

        Context context = getContext();

        // Eliminar la factura de la base de datos
        db.collection("Facturas")
                .document(idFacturaModificar)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Factura eliminada con éxito", Toast.LENGTH_SHORT).show();
                    // Eliminar la factura de la lista de facturas asociadas al cliente
                    eliminarFacturaDeCliente();
                    FragmentFacturas f = new FragmentFacturas(this);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, f);
                    transaction.addToBackStack(null);  // Permite regresar al FragmentDetalleCliente
                    transaction.commit();
                    Log.d("Check", "MainActivity:AonNavigationItemSelectd Facturas");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar factura", Toast.LENGTH_SHORT).show();
                });
    }

    private void eliminarFacturaDeCliente() {
        Context context = getContext();
        // Obtener el cliente seleccionado en el Spinner
        Cliente clienteSeleccionado = (Cliente) spinnerClientes.getSelectedItem();

        if (clienteSeleccionado != null) {
            // Eliminar la factura del array de facturas del cliente
            clienteSeleccionado.getFacturas().remove(idFacturaModificar);

            // Actualizar el documento del cliente en Firestore
            db.collection("clientes")
                .document(clienteSeleccionado.getIdentificador())
                .set(clienteSeleccionado)  // Utilizar set para reemplazar completamente el documento
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cliente actualizado después de eliminar factura", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el cliente después de eliminar factura", Toast.LENGTH_SHORT).show();
                });

        }

    }

    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }
}
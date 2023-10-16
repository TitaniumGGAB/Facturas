package com.guillermogarcia.facturas.interfaces;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface IAPIService {

    @GET("clientes/all")
    Call<List<Cliente>> getClientes();

    @POST("clientes/add")
    Call<Boolean> addCliente(@Body Cliente cliente);

    @PUT("clientes/update")
    Call actualizarCliente(@Body Cliente Cliente);

    @DELETE("clientes/eliminar/{id}")
    Call eliminarCliente(int id);



    @GET("facturas/all")
    Call<List<Factura>> getFacturas();

    @POST("facturas/add")
    Call<Boolean> addFactura(@Body Factura factura);

    @PUT("facturas/update")
    Call actualizarFactura(@Body Factura factura);

    @DELETE("facturas/eliminar/{id}")
    Call eliminarFactura(int id);

    /* Creo que este bloque no me hace falta porque esto para introducir
    los dato del objeto en vez del objeto
    @POST("clientes/addValues")
    @FormUrlEncoded
    Call<Boolean> addFraseValues(@Field("texto") String texto,
                                 @Field("fechaProgramada") String fechaProgramada,
                                 @Field("idAutor") int idAutor,
                                 @Field("idCategoria")int idCategoria);

    */


}

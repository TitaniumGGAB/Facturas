package com.guillermogarcia.facturas.rest;

import com.guillermogarcia.facturas.interfaces.IAPIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static IAPIService instance;
    //http://192.168.20.121, http://192.168.1.133
    public static String direccionIp ="http://192.168.1.90";
    public static int port = 8045;
    //private static final String BASE_URL = "" + ":" + PORT;
    private static final String BASE_URL =  direccionIp + ":" + port;

    /* Lo hacemos privado para evitar que se puedan crear instancias de esta forma */
    private RestClient() {

    }

    public static synchronized IAPIService getInstance() {
        if(instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(IAPIService.class);
        }
        return instance;
    }
}

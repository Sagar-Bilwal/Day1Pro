package com.example.sagar.day1pro;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    PdfApi pdfApi;
    private static ApiClient INSTANCE;
    private ApiClient()
    {
        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://noteshub.co.in").build();
        pdfApi = retrofit.create(PdfApi.class);
    }
    public static ApiClient getInstance() {
        if(INSTANCE == null){
            INSTANCE = new ApiClient();
        }
        return INSTANCE;
    }

    public PdfApi getPdfAPI() {
        return pdfApi;
    }
}

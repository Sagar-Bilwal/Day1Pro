package com.example.sagar.day1pro;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface PdfApi
{
    @GET
    public Call<ResponseBody> getPdf(@Url String url);
}

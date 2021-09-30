package com.example.ContactBook.Data.Remote;

import com.example.ContactBook.Model.Contactitem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetDataService {
    @GET
    Call<List<Contactitem>> getAllData(@Url String url);
}

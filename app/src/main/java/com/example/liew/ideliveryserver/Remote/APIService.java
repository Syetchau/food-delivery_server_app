package com.example.liew.ideliveryserver.Remote;

import com.example.liew.ideliveryserver.Model.MyResponse;
import com.example.liew.ideliveryserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=yourKey"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

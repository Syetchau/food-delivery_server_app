package com.example.liew.ideliveryserver;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.liew.ideliveryserver.Common.Common;
import com.example.liew.ideliveryserver.Model.MyResponse;
import com.example.liew.ideliveryserver.Model.Notification;
import com.example.liew.ideliveryserver.Model.Sender;
import com.example.liew.ideliveryserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtTitle, edtMessage;
    FButton btnSubmit;

    APIService mService;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_send_message);

        mService = Common.getFCMClient();

        edtTitle = (MaterialEditText) findViewById(R.id.edtTitle);
        edtMessage = (MaterialEditText) findViewById(R.id.edtMessage);
        btnSubmit = (FButton) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyTitleAndMessage();
            }
        });

    }

    private void verifyTitleAndMessage() {
        if (TextUtils.isEmpty(edtTitle.getText().toString())) {
            Toast.makeText(this, "Title is empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(edtMessage.getText().toString())) {
            Toast.makeText(this, "Message is empty!", Toast.LENGTH_SHORT).show();
        } else
            sendNotification();
    }


    private void sendNotification() {
        Notification notification = new Notification(edtTitle.getText().toString(), edtMessage.getText().toString());

        Sender toTopic = new Sender();
        toTopic.to = new StringBuilder("/topics/").append(Common.topicName).toString();
        toTopic.notification = notification;

        mService.sendNotification(toTopic).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.isSuccessful())
                    Toast.makeText(SendMessage.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Toast.makeText(SendMessage.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}




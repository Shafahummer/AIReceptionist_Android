package com.kk.mysafedrive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class enquiry_response extends  ListeningActivity  {

    TextView s,resp;
    SharedPreferences sp;

    String fname="";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_response);

        s=findViewById(R.id.message);
        resp=findViewById(R.id.response);


        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name=sp.getString("name","");
        resp.setText("Hello "+name+"\n"+"Welcome"+"\nHow can I help you?");
        context = getApplicationContext();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        fname= formatter.format(date);

        try{
            VoiceRecognitionListener.getInstance().setListener(this);
        }catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


        startListening();


    }


    public static String text="",msg="";
    @Override
    public void processVoiceCommands(String... voiceCommands) {

        text+=voiceCommands[0]+" ";
        s.setText(text);
        msg=text;
        text="";

        s.setTextColor(Color.BLACK);
        s.setGravity(Gravity.CENTER);
        Log.d("=======",s.getText().toString());

        if(!s.getText().toString().equals(""))
        {
            RequestQueue queue = Volley.newRequestQueue(enquiry_response.this);
            String url = "http://192.168.43.107:5000/message";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the response string.
                    Log.d("+++++++++++++++++", response);
                    try {
                        JSONObject json = new JSONObject(response);
                        String res = json.getString("task");
                        if(!res.equals("error"))
                        {

                            resp.setText(res);
                            text="";

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("message", msg);

                    params.put("uid", sp.getString("id","0"));




                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


        restartListeningService();
    }

    @Override
    public void onBackPressed(){
        stopListening();
       // s.setText("");
       // resp.setText("");
        finish();

    }

}

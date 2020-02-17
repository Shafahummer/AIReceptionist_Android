package com.kk.mysafedrive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText e1,e2;
    Button b;
    ImageView imageView;
    SharedPreferences sp;
    String ip="",name="",email="",img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageView=findViewById(R.id.imageView);
        e1=findViewById(R.id.name);
        e2=findViewById(R.id.email);
        b=findViewById(R.id.button);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        img=getIntent().getStringExtra("img");
        ip="192.168.43.107";
        SharedPreferences.Editor ed=sp.edit();
        ed.putString("ip",ip);
        ed.commit();


        java.net.URL thumb_u;

        try {

            thumb_u = new java.net.URL("http://"+ip+":5000/static/check/"+img);
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
            imageView.setImageDrawable(thumb_d);



        }

        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e+"",Toast.LENGTH_LONG).show();
        }


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog pDialog = new ProgressDialog(RegisterActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();

                name = e1.getText().toString();
                email = e2.getText().toString();

                if (name.equals("")) {
                    e1.setError("Enter name");
                    e1.requestFocus();
                } else if (email.equals("")) {
                    e2.setError("Enter email");
                    e2.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    e2.setError("Enter Valid Email");
                    e2.requestFocus();
                }
                else {


                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    String url = "http://" + sp.getString("ip", "") + ":5000/reg";

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the response string.
                            Log.d("+++++++++++++++++", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                String res = json.getString("result");
                                if (!res.equals("error")) {
                                    SharedPreferences.Editor ed = sp.edit();
                                    ed.putString("id", res);
                                    ed.putString("name", name);
                                    ed.commit();
                                    // String id=sp.getString("id","");
                                    startActivity(new Intent(getApplicationContext(), enquiry_response.class));

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
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("name", name);

                            params.put("email", email);
                            params.put("img", img);


                            return params;
                        }
                    };
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            }
            });


    }

}

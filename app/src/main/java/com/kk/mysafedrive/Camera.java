package com.kk.mysafedrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.PreferenceChangeEvent;

public class Camera extends AppCompatActivity {

    public static String imageurl="";

    SharedPreferences sh;
    String path,fileName;
    byte[] byteArray = null;
    Uri imageUri=null;
    private static final int CAMERA_PIC_REQUEST = 0;


    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);


    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if  (requestCode == CAMERA_PIC_REQUEST)
            {
                if (resultCode == RESULT_OK)
                {
                    try {

                        imageurl = getRealPathFromURI(imageUri);
                        path=imageurl;

                        File file = new File(imageurl);
                        String[] val=path.split("/");
                        fileName=val[val.length-1];
                        int ln=(int) file.length();

                        try
                        {
                            InputStream inputStream = new FileInputStream(file);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();

                            byte[] b = new byte[ln];
                            int bytesRead;

                            while ((bytesRead = inputStream.read(b)) != -1)
                            {
                                bos.write(b, 0, bytesRead);
                            }
                            inputStream.close();
                            byteArray = bos.toByteArray();

                            uploadFile(path);
                        }
                        catch (IOException e)
                        {
                            Toast.makeText(this,"Strings :"+e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private String getRealPathFromURI(Uri contentURI) {

            Cursor cursor = getContentResolver()
                    .query(contentURI, null, null, null, null);
            if (cursor == null)
                path=contentURI.getPath();

            else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path=cursor.getString(idx);

            }
            if(cursor!=null)
                cursor.close();
            return path;
        }

        public void uploadFile(String sourceFileUri) {

	        url="http://192.168.43.107:5000/addimg";

            String fileName = sourceFileUri;
            try {
                FileUpload client = new FileUpload(url);
                client.connectForMultipart();

                client.addFilePart("files", fileName, byteArray);
                client.finishMultipart();
                String result = client.getResponse();
                JSONObject json = new JSONObject(result);
                String res = json.getString("result");
                String []resu=res.split("#");



                if(resu[0].equalsIgnoreCase( "na"))
                {
                    Intent i=new Intent(getApplicationContext(),RegisterActivity.class);
                    i.putExtra("img",resu[1]);
                    startActivity(i);
                }
                else
                {
                    SharedPreferences.Editor ed=sh.edit();
                   // Toast.makeText(getApplicationContext(),"Hello "+resu[0],Toast.LENGTH_LONG).show();
                    ed.putString("id",resu[1]);
                    ed.putString("name",resu[0]);
                    ed.commit();

                    Intent i=new Intent(getApplicationContext(),enquiry_response.class);
                    i.putExtra("name",resu[0]);
                    startActivity(i);
                }


                Log.d("response=======",result);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Exception 123 : " +e, Toast.LENGTH_LONG).show();
            }

        }


}

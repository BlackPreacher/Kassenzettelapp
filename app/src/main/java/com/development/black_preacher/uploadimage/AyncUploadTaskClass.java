package com.development.black_preacher.uploadimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bro on 25.11.2017.
 */

class AyncUploadTaskClass extends AsyncTask<Void, Void, String> {
    Uri uri;
    String server;
    Context context;

    int serverResponseCode;

    ProgressDialog progressDialog;


    public void setUri(Uri trans_uri){
        uri = trans_uri;
    }
    public void setServer(String trans_Server){
        server = trans_Server;
    }

    public void setContext(Context trans_context){
        context = trans_context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        StringBuilder stringBuilder = new StringBuilder();
        String serverResponseMessage = null;
        InputStream inputStream = null;
        int maxBufferSize = 1 * 1024 * 1024;
        int bytesAvailable = 0;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(server+"/upload.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("fileToUpload", "MyFile.jpg");

            DataOutputStream dos = null;
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\";filename=" + "MyFile.jpg" + "" + lineEnd);
            dos.writeBytes(lineEnd);


            bytesAvailable = inputStream.available();

            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;
            bytesRead = inputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = inputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = inputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)

            serverResponseCode = conn.getResponseCode();

            serverResponseMessage = conn.getResponseMessage();

            if (serverResponseCode == 200){
                String result = readStream(conn.getInputStream());
                System.out.println(result);
            }
            System.out.println(serverResponseCode);
            System.out.println(serverResponseMessage);



        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverResponseMessage;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Image is Uploading", "Please Wait", false, false);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // Dismiss the progress dialog after done uploading.
        progressDialog.dismiss();

        // Printing uploading success message coming from server on android app.
        if(serverResponseCode == 200){
            Toast.makeText(context, "Upload Complete", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
        }


        // Setting image as transparent after done uploading.
        //imageView.setImageResource(android.R.color.transparent);


    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}

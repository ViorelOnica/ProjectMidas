package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by SunQuest on 1/4/2018.
 */

public class UploadBackgroundTask extends AsyncTask<String,Void,String> {

    Context context;
    Dialog userAvatarDialog;

    UploadBackgroundTask(Context context){

        this.context=context;

    }

    UploadBackgroundTask(Context context, Dialog userAvatarDialog){

        this.context=context;
        this.userAvatarDialog=userAvatarDialog;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        General.CONNECTION_BUFFERING=true;

        String method=params[0];

        switch(method){
            case "uploadUserAvatar":
                String id_user=params[1];
                String encoded_image=params[2];

                return uploadUserAvatar(id_user,encoded_image);

        }

        return "unreachable";
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        General.CONNECTION_BUFFERING=false;

        switch(result){
            case "Upload succeded!":

                userAvatarDialog.dismiss();

                ImageButton avatarImageButton=(ImageButton)((AccountSettingsActivity)context).findViewById(R.id.avatarImageButton);
                avatarImageButton.setImageBitmap(AccountSettingsActivity.bitmap);

                break;
            case "Upload failed!":

                RelativeLayout uploadingBufferingArea=(RelativeLayout)userAvatarDialog.findViewById(R.id.uploadingBufferingArea);
                uploadingBufferingArea.setVisibility(View.INVISIBLE);

                TextView uploadFailedTextView=(TextView)userAvatarDialog.findViewById(R.id.uploadFailedMessageTextView);
                uploadFailedTextView.setVisibility(View.VISIBLE);

                break;

        }

    }

    private String uploadUserAvatar(String id_user, String encoded_image){
        String upload_url="http://"+General.SERVER_IP+"/client/upload_user_avatar.php";

        try{
            URL url=new URL(upload_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(id_user,"UTF-8")+"&"+
                    URLEncoder.encode("image","UTF-8")+"="+URLEncoder.encode(encoded_image,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));
            String response="";
            String line;
            while((line=bufferedReader.readLine())!=null){
                response=response+line;
            }
            bufferedReader.close();
            IS.close();
            httpURLConnection.disconnect();

            return response;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Upload failed!";
    }

}
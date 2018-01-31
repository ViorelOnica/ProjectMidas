package com.example.sunquest.midas_project;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.net.URLEncoder;

/**
 * Created by SunQuest on 12/25/2017.
 */

public class UserDataBackgroundTask extends AsyncTask<String,Void,String> {
    Context context;
    Dialog editDialog;

    UserDataBackgroundTask(Context context){
        this.context=context;
    }

    UserDataBackgroundTask(Context context, Dialog editDialog){
        this.context=context;
        this.editDialog=editDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String method=params[0];
        String user_id;

        General.CONNECTION_BUFFERING=true;

        switch(method){

            case "getUserData":
                MainMenuActivity.CURRENT_USER_DATA=getUserData();
                if(MainMenuActivity.CURRENT_USER_DATA==null)
                    return "Connection problem on extraction!";
                else
                    return "User data extracted!";

            case "updateUsername":
                user_id=params[1];
                String user_name=params[2];

                return updateUsername(user_id, user_name);

            case "updateBirthdate":
                user_id=params[1];
                String user_birthdate=params[2];

                return updateBirthdate(user_id, user_birthdate);

            case "updateCurrentCity":
                user_id=params[1];
                String user_currentcity=params[2];

                return updateCurrentCity(user_id, user_currentcity);

            case "updatePhoneNumber":
                user_id=params[1];
                String user_phonenumber=params[2];

                return updatePhoneNumber(user_id, user_phonenumber);

            case "updatePassword":
                user_id=params[1];
                String user_password=params[2];

                return updatePassword(user_id, user_password);

            case "sendRecoveryMail":
                String email=params[1];

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

            case "Username updated!":

                TextView usernameTextView=(TextView)((AccountSettingsActivity)context).findViewById(R.id.usernameTextView);
                usernameTextView.setText(MainMenuActivity.CURRENT_USER_DATA[1]);

                editDialog.dismiss();
                break;

            case "Birthdate updated!":

                editDialog.dismiss();

                break;

            case "CurrentCity updated!":

                editDialog.dismiss();

                break;

            case "PhoneNumber updated!":

                editDialog.dismiss();

                break;

            case "Password updated!":

                editDialog.dismiss();

                break;

            case "Connection problem on update!":

                editingBufferingUnshowOnEditDialog();

                TextView editingFailedMessageTextView=editDialog.findViewById(R.id.editingFailedMessageTextView);
                editingFailedMessageTextView.setVisibility(View.VISIBLE);
                editingFailedMessageTextView.setText("Connection problem");

                break;
            case "User data extracted!":

                extractingDataBufferingUnshow();

                ImageButton avatarImageButton=((MainMenuActivity)context).findViewById(R.id.avatarImageButton);

                String path="http://"+General.SERVER_IP+"/client/uploads/user"+String.valueOf(MainMenuActivity.CURRENT_USER_ID)+".png";
                if(MainMenuActivity.CURRENT_USER_DATA[8].equals("yes")){
                    Picasso.with(context)
                            .load(path)
                            .placeholder(R.drawable.avatar_icon)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(avatarImageButton);
                }

                break;

            case "Connection problem on extraction!":

                extractingDataBufferingUnshow();

                onConnectionLost();

                break;
        }
        Toast.makeText(context,result,Toast.LENGTH_LONG).show();
    }

    private String[] getUserData(){
        String get_user_data_url="http://"+General.SERVER_IP+"/client/get_current_user_data.php";

        try{
            URL url=new URL(get_user_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(String.valueOf(MainMenuActivity.CURRENT_USER_ID),"UTF-8");

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

            String[] userDataArray=response.split(";");

            return userDataArray;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private String updateUsername(String user_id, String user_name){

        String update_user_data_url="http://"+General.SERVER_IP+"/client/update_username.php";


        try{
            URL url=new URL(update_user_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            MainMenuActivity.CURRENT_USER_DATA[1]=user_name;

            return "Username updated!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem on update!";
    }

    private String updateBirthdate(String user_id, String user_birthdate){

        String update_user_data_url="http://"+General.SERVER_IP+"/client/update_birthdate.php";


        try{
            URL url=new URL(update_user_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("user_birthdate","UTF-8")+"="+URLEncoder.encode(user_birthdate,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            MainMenuActivity.CURRENT_USER_DATA[5]=user_birthdate;

            return "Birthdate updated!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem on update!";
    }

    private String updateCurrentCity(String user_id, String user_currentcity){

        String update_user_data_url="http://"+General.SERVER_IP+"/client/update_currentcity.php";


        try{
            URL url=new URL(update_user_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("user_currentcity","UTF-8")+"="+URLEncoder.encode(user_currentcity,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            MainMenuActivity.CURRENT_USER_DATA[6]=user_currentcity;

            return "CurrentCity updated!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem on update!";
    }

    private String updatePhoneNumber(String user_id, String user_phonenumber){

        String update_user_data_url="http://"+General.SERVER_IP+"/client/update_phonenumber.php";


        try{
            URL url=new URL(update_user_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("user_phonenumber","UTF-8")+"="+URLEncoder.encode(user_phonenumber,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            MainMenuActivity.CURRENT_USER_DATA[7]=user_phonenumber;

            return "PhoneNumber updated!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem on update!";
    }

    private String updatePassword(String user_id, String user_password){

        String update_user_data_url="http://"+General.SERVER_IP+"/client/update_password.php";


        try{
            URL url=new URL(update_user_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("user_password","UTF-8")+"="+URLEncoder.encode(user_password,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            MainMenuActivity.CURRENT_USER_DATA[2]=user_password;

            return "Password updated!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem on update!";
    }

    private void editingBufferingUnshowOnEditDialog(){
        RelativeLayout editingBufferingArea=(RelativeLayout)editDialog.findViewById(R.id.editingBufferingArea);
        editingBufferingArea.setVisibility(View.INVISIBLE);
    }

    private void extractingDataBufferingUnshow(){
        RelativeLayout connectionBufferingArea=(RelativeLayout)((MainMenuActivity)context).findViewById(R.id.extractingDataBufferingArea);
        connectionBufferingArea.setVisibility(View.INVISIBLE);
    }

    private void configConnectionLostDialog(Dialog connectionLostDialog){
        connectionLostDialog.setContentView(R.layout.connection_lost_dialog);
        connectionLostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        connectionLostDialog.setCanceledOnTouchOutside(false);
        connectionLostDialog.show();
    }

    private void onConnectionLost(){
        Dialog connectionLostDialog=new Dialog(context);

        configConnectionLostDialog(connectionLostDialog);

        ImageButton reconnectButton=(ImageButton)connectionLostDialog.findViewById(R.id.reconnectImageButton);
        ImageButton exitButton=(ImageButton)connectionLostDialog.findViewById(R.id.exitImageButton);

        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainMenuActivity)context).finish();
                context.startActivity(new Intent(context,MainMenuActivity.class));
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainMenuActivity)context).finish();
                context.startActivity(new Intent(context,LoginActivity.class));
            }
        });
    }


}
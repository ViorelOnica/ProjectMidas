package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
 * Created by SunQuest on 12/19/2017.
 */

public class LoginActivityBackgroundTask extends AsyncTask<String,Void,String> {
    Context context;
    Dialog createAccountDialog;

    LoginActivityBackgroundTask(Context context){
        this.context=context;
    }

    LoginActivityBackgroundTask(Context context, Dialog createAccountDialog){
        this.context=context;
        this.createAccountDialog=createAccountDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        General.CONNECTION_BUFFERING=true;

        String method=params[0];

        if(method.equals("register")){
            String user_name=params[1];
            String user_pass=params[2];
            String user_email=params[3];
            String user_type=params[4];

            if(getMaxUserIdFromUsernameTable().equals("Connection problem!"))
                return "Registration failed!";

            String user_id=String.valueOf(Integer.parseInt(getMaxUserIdFromUsernameTable())+1);
            return registerManager(user_id,user_name,user_pass,user_email,user_type);

        }

        else if(method.equals("login")){

            String user_name=params[1];
            String user_pass=params[2];

            if(!getCurrentUserId(user_name).equals("")&&!getCurrentUserId(user_name).equals("Connection problem!"))
                MainMenuActivity.CURRENT_USER_ID = Integer.parseInt(getCurrentUserId(user_name));

            return loginManager(user_name,user_pass);

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

            case "Duplicate username":

                connectingBufferingUnshowOnCreateAccountDialog();

                manageLoginFailedMessageTextViewOnCreateAccountDialog("Username taken");

                break;

            case "Duplicate email":

                connectingBufferingUnshowOnCreateAccountDialog();

                manageLoginFailedMessageTextViewOnCreateAccountDialog("Email taken");

                break;


            case "Registration success!":

                connectingBufferingUnshowOnLoginActivity();

                createAccountDialog.dismiss();

                context.startActivity(new Intent(context,MainMenuActivity.class));
                ((LoginActivity)context).finish();

                break;

            case "Registration failed!":

                connectingBufferingUnshowOnCreateAccountDialog();

                manageLoginFailedMessageTextViewOnCreateAccountDialog("Connection problem");

                break;

            case "Login success!":

                connectingBufferingUnshowOnLoginActivity();

                context.startActivity(new Intent(context,MainMenuActivity.class));
                ((LoginActivity)context).finish();

                break;

            case "Login failed!":

                connectingBufferingUnshowOnLoginActivity();

                manageLoginFailedMessageTextViewOnLoginActivity("Invalid username or password");

                break;

            case "Connection problem!":

                connectingBufferingUnshowOnLoginActivity();

                manageLoginFailedMessageTextViewOnLoginActivity("Connection problem");

                break;
        }

    }

    private String registerManager(String user_id, String user_name, String user_pass,String user_email, String user_type){

        String reg_url="http://"+General.SERVER_IP+"/client/register.php";

        if(usernameDuplicateCheck(user_name).equals("Duplicate username"))
            return "Duplicate username";

        if(emailDuplicateCheck(user_email).equals("Duplicate email"))
            return "Duplicate email";

        try{
            URL url=new URL(reg_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"+
                    URLEncoder.encode("user_pass","UTF-8")+"="+URLEncoder.encode(user_pass,"UTF-8")+"&"+
                    URLEncoder.encode("user_email","UTF-8")+"="+URLEncoder.encode(user_email,"UTF-8")+"&"+
                    URLEncoder.encode("user_type","UTF-8")+"="+URLEncoder.encode(user_type,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            MainMenuActivity.CURRENT_USER_ID=Integer.valueOf(user_id);

            return "Registration success!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Registration failed!";

    }

    private String loginManager(String user_name, String user_pass){
        String login_url="http://"+General.SERVER_IP+"/client/login.php";

        try{
            URL url=new URL(login_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"+
                    URLEncoder.encode("user_pass","UTF-8")+"="+URLEncoder.encode(user_pass,"UTF-8");

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

        return "Connection problem!";
    }

    private String getCurrentUserId(String user_name){
        String get_current_user_id_url="http://"+General.SERVER_IP+"/client/get_current_user_id.php";

        try{
            URL url=new URL(get_current_user_id_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8");

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

        return "Connection problem!";
    }

    private String getMaxUserIdFromUsernameTable(){
        String get_max_id_url="http://"+General.SERVER_IP+"/client/max_id.php";

        try{
            URL url=new URL(get_max_id_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

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

            if(response.equals(""))
                return "0";

            return response;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem!";
    }

    private String usernameDuplicateCheck(String user_name){
        String duplicate_username_check_url="http://"+General.SERVER_IP+"/client/duplicate_username_check.php";

        try{
            URL url=new URL(duplicate_username_check_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8");

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

        return "Connection problem!";

    }

    private String emailDuplicateCheck(String user_email){
        String duplicate_email_check_url="http://"+General.SERVER_IP+"/client/duplicate_email_check.php";

        try{
            URL url=new URL(duplicate_email_check_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_email","UTF-8")+"="+URLEncoder.encode(user_email,"UTF-8");

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

        return "Connection problem!";

    }

    private void connectingBufferingUnshowOnLoginActivity(){
        RelativeLayout connectionBufferingArea = (RelativeLayout)((LoginActivity)context).findViewById(R.id.connectionBufferingArea);
        connectionBufferingArea.setVisibility(View.INVISIBLE);
    }

    private void connectingBufferingUnshowOnCreateAccountDialog(){
        RelativeLayout connectionBufferingArea = (RelativeLayout)createAccountDialog.findViewById(R.id.connectionBufferingArea);
        connectionBufferingArea.setVisibility(View.INVISIBLE);
    }

    private void manageLoginFailedMessageTextViewOnCreateAccountDialog(String message){

        TextView loginFailedMessageTextViewOnCreateAccountDialog = (TextView)createAccountDialog.findViewById(R.id.loginFailedMessageTextView);
        loginFailedMessageTextViewOnCreateAccountDialog.setText(message);
        loginFailedMessageTextViewOnCreateAccountDialog.setVisibility(View.VISIBLE);

    }

    private void manageLoginFailedMessageTextViewOnLoginActivity(String message){
        TextView loginFailedMessageTextViewOnLoginActivity = (TextView) ((LoginActivity)context).findViewById(R.id.loginFailedMessageTextView);
        loginFailedMessageTextViewOnLoginActivity.setText(message);
        loginFailedMessageTextViewOnLoginActivity.setVisibility(View.VISIBLE);
    }

}

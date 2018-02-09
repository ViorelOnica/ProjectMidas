package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SunQuest on 12/27/2017.
 */

public class CompanyBackgroundTask extends AsyncTask<String,Void,String> {
    Context context;
    Dialog dialog;
    String filterStatus;

    CompanyBackgroundTask(Context context){
        this.context=context;
    }

    CompanyBackgroundTask(Context context, Dialog dialog){
        this.context=context;
        this.dialog=dialog;
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
            case "registerCompany":
                String company_name=params[1];
                String revenue=params[2];
                String currency=params[3];
                String employees_number=params[4];
                String foundation_date=params[5];
                String description=params[6];
                String approved="no";
                String image=params[7];

                if(getMaxIdFromCompanyTable().equals("Connection problem!"))
                    return "Connection problem!";

                String company_id=String.valueOf(Integer.parseInt(getMaxIdFromCompanyTable())+1);
                return registerCompany(company_id,company_name,revenue,currency,employees_number,foundation_date,description,approved,MainMenuActivity.CURRENT_USER_DATA[0],image);

            case "getCompanyData":

                String filter=params[1];
                filterStatus=params[2];

                MainMenuActivity.COMPANY_DATA=getCompanyData(filter);
                if(MainMenuActivity.COMPANY_DATA==null)
                    return "Connection problem on extraction!";
                else
                    return "Company data extracted!";

            case "getCompanyDashboardData":

                CompanyDashboardActivity.COMPANY_DASHBOARD_DATA=getCompanyDashboardData(CompanyDashboardActivity.CURRENT_COMPANY_ID);
                if(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA==null)
                    return "Company dashboard failed on extraction!";
                else
                    return "Company dashboard data extracted!";

            case "removeCompany":
                return removeCompany(CompanyDashboardActivity.CURRENT_COMPANY_ID);

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
            case "Company registered!":

                bufferingUnshowOnRegisterCompany();

                dialog.dismiss();

                final Dialog newCompanyRequestDialog=new Dialog(context);

                configNewCompanyRequestDialog(newCompanyRequestDialog);

                ImageButton okButton=(ImageButton)newCompanyRequestDialog.findViewById(R.id.okImageButton);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newCompanyRequestDialog.dismiss();
                        if(context.getClass().getSimpleName().equals("MainMenuActivity")) {
                            CompanyBackgroundTask getCompanyDataTask = new CompanyBackgroundTask(context);
                            getCompanyDataTask.execute("getCompanyData",MainMenuActivity.filter,"nofilter");
                        }

                    }
                });

                break;
            case "Connection problem!":

                bufferingUnshowOnRegisterCompany();

                TextView loginFailedMessageTextViewOnCreateAccountDialog = (TextView)dialog.findViewById(R.id.connectionProblemMessageTextView);
                loginFailedMessageTextViewOnCreateAccountDialog.setVisibility(View.VISIBLE);

                break;

            case "Company data extracted!":

                if(filterStatus.equals("filter")){
                    dialog.dismiss();
                }

                RecyclerView companyListRecyclerView=(RecyclerView)((MainMenuActivity)context).findViewById(R.id.companyListRecyclerView);

                CompanyListAdapter companyListAdapter=new CompanyListAdapter(context,MainMenuActivity.COMPANY_DATA);
                companyListRecyclerView.setAdapter(companyListAdapter);
                companyListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                break;

            case "Company dashboard data extracted!":

                TextView removeEditText=((CompanyDashboardActivity)context).findViewById(R.id.removeTextView);
                TextView editEditText=((CompanyDashboardActivity)context).findViewById(R.id.editTextView);

                if(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.username_author.equals(MainMenuActivity.CURRENT_USER_DATA[1])){
                    removeEditText.setVisibility(View.VISIBLE);
                    editEditText.setVisibility(View.VISIBLE);
                }

                bufferingUnshowOnDashboardActivity();

                TextView companyNameTextView=((CompanyDashboardActivity)context).findViewById(R.id.companyNameTextView);
                TextView companyGradeTextView=((CompanyDashboardActivity)context).findViewById(R.id.companyGrade);
                ImageView star1=((CompanyDashboardActivity)context).findViewById(R.id.star1);
                ImageView star2=((CompanyDashboardActivity)context).findViewById(R.id.star2);
                ImageView star3=((CompanyDashboardActivity)context).findViewById(R.id.star3);
                ImageView star4=((CompanyDashboardActivity)context).findViewById(R.id.star4);
                ImageView star5=((CompanyDashboardActivity)context).findViewById(R.id.star5);
                TextView foundationDateContentTextView=((CompanyDashboardActivity)context).findViewById(R.id.foundationDateContentTextView);
                TextView revenueContentTextView=((CompanyDashboardActivity)context).findViewById(R.id.revenueContentTextView);
                TextView employeesNumberContentTextView=((CompanyDashboardActivity)context).findViewById(R.id.numberOfEmployeesContentTextView);
                TextView descriptionContentTextView=((CompanyDashboardActivity)context).findViewById(R.id.descriptionContentTextView);

                companyNameTextView.setText(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.company_name);

                companyGradeTextView.setText(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.grade);

                highlightStars(star1,star2,star3,star4,star5,Integer.parseInt(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.current_user_stars_number));

                String[] foundationDateValues=CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.foundation_date.split("-");
                foundationDateContentTextView.setText(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.foundation_date);

                revenueContentTextView.setText(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.revenue+" "+CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.currency);

                employeesNumberContentTextView.setText(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.employees_number);

                descriptionContentTextView.setText(CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.description);

                RelativeLayout dashboardLayout=((CompanyDashboardActivity)context).findViewById(R.id.dashboardLayout);
                dashboardLayout.setVisibility(View.VISIBLE);

                break;

            case "Company dashboard failed on extraction!":
                bufferingUnshowOnDashboardActivity();
                onConnectionLost();
                break;

            case "Connection problem on extraction!":

                if(filterStatus.equals("filter")){
                    dialog.dismiss();
                }

                onConnectionLost();

                break;

            case "Remove company succeded!":

                dialog.dismiss();       //this is confirmation dialog

                bufferingUnshowOnConfirmationDialog(dialog);

                final Dialog companyRemovedDialog=new Dialog(context);
                configCompanyRemovedDialog(companyRemovedDialog);

                TextView companyRemovedTextView=companyRemovedDialog.findViewById(R.id.topTextView);
                okButton=companyRemovedDialog.findViewById(R.id.okImageButton);

                companyRemovedTextView.setText("Company removed!");
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        companyRemovedDialog.dismiss();
                        ((CompanyDashboardActivity)context).onBackPressed();
                    }
                });

                break;

            case "Remove company failed!":
                dialog.dismiss();
                onConnectionLost();

                break;

        }

    }

    private String removeCompany(String company_id){

        String remove_company_url="http://"+General.SERVER_IP+"/client/remove_company.php";

        try{
            URL url=new URL(remove_company_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            return "Remove company succeded!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Remove company failed!";
    }

    private String registerCompany(String company_id, String company_name, String revenue, String currency, String employees_number, String foundation_date, String description, String approved,String user_id, String image){

        String reg_url="http://"+General.SERVER_IP+"/client/insert_new_company.php";

        try{
            URL url=new URL(reg_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8")+"&"+
                    URLEncoder.encode("company_name","UTF-8")+"="+URLEncoder.encode(company_name,"UTF-8")+"&"+
                    URLEncoder.encode("revenue","UTF-8")+"="+URLEncoder.encode(revenue,"UTF-8")+"&"+
                    URLEncoder.encode("currency","UTF-8")+"="+URLEncoder.encode(currency,"UTF-8")+"&"+
                    URLEncoder.encode("employees_number","UTF-8")+"="+URLEncoder.encode(employees_number,"UTF-8")+"&"+
                    URLEncoder.encode("foundation_date","UTF-8")+"="+URLEncoder.encode(foundation_date,"UTF-8")+"&"+
                    URLEncoder.encode("description","UTF-8")+"="+URLEncoder.encode(description,"UTF-8")+"&"+
                    URLEncoder.encode("approved","UTF-8")+"="+URLEncoder.encode(approved,"UTF-8")+"&"+
                    URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("image","UTF-8")+"="+URLEncoder.encode(image,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            return "Company registered!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem!";
    }

    private String getMaxIdFromCompanyTable(){
        String get_max_id_url="http://"+General.SERVER_IP+"/client/max_company_id.php";

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

    private String getUsernameByUserId(String user_id){
        String get_username_url="http://"+General.SERVER_IP+"/client/get_username_by_userid.php";

        try{
            URL url=new URL(get_username_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");

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

        return null;
    }

    private List<InfoCompany> getCompanyData(String filter){
        String get_company_data_url="http://"+General.SERVER_IP+"/client/get_company_data.php";

        List<InfoCompany> companyData=new ArrayList<>();

        try{
            URL url=new URL(get_company_data_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("filter","UTF-8")+"="+URLEncoder.encode(filter,"UTF-8")+"&"+
                    URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(String.valueOf(MainMenuActivity.CURRENT_USER_ID),"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));
            String response="";
            String line;
            List<String> companyNamesArray=new ArrayList<>();
            List<String> authorNamesArray=new ArrayList<>();
            List<String> authorIdsArray=new ArrayList<>();
            List<String> companyIdsArray=new ArrayList<>();
            List<String> grades=new ArrayList<>();
            List<String> reviewsNumber=new ArrayList<>();

            while((line=bufferedReader.readLine())!=null){
                response=response+line;
            }

            String word="";
            int element=1;
            for(int i=0;i<response.length();i++){
                word=word+response.charAt(i);
                if(response.charAt(i+1)=='-') {
                    if(element==1) {
                        companyNamesArray.add(word);
                        element=2;
                    }
                    else if(element==2){
                        String username=getUsernameByUserId(word);
                        if(username==null)
                            return null;

                        authorIdsArray.add(word);
                        authorNamesArray.add(username);
                        element=3;
                    }
                    else if(element==3){
                        companyIdsArray.add(word);

                        String revNumber=getReviewsNumber(word);
                        if(revNumber.equals("Get reviews number failed!"))
                            return null;

                        reviewsNumber.add(revNumber);


                    }
                    word="";
                    i++;
                }
                if(response.charAt(i+1)=='|'){
                    grades.add(word);
                    word="";
                    i++;
                    element=1;
                }



            }

            for(int i=0;i<companyNamesArray.size();i++) {
                companyData.add(new InfoCompany(companyNamesArray.get(i), authorNamesArray.get(i), companyIdsArray.get(i),grades.get(i),reviewsNumber.get(i)));
            }

            bufferedReader.close();
            IS.close();
            httpURLConnection.disconnect();

            return companyData;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private String getReviewsNumber(String id_company){
        String get_reviews_number_url="http://"+General.SERVER_IP+"/client/get_reviews_number_by_companyid.php";

        try{
            URL url=new URL(get_reviews_number_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_company","UTF-8")+"="+URLEncoder.encode(id_company,"UTF-8");

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

        return "Get reviews number failed!";
    }

    private InfoCompany getCompanyDashboardData(String company_id){
        String filter_url="http://"+General.SERVER_IP+"/client/get_company_dashboard_data.php";;

        InfoCompany companyDashboardData=new InfoCompany();

        try{
            URL url=new URL(filter_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8");

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

            String word="";
            int element=1;
            for(int i=0;i<response.length();i++){
                word=word+response.charAt(i);
                if(response.charAt(i+1)=='`') {
                    if(element==1) {
                        companyDashboardData.company_name=word;
                        element=2;
                    }
                    else if(element==2){
                        companyDashboardData.revenue=word;
                        element=3;
                    }
                    else if(element==3){
                        companyDashboardData.currency=word;
                        element=4;
                    }
                    else if(element==4){
                        companyDashboardData.employees_number=word;
                        element=5;
                    }
                    else if(element==5){
                        companyDashboardData.foundation_date=word;
                        element=6;
                    }
                    else if(element==6){
                        companyDashboardData.description=word;
                        element=7;
                    }
                    else if(element==7){

                        String username=getUsernameByUserId(word);
                        if(username==null)
                            return null;

                        companyDashboardData.username_author=username;
                        element=8;
                    }
                    word="";
                    i++;
                }
                if(response.charAt(i+1)=='|'){
                    companyDashboardData.grade=word;
                    i++;
                    element=1;
                }
            }

            String starsNumber=getStarsNumber(String.valueOf(MainMenuActivity.CURRENT_USER_ID),company_id);
            if(starsNumber==null)
                return null;

            companyDashboardData.current_user_stars_number=starsNumber;

            bufferedReader.close();
            IS.close();
            httpURLConnection.disconnect();

            return companyDashboardData;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private void bufferingUnshowOnRegisterCompany(){
        RelativeLayout connectionBufferingArea = (RelativeLayout)dialog.findViewById(R.id.bufferingArea);
        connectionBufferingArea.setVisibility(View.INVISIBLE);
    }

    private void bufferingUnshowOnDashboardActivity(){
        ProgressBar bufferingProgressBar=(ProgressBar)((CompanyDashboardActivity)context).findViewById(R.id.bufferingProgressBar);
        bufferingProgressBar.setVisibility(View.INVISIBLE);
    }

    private void bufferingUnshowOnConfirmationDialog(Dialog confirmationDialog){
        ProgressBar bufferingProgressBar=confirmationDialog.findViewById(R.id.bufferingProgressBar);
        bufferingProgressBar.setVisibility(View.INVISIBLE);
    }

    private void configNewCompanyRequestDialog(Dialog newCompanyRequestDialog){
        newCompanyRequestDialog.setContentView(R.layout.new_company_request_dialog);
        newCompanyRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        newCompanyRequestDialog.setCanceledOnTouchOutside(false);
        newCompanyRequestDialog.show();

    }

    private void configCompanyRemovedDialog(Dialog companyRemovedDialog){
        companyRemovedDialog.setContentView(R.layout.new_company_request_dialog);
        companyRemovedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        companyRemovedDialog.setCanceledOnTouchOutside(false);
        companyRemovedDialog.show();

    }

    private void highlightStars(ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5, int stars_number){
        switch(stars_number){

            case 0:
                star1.setImageResource(R.drawable.white_star_icon);
                star2.setImageResource(R.drawable.white_star_icon);
                star3.setImageResource(R.drawable.white_star_icon);
                star4.setImageResource(R.drawable.white_star_icon);
                star5.setImageResource(R.drawable.white_star_icon);
                break;

            case 1:
                star1.setImageResource(R.drawable.yellow_star_icon);
                star2.setImageResource(R.drawable.white_star_icon);
                star3.setImageResource(R.drawable.white_star_icon);
                star4.setImageResource(R.drawable.white_star_icon);
                star5.setImageResource(R.drawable.white_star_icon);
                break;

            case 2:
                star1.setImageResource(R.drawable.yellow_star_icon);
                star2.setImageResource(R.drawable.yellow_star_icon);
                star3.setImageResource(R.drawable.white_star_icon);
                star4.setImageResource(R.drawable.white_star_icon);
                star5.setImageResource(R.drawable.white_star_icon);
                break;

            case 3:

                star1.setImageResource(R.drawable.yellow_star_icon);
                star2.setImageResource(R.drawable.yellow_star_icon);
                star3.setImageResource(R.drawable.yellow_star_icon);
                star4.setImageResource(R.drawable.white_star_icon);
                star5.setImageResource(R.drawable.white_star_icon);
                break;

            case 4:
                star1.setImageResource(R.drawable.yellow_star_icon);
                star2.setImageResource(R.drawable.yellow_star_icon);
                star3.setImageResource(R.drawable.yellow_star_icon);
                star4.setImageResource(R.drawable.yellow_star_icon);
                star5.setImageResource(R.drawable.white_star_icon);
                break;

            case 5:
                star1.setImageResource(R.drawable.yellow_star_icon);
                star2.setImageResource(R.drawable.yellow_star_icon);
                star3.setImageResource(R.drawable.yellow_star_icon);
                star4.setImageResource(R.drawable.yellow_star_icon);
                star5.setImageResource(R.drawable.yellow_star_icon);
                break;
        }
    }

    private String getStarsNumber(String user_id, String company_id){

        String get_company_stars_url="http://"+General.SERVER_IP+"/client/get_company_stars.php";

        try{
            URL url=new URL(get_company_stars_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                    URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8");

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

        return "wtf";
    }

    private void onConnectionLost(){
        Dialog connectionLostDialog=new Dialog(context);

        configConnectionLostDialog(connectionLostDialog);

        ImageButton reconnectButton=(ImageButton)connectionLostDialog.findViewById(R.id.reconnectImageButton);
        ImageButton exitButton=(ImageButton)connectionLostDialog.findViewById(R.id.exitImageButton);

        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context.getClass().getSimpleName().equals("MainMenuActivity")) {
                    ((MainMenuActivity)context).finish();
                    context.startActivity(new Intent(context, MainMenuActivity.class));
                }
                else{
                    ((CompanyDashboardActivity)context).finish();
                    context.startActivity(new Intent(context, CompanyDashboardActivity.class));
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context.getClass().getSimpleName().equals("MainMenuActivity")){
                    ((MainMenuActivity)context).finish();
                    context.startActivity(new Intent(context,LoginActivity.class));
                }
                else{
                    ((CompanyDashboardActivity)context).finish();
                    context.startActivity(new Intent(context,LoginActivity.class));
                }
            }
        });
    }

    private void configConnectionLostDialog(Dialog connectionLostDialog){
        connectionLostDialog.setContentView(R.layout.connection_lost_dialog);
        connectionLostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        connectionLostDialog.setCanceledOnTouchOutside(false);
        connectionLostDialog.show();
    }

}
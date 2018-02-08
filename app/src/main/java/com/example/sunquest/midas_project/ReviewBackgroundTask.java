package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunQuest on 12/28/2017.
 */

public class ReviewBackgroundTask extends AsyncTask<String,Void,String> {
    Context context;
    ProgressBar bufferingProgressBar;
    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;
    TextView companyGrade;

    ImageView likeButton;

    String current_grade;

    Dialog commentariesDialog;

    View view;

    String likesNumber;

    String likedStatus;

    TextView likesNumberTextView;

    ReviewBackgroundTask(Context context, ProgressBar bufferingProgressBar, ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5){

        this.context=context;
        this.bufferingProgressBar=bufferingProgressBar;

        this.star1=star1;
        this.star2=star2;
        this.star3=star3;
        this.star4=star4;
        this.star5=star5;
    }

    ReviewBackgroundTask(Context context, ProgressBar bufferingProgressBar, ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5,TextView companyGrade){

        this.context=context;
        this.bufferingProgressBar=bufferingProgressBar;

        this.star1=star1;
        this.star2=star2;
        this.star3=star3;
        this.star4=star4;
        this.star5=star5;

        this.companyGrade=companyGrade;
    }

    ReviewBackgroundTask (Context context, ProgressBar bufferingProgressBar, Dialog commentariesDialog,View view){
        this.context=context;
        this.bufferingProgressBar=bufferingProgressBar;
        this.commentariesDialog=commentariesDialog;
        this.view=view;
    }

    ReviewBackgroundTask(Context context, Dialog commentariesDialog){
        this.context=context;
        this.commentariesDialog=commentariesDialog;
        this.bufferingProgressBar=commentariesDialog.findViewById(R.id.topProgressBar);
    }

    ReviewBackgroundTask(Context context,ProgressBar bufferingProgressBar, TextView likesNumberTextView, ImageView likeButton){
        this.context=context;
        this.bufferingProgressBar=bufferingProgressBar;
        this.likesNumberTextView=likesNumberTextView;
        this.likeButton=likeButton;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        General.CONNECTION_BUFFERING=true;

        String method=params[0];
        String id_user;
        String company_id;

        switch(method){
            case "manageRating":

                String stars=params[1];
                id_user=params[2];
                company_id=params[3];

                return manageRating(id_user,company_id,stars);

            case "highlightStars":
                id_user=params[1];
                company_id=params[2];

                return getStarsNumber(id_user,company_id);

            case "newComment":
                company_id=params[1];
                id_user=params[2];
                String comment=params[3];

                return insertNewComment(company_id,id_user,comment);

            case "getCommentaries":

                bufferingProgressBar.setVisibility(View.VISIBLE);

                company_id=params[1];

                CompanyDashboardActivity.commentariesData=getCommentariesData(company_id);

                if(CompanyDashboardActivity.commentariesData==null)
                    return "Commentaries extraction failed!";

                return "Commentaries extraction succeded!";

            case "likeComment":

                bufferingProgressBar.setVisibility(View.VISIBLE);

                String id_comment=params[1];
                id_user=params[2];

                return manageLikeComment(id_comment,id_user);

            case "getLikesNumber":

                id_comment=params[1];
                id_user=params[2];
                return getLikesNumber(id_comment,id_user);

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
        bufferingProgressBar.setVisibility(View.INVISIBLE);

        switch(result){

            case "Company grade updated!1":
                highlightStars(1);
                companyGrade.setText(current_grade);
                break;
            case "Company grade updated!2":
                highlightStars(2);
                companyGrade.setText(current_grade);
                break;
            case "Company grade updated!3":
                highlightStars(3);
                companyGrade.setText(current_grade);
                break;
            case "Company grade updated!4":
                highlightStars(4);
                companyGrade.setText(current_grade);
                break;
            case "Company grade updated!5":
                highlightStars(5);
                companyGrade.setText(current_grade);
                break;
            case "0":
                highlightStars(0);
                break;
            case "1":
                highlightStars(1);
                break;
            case "2":
                highlightStars(2);
                break;
            case "3":
                highlightStars(3);
                break;
            case "4":
                highlightStars(4);
                break;
            case "5":
                highlightStars(5);
                break;

            case "Connection problem!":
                onConnectionLost();

                break;

            case "Comment insertion succeded!":

                ImageButton sendButton=(ImageButton)commentariesDialog.findViewById(R.id.sendImageButton);
                CustomEditText writeCommentEditText=(CustomEditText)commentariesDialog.findViewById(R.id.writeCommentEditText);

                sendButton.setImageResource(R.drawable.send_icon);

                writeCommentEditText.setText("");
                writeCommentEditText.setCursorVisible(false);

                InputMethodManager imm = (InputMethodManager)((CompanyDashboardActivity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                break;

            case "Comment insertion failed!":

                sendButton=(ImageButton)commentariesDialog.findViewById(R.id.sendImageButton);
                writeCommentEditText=(CustomEditText)commentariesDialog.findViewById(R.id.writeCommentEditText);

                sendButton.setImageResource(R.drawable.send_icon);

                writeCommentEditText.setText("");
                writeCommentEditText.setCursorVisible(false);

                imm = (InputMethodManager)((CompanyDashboardActivity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                onConnectionLost();

                break;

            case "Commentaries extraction succeded!":

                RecyclerView commentariesListRecyclerView=(RecyclerView)commentariesDialog.findViewById(R.id.commentariesListRecyclerView);

                CommentariesListAdapter commentariesListAdapter=new CommentariesListAdapter(context,CompanyDashboardActivity.commentariesData);
                commentariesListRecyclerView.setAdapter(commentariesListAdapter);
                commentariesListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

                break;

            case "Commentaries extraction failed!":

                onConnectionLost();

                break;

            case "Like update succeded!":

                Toast.makeText(context,likedStatus,Toast.LENGTH_LONG).show();
                if(likedStatus.equals("yes")) {
                    likeButton.setImageResource(R.drawable.like_button2);
                    likesNumberTextView.setTextColor(Color.parseColor("#5089b1"));
                }
                else {
                    likeButton.setImageResource(R.drawable.like_button);
                    likesNumberTextView.setTextColor(Color.parseColor("#50b1a6"));
                }

                if(Integer.parseInt(likesNumber)==0){
                    likesNumberTextView.setVisibility(View.INVISIBLE);
                }
                else{
                    likesNumberTextView.setVisibility(View.VISIBLE);
                    likesNumberTextView.setText(likesNumber);
                }

                break;

            case "Like update failed!":

                onConnectionLost();

                break;

            case "Likes number extraction succeded!":

                if(likedStatus.equals("yes")){
                    likeButton.setImageResource(R.drawable.like_button2);
                    likesNumberTextView.setTextColor(Color.parseColor("#5089b1"));
                }
                else{
                    likeButton.setImageResource(R.drawable.like_button);
                    likesNumberTextView.setTextColor(Color.parseColor("#50b1a6"));
                }

                if(Integer.parseInt(likesNumber)==0){
                    likesNumberTextView.setVisibility(View.INVISIBLE);
                }
                else{
                    likesNumberTextView.setVisibility(View.VISIBLE);
                    likesNumberTextView.setText(likesNumber);
                }

                break;

        }

    }

    private String getLikedStatus(String id_user, String id_comment){
        String get_liked_status_url="http://"+General.SERVER_IP+"/client/get_liked_status.php";

        try{
            URL url=new URL(get_liked_status_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(id_user,"UTF-8")+"&"+
                    URLEncoder.encode("id_comment","UTF-8")+"="+URLEncoder.encode(id_comment,"UTF-8");

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

            if(response.equals("yes"))
                response="yes";
            else
                response="no";

            return response;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Like status extraction failed!";
    }

    private String getLikesNumber(String id_comment, String id_user){
        String get_username_url="http://"+General.SERVER_IP+"/client/get_likes_number.php";

        try{
            URL url=new URL(get_username_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_comment","UTF-8")+"="+URLEncoder.encode(id_comment,"UTF-8");

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

            likesNumber=response;

            if((likedStatus=getLikedStatus(id_user,id_comment)).equals("Like status extraction failed!"))
                return "Likes number extraction failed!";

            return "Likes number extraction succeded!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Likes number extraction failed!";
    }

    private String manageLikeComment(String id_comment, String id_user){

        String like_comment_url="http://"+General.SERVER_IP+"/client/update_like.php";

        try{
            URL url=new URL(like_comment_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_comment","UTF-8")+"="+URLEncoder.encode(id_comment,"UTF-8")+"&"+
                    URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(id_user,"UTF-8")+"&"+
                    URLEncoder.encode("id_company","UTF-8")+"="+URLEncoder.encode(CompanyDashboardActivity.CURRENT_COMPANY_ID,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            if(getLikesNumber(id_comment,id_user).equals("Likes number extraction failed!"))
                return "Like update failed!";

            return "Like update succeded!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Like update failed!";

    }

    private String manageRating(String id_user, String company_id, String stars){
        if(deleteStars(id_user,company_id).equals("Connection problem!"))
            return "Connection problem!";

        if(insertStars(id_user,company_id,stars).equals("Connection problem!"))
            return "Connection problem!";

        String update_company_grade_url="http://"+General.SERVER_IP+"/client/update_company_grade.php";


        try{
            URL url=new URL(update_company_grade_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String company_grade=getCompanyGrade(company_id);

            if(company_grade.equals("Connection problem!"))
                return "Connection problem!";

            String data= URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8")+"&"+
                    URLEncoder.encode("company_grade","UTF-8")+"="+URLEncoder.encode(company_grade,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            current_grade=company_grade;

            return "Company grade updated!"+stars;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem!";

    }

    private String deleteStars(String id_user, String company_id){

        String delete_stars_url="http://"+General.SERVER_IP+"/client/delete_current_stars.php";


        try{
            URL url=new URL(delete_stars_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(id_user,"UTF-8")+"&"+
                    URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            return "Stars deleted!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem!";
    }

    private String insertStars(String id_user, String company_id, String stars){

        String insert_stars_url="http://"+General.SERVER_IP+"/client/insert_current_stars.php";

        try{
            URL url=new URL(insert_stars_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String data= URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(id_user,"UTF-8")+"&"+
                    URLEncoder.encode("company_id","UTF-8")+"="+URLEncoder.encode(company_id,"UTF-8")+"&"+
                    URLEncoder.encode("stars","UTF-8")+"="+URLEncoder.encode(stars,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            return "Stars insertion succeded!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Connection problem!";
    }

    private String getCompanyGrade(String company_id){

        String get_company_grade_url="http://"+General.SERVER_IP+"/client/get_company_grade.php";

        try{
            URL url=new URL(get_company_grade_url);

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
            bufferedReader.close();
            IS.close();
            httpURLConnection.disconnect();

            if(response.equals(""))
                return "0.0";

            response = String.format("%1.1f", Double.parseDouble(response));

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

    private void configConnectionLostDialog(Dialog connectionLostDialog){
        connectionLostDialog.setContentView(R.layout.connection_lost_dialog);
        connectionLostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        connectionLostDialog.setCanceledOnTouchOutside(false);
        connectionLostDialog.show();
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


        return "Connection problem!";
    }

    private void highlightStars(int stars_number){
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

    private String insertNewComment(String id_company, String id_user, String comment){
        String insert_stars_url="http://"+General.SERVER_IP+"/client/insert_new_comment.php";

        try{
            URL url=new URL(insert_stars_url);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

            String id_comment=getMaxCommentId();

            if(id_comment.equals("getMaxCommentId failed!"))
                return "Comment insertion failed!";

            id_comment=String.valueOf(Integer.parseInt(id_comment)+1);

            String data= URLEncoder.encode("id_comment","UTF-8")+"="+URLEncoder.encode(id_comment,"UTF-8")+"&"+
                    URLEncoder.encode("id_company","UTF-8")+"="+URLEncoder.encode(id_company,"UTF-8")+"&"+
                    URLEncoder.encode("id_user","UTF-8")+"="+URLEncoder.encode(id_user,"UTF-8")+"&"+
                    URLEncoder.encode("comment","UTF-8")+"="+URLEncoder.encode(comment,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS=httpURLConnection.getInputStream();
            IS.close();
            httpURLConnection.disconnect();

            CompanyDashboardActivity.commentariesData.add(new InfoComment(id_comment,id_user,MainMenuActivity.CURRENT_USER_DATA[1],comment));

            return "Comment insertion succeded!";

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return "Comment insertion failed!";
    }

    private String getMaxCommentId(){

        String get_max_comment_id_url="http://"+General.SERVER_IP+"/client/get_max_comment_id.php";

        try{
            URL url=new URL(get_max_comment_id_url);

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

        return "getMaxCommentId failed!";
    }

    private List<InfoComment> getCommentariesData(String id_company){
        String get_commentaries_data_url="http://"+General.SERVER_IP+"/client/get_commentaries_data.php";

        List<InfoComment> commentariesData=new ArrayList<>();

        try{
            URL url=new URL(get_commentaries_data_url);

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
            BufferedReader br=new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));
            StringBuilder sb=new StringBuilder();

            String line=null;
            String result=null;

            while((line=br.readLine())!=null){
                sb.append(line+"\n");
            }
            result=sb.toString();

            if(!result.equals("")) {
                JSONArray ja = new JSONArray(result);
                JSONObject jo = null;

                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);

                    String username = getUsernameByUserId(jo.getString("id_user"));

                    if (username.equals(""))
                        return null;

                    commentariesData.add(new InfoComment(jo.getString("id_comment"), jo.getString("id_user"), username, jo.getString("comment_content")));

                }
            }

            br.close();
            IS.close();
            httpURLConnection.disconnect();

            return commentariesData;

        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

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

        return "";
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
                    ((MainMenuActivity) context).finish();
                    context.startActivity(new Intent(context, MainMenuActivity.class));
                }
                else{
                    ((CompanyDashboardActivity) context).finish();
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

}

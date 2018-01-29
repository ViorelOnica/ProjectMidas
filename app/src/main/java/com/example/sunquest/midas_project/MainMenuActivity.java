package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunQuest on 12/17/2017.
 */

public class MainMenuActivity extends AppCompatActivity {
    protected static int CURRENT_USER_ID;
    protected static String[] CURRENT_USER_DATA;
    protected static List<InfoCompany> COMPANY_DATA = new ArrayList<>();
    protected static ReviewBackgroundTask RATING_MANAGER_TASK;
    protected static String filter="grade";

    protected UserDataBackgroundTask getUserDataTask;
    protected CompanyBackgroundTask getCompanyDataTask;
    protected CompanyBackgroundTask filterGetCompanyDataTask;
    Dialog newCompanyDialog;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity_layout);

        ImageButton avatarImageButton=findViewById(R.id.avatarImageButton);
        TextView homeTextView=findViewById(R.id.homeTextView);
        TextView addCompanyTextView=findViewById(R.id.addCompanyTextView);
        TextView filterByTextView=findViewById(R.id.filterByTextView);

        avatarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog userMenuDialog = new Dialog(MainMenuActivity.this);
                configUserMenuDialogProperties(userMenuDialog);
                TextView accountSettingsTextView = (TextView) userMenuDialog.findViewById(R.id.accountSettingsTextView);
                TextView signOutTextView = (TextView) userMenuDialog.findViewById(R.id.signOutTextView);

                accountSettingsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            startActivity(new Intent(MainMenuActivity.this, AccountSettingsActivity.class));
                            userMenuDialog.dismiss();
                        }
                    }
                });

                signOutTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            MainMenuActivity.RATING_MANAGER_TASK.cancel(true);
                        }
                        catch(Exception e){}

                        try{
                            getUserDataTask.cancel(true);
                        }
                        catch(Exception e){}

                        try{
                            getCompanyDataTask.cancel(true);
                        }
                        catch(Exception e){}

                        try{
                            filterGetCompanyDataTask.cancel(true);
                        }
                        catch(Exception e){}

                        General.CONNECTION_BUFFERING=false;

                        finish();
                        startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
                    }
                });
            }
        });

        homeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    MainMenuActivity.RATING_MANAGER_TASK.cancel(true);
                }
                catch(Exception e){}

                try{
                    getUserDataTask.cancel(true);
                }
                catch(Exception e){}

                try{
                    getCompanyDataTask.cancel(true);
                }
                catch(Exception e){}

                try{
                    filterGetCompanyDataTask.cancel(true);
                }
                catch(Exception e){}

                General.CONNECTION_BUFFERING=false;

                finish();
                startActivity(new Intent(MainMenuActivity.this,MainMenuActivity.class));

            }
        });

        addCompanyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCompanyDialog=new Dialog(MainMenuActivity.this);
                configNewCompanyDialogProperties(newCompanyDialog);

                final CustomEditText companyNameEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.companyNameEditText);

                final CustomEditText revenueEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.revenueEditText);

                final Spinner currencySpinner=(Spinner)newCompanyDialog.findViewById(R.id.currencySpinner);
                ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(MainMenuActivity.this,R.array.currency_array, R.layout.spinner_text_properties);
                currencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_properties);
                currencySpinner.setAdapter(currencyAdapter);

                final CustomEditText employeesNumberEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.numberOfEmployeesEditText);

                final Spinner monthSpinner=(Spinner)newCompanyDialog.findViewById(R.id.monthSpinner);
                final ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(MainMenuActivity.this,R.array.month_array, R.layout.monthspinner_text_properties);
                monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_properties);
                monthSpinner.setAdapter(monthAdapter);

                final CustomEditText dayEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.dayEditText);
                final CustomEditText yearEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.yearEditText);

                final CustomEditText descriptionEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.descriptionEditText);

                final ImageButton saveButton=(ImageButton)newCompanyDialog.findViewById(R.id.saveButton);
                TextView saveText=(TextView)newCompanyDialog.findViewById(R.id.saveText);
                ImageButton cancelButton=(ImageButton)newCompanyDialog.findViewById(R.id.cancelButton);

                ImageView companyNameValidationSign=(ImageView)newCompanyDialog.findViewById(R.id.companyNameValidationSign);
                ImageView revenueValidationSign=(ImageView)newCompanyDialog.findViewById(R.id.revenueValidationSign);
                ImageView employeesNumberValidationSign=(ImageView)newCompanyDialog.findViewById(R.id.employeesNumberValidationSign);
                ImageView foundationDateValidationSign=(ImageView)newCompanyDialog.findViewById(R.id.foundationDateValidationSign);
                ImageView descriptionValidationSign=(ImageView)newCompanyDialog.findViewById(R.id.descriptionValidationSign);
                ImageButton avatarImageButton=(ImageButton)newCompanyDialog.findViewById(R.id.avatarImageButton);

                General.TRACK_NEWCOMPANY_CHANGES(companyNameEditText,revenueEditText,employeesNumberEditText,monthSpinner,dayEditText,yearEditText,descriptionEditText,companyNameValidationSign,revenueValidationSign,employeesNumberValidationSign,foundationDateValidationSign,descriptionValidationSign,saveButton,saveText);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING) {

                            TextView loginFailedMessageTextViewOnCreateAccountDialog = (TextView) newCompanyDialog.findViewById(R.id.connectionProblemMessageTextView);
                            loginFailedMessageTextViewOnCreateAccountDialog.setVisibility(View.INVISIBLE);

                            bufferingPopupOnRegisterCompany(newCompanyDialog);

                            String foundationDate = monthSpinner.getSelectedItem() + "-" + dayEditText.getText().toString() + "-" + yearEditText.getText().toString();
                            registerCompany(companyNameEditText, revenueEditText, currencySpinner.getSelectedItem().toString(), employeesNumberEditText, foundationDate, descriptionEditText, newCompanyDialog);
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newCompanyDialog.dismiss();
                    }
                });

                avatarImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage();
                    }
                });

            }
        });

        filterByTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog filterByDialog=new Dialog(MainMenuActivity.this);
                configFilterByDialog(filterByDialog);

                TextView gradeTextView=filterByDialog.findViewById(R.id.gradeTextView);
                TextView reviewsTextView=filterByDialog.findViewById(R.id.reviewsTextView);
                TextView myPostsTextView=filterByDialog.findViewById(R.id.myPostsTextView);
                TextView myRatingsTextView=filterByDialog.findViewById(R.id.myRatingsTextView);

                final ProgressBar bufferingProgressBar = filterByDialog.findViewById(R.id.bufferingProgressBar);

                gradeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            bufferingProgressBar.setVisibility(View.VISIBLE);
                            filter="grade";
                            getCompanyDataTask=new CompanyBackgroundTask(MainMenuActivity.this,filterByDialog);
                            getCompanyDataTask.execute("getCompanyData","grade","filter");
                        }
                    }
                });

                reviewsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            bufferingProgressBar.setVisibility(View.VISIBLE);
                            filter="reviews";
                            getCompanyDataTask=new CompanyBackgroundTask(MainMenuActivity.this,filterByDialog);
                            getCompanyDataTask.execute("getCompanyData","reviews","filter");
                        }
                    }
                });

                myPostsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            bufferingProgressBar.setVisibility(View.VISIBLE);
                            filter="myposts";
                            getCompanyDataTask=new CompanyBackgroundTask(MainMenuActivity.this,filterByDialog);
                            getCompanyDataTask.execute("getCompanyData","myposts","filter");
                        }
                    }
                });

                myRatingsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            bufferingProgressBar.setVisibility(View.VISIBLE);
                            filter="myratings";
                            getCompanyDataTask=new CompanyBackgroundTask(MainMenuActivity.this,filterByDialog);
                            getCompanyDataTask.execute("getCompanyData","myratings","filter");
                        }
                    }
                });

            }
        });

    }

    private void registerCompany(CustomEditText companyNameEditText, CustomEditText revenueEditText, String currency, CustomEditText numberOfEmployeesEditText, String foundationDate, CustomEditText descriptionEditText, final Dialog newCompanyDialog){
        String method="registerCompany";
        final CompanyBackgroundTask registerCompanyTask=new CompanyBackgroundTask(this,newCompanyDialog);
        registerCompanyTask.execute(method,companyNameEditText.getText().toString(),revenueEditText.getText().toString(),currency,numberOfEmployeesEditText.getText().toString(),foundationDate,descriptionEditText.getText().toString(),imageToString(bitmap));

        ImageButton cancelButton=(ImageButton)newCompanyDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCompanyTask.cancel(true);
                newCompanyDialog.dismiss();
            }
        });
    }

    private void bufferingPopupOnRegisterCompany(Dialog newCompanyDialog){
        RelativeLayout connectionBufferingArea=(RelativeLayout)newCompanyDialog.findViewById(R.id.bufferingArea);
        connectionBufferingArea.setVisibility(View.VISIBLE);
    }

    private void configUserMenuDialogProperties(Dialog userMenuDialog){
        userMenuDialog.setContentView(R.layout.user_menu_dialog);
        userMenuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        userMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        userMenuDialog.show();

        //This changes menu layout position (without these, it would be centered)
        Window window=userMenuDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.START|Gravity.TOP;
        wlp.x=13;
        wlp.y=120;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

    }

    private void configFilterByDialog(Dialog configFilterByDialog){
        configFilterByDialog.setContentView(R.layout.filter_by_dialog);
        configFilterByDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        configFilterByDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        configFilterByDialog.show();

        //This changes menu layout position (without these, it would be centered)
        Window window=configFilterByDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.START|Gravity.BOTTOM;
        wlp.x=13;
        wlp.y=100;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

    }

    private void configNewCompanyDialogProperties(Dialog newCompanyDialog){
        newCompanyDialog.setContentView(R.layout.new_company_dialog);
        newCompanyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        newCompanyDialog.setCanceledOnTouchOutside(false);
        newCompanyDialog.show();
    }

    private void extractingDataBufferingPopup(){
        RelativeLayout connectionBufferingArea=(RelativeLayout)findViewById(R.id.extractingDataBufferingArea);
        connectionBufferingArea.setVisibility(View.VISIBLE);
    }

    private void selectImage(){
        if(!General.CONNECTION_BUFFERING) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){

            Uri path=data.getData();

            ImageButton saveButton=(ImageButton)newCompanyDialog.findViewById(R.id.saveButton);
            TextView saveText=(TextView)newCompanyDialog.findViewById(R.id.saveText);

            try {

                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                ImageButton avatarImageButtonFromUserAvatarDialog=(ImageButton)newCompanyDialog.findViewById(R.id.avatarImageButton);
                avatarImageButtonFromUserAvatarDialog.setImageBitmap(bitmap);

                General.logoInserted=true;

                if(General.validation1&&General.validation2&&General.validation3&&General.validation4&&General.validation5&&General.logoInserted)
                    General.CONFIG_BUTTON(saveButton,saveText,true);
                else
                    General.CONFIG_BUTTON(saveButton,saveText,false);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        extractingDataBufferingPopup();

        getUserDataTask=new UserDataBackgroundTask(this);
        getUserDataTask.execute("getUserData");

        getCompanyDataTask=new CompanyBackgroundTask(this);
        getCompanyDataTask.execute("getCompanyData",filter,"nofilter");

    }

}

//asdfdsfdafdsafds
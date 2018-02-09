package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
 * Created by SunQuest on 12/28/2017.
 */

public class CompanyDashboardActivity extends AppCompatActivity {
    protected static String CURRENT_COMPANY_ID;
    protected static InfoCompany COMPANY_DASHBOARD_DATA;
    ReviewBackgroundTask ratingTask;
    Dialog newCompanyDialog;
    Bitmap bitmap;
    CompanyBackgroundTask companyDashboardDataTask;
    ReviewBackgroundTask newCommentTask;
    CompanyBackgroundTask removeCompanyTask;
    static List<InfoComment> commentariesData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_dashboard_activity_layout);

        COMPANY_DASHBOARD_DATA=new InfoCompany();

        ImageView logo=(ImageView)findViewById(R.id.logo);

        final ImageView star1=(ImageView)findViewById(R.id.star1);
        final ImageView star2=(ImageView)findViewById(R.id.star2);
        final ImageView star3=(ImageView)findViewById(R.id.star3);
        final ImageView star4=(ImageView)findViewById(R.id.star4);
        final ImageView star5=(ImageView)findViewById(R.id.star5);

        final ProgressBar bufferingProgressBar=(ProgressBar)findViewById(R.id.bufferingProgressBar);

        final TextView grade=(TextView)findViewById(R.id.companyGrade);

        TextView removeTextView=findViewById(R.id.removeTextView);
        TextView editTextView=findViewById(R.id.editTextView);

        bufferingProgressBar.setVisibility(View.VISIBLE);

        companyDashboardDataTask=new CompanyBackgroundTask(this);
        companyDashboardDataTask.execute("getCompanyDashboardData",CURRENT_COMPANY_ID);

        String path="http://"+General.SERVER_IP+"/client/uploads/company"+CURRENT_COMPANY_ID+".png";
        Picasso.with(this)
                .load(path)
                .placeholder(R.drawable.no_photo)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(logo);

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING){

                    bufferingProgressBar.setVisibility(View.VISIBLE);

                    ratingTask=new ReviewBackgroundTask(CompanyDashboardActivity.this,bufferingProgressBar,star1,star2,star3,star4,star5,grade);
                    ratingTask.execute("manageRating","1",MainMenuActivity.CURRENT_USER_DATA[0],CURRENT_COMPANY_ID);

                }
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    bufferingProgressBar.setVisibility(View.VISIBLE);

                    ratingTask=new ReviewBackgroundTask(CompanyDashboardActivity.this,bufferingProgressBar,star1,star2,star3,star4,star5,grade);
                    ratingTask.execute("manageRating","2",MainMenuActivity.CURRENT_USER_DATA[0],CURRENT_COMPANY_ID);

                }
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    bufferingProgressBar.setVisibility(View.VISIBLE);

                    ratingTask=new ReviewBackgroundTask(CompanyDashboardActivity.this,bufferingProgressBar,star1,star2,star3,star4,star5,grade);
                    ratingTask.execute("manageRating","3",MainMenuActivity.CURRENT_USER_DATA[0],CURRENT_COMPANY_ID);

                }
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    bufferingProgressBar.setVisibility(View.VISIBLE);

                    ratingTask=new ReviewBackgroundTask(CompanyDashboardActivity.this,bufferingProgressBar,star1,star2,star3,star4,star5,grade);
                    ratingTask.execute("manageRating","4",MainMenuActivity.CURRENT_USER_DATA[0],CURRENT_COMPANY_ID);

                }
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    bufferingProgressBar.setVisibility(View.VISIBLE);

                    ratingTask=new ReviewBackgroundTask(CompanyDashboardActivity.this,bufferingProgressBar,star1,star2,star3,star4,star5,grade);
                    ratingTask.execute("manageRating","5",MainMenuActivity.CURRENT_USER_DATA[0],CURRENT_COMPANY_ID);

                }
            }
        });


        ImageButton avatarImageButton=(ImageButton)findViewById(R.id.avatarImageButton);
        TextView homeTextView=(TextView)findViewById(R.id.homeTextView);
        TextView addCompanyTextView=(TextView)findViewById(R.id.addCompanyTextView);

        path="http://"+General.SERVER_IP+"/client/uploads/user"+String.valueOf(MainMenuActivity.CURRENT_USER_ID)+".png";
        if(MainMenuActivity.CURRENT_USER_DATA[8].equals("yes")){
            Picasso.with(this)
                    .load(path)
                    .placeholder(R.drawable.avatar_icon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(avatarImageButton);
        }

        avatarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog userMenuDialog = new Dialog(CompanyDashboardActivity.this);
                configUserMenuDialogProperties(userMenuDialog);
                TextView accountSettingsTextView = (TextView) userMenuDialog.findViewById(R.id.accountSettingsTextView);
                TextView signOutTextView = (TextView) userMenuDialog.findViewById(R.id.signOutTextView);
                accountSettingsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            startActivity(new Intent(CompanyDashboardActivity.this, AccountSettingsActivity.class));
                            userMenuDialog.dismiss();
                        }
                    }
                });

                signOutTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try{
                            ratingTask.cancel(true);
                        }
                        catch(Exception e){}

                        try{
                            companyDashboardDataTask.cancel(true);
                        }
                        catch(Exception e){}

                        try{
                            removeCompanyTask.cancel(true);
                        }
                        catch(Exception e){}

                        General.CONNECTION_BUFFERING=false;

                        finish();
                        startActivity(new Intent(CompanyDashboardActivity.this, LoginActivity.class));
                    }
                });
            }
        });

        homeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        addCompanyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCompanyDialog=new Dialog(CompanyDashboardActivity.this);
                configNewCompanyDialogProperties(newCompanyDialog);

                final CustomEditText companyNameEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.companyNameEditText);

                final CustomEditText revenueEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.revenueEditText);

                final Spinner currencySpinner=(Spinner)newCompanyDialog.findViewById(R.id.currencySpinner);
                ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(CompanyDashboardActivity.this,R.array.currency_array, R.layout.spinner_text_properties);
                currencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_properties);
                currencySpinner.setAdapter(currencyAdapter);

                final CustomEditText employeesNumberEditText=(CustomEditText)newCompanyDialog.findViewById(R.id.numberOfEmployeesEditText);

                final Spinner monthSpinner=(Spinner)newCompanyDialog.findViewById(R.id.monthSpinner);
                final ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(CompanyDashboardActivity.this,R.array.month_array, R.layout.monthspinner_text_properties);
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

        ImageButton commentariesPopupIcon=(ImageButton)findViewById(R.id.popupIcon);
        commentariesPopupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog commentariesDialog=new Dialog(CompanyDashboardActivity.this);
                configCommentariesDialog(commentariesDialog);

                if(!General.CONNECTION_BUFFERING) {
                    manageCommentSection(commentariesDialog);
                }

            }
        });

        removeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!General.CONNECTION_BUFFERING){
                    final Dialog confirmationDialog=new Dialog(CompanyDashboardActivity.this);
                    configConfirmationDialog(confirmationDialog);

                    TextView topTextView=confirmationDialog.findViewById(R.id.topTextView);
                    ImageButton yesButton=confirmationDialog.findViewById(R.id.yesImageButton);
                    ImageButton cancelButton=confirmationDialog.findViewById(R.id.cancelImageButton);

                    topTextView.setText("Remove "+CompanyDashboardActivity.COMPANY_DASHBOARD_DATA.company_name+ "?");

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmationDialog.dismiss();
                        }
                    });

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bufferingPopupOnConfirmationDialog(confirmationDialog);
                            removeCompanyTask=new CompanyBackgroundTask(CompanyDashboardActivity.this, confirmationDialog);
                            removeCompanyTask.execute("removeCompany");
                        }
                    });

                }
            }
        });

        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CompanyDashboardActivity.this, "edit",Toast.LENGTH_LONG).show();
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

    private void bufferingPopupOnConfirmationDialog(Dialog confirmationDialog){
        ProgressBar bufferingProgressBar=confirmationDialog.findViewById(R.id.bufferingProgressBar);
        bufferingProgressBar.setVisibility(View.VISIBLE);
    }

    private void configConfirmationDialog(Dialog confirmationDialog){
        confirmationDialog.setContentView(R.layout.confirmation_dialog);
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        confirmationDialog.setCanceledOnTouchOutside(false);
        confirmationDialog.show();
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

    private void configNewCompanyDialogProperties(Dialog newCompanyDialog){
        newCompanyDialog.setContentView(R.layout.new_company_dialog);
        newCompanyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        newCompanyDialog.setCanceledOnTouchOutside(false);
        newCompanyDialog.show();
    }

    private void configCommentariesDialog(Dialog commentariesDialog){
        commentariesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        commentariesDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        commentariesDialog.show();

        Window window=commentariesDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.height= WindowManager.LayoutParams.MATCH_PARENT;
        wlp.width=WindowManager.LayoutParams.MATCH_PARENT;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
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

    protected void manageCommentSection(final Dialog commentariesDialog){

        commentariesDialog.setContentView(R.layout.commentaries_dialog);

        ReviewBackgroundTask getCommentariesDataTask=new ReviewBackgroundTask(CompanyDashboardActivity.this,commentariesDialog);
        getCommentariesDataTask.execute("getCommentaries",CURRENT_COMPANY_ID);

        final CustomEditText writeCommentEditText=(CustomEditText)commentariesDialog.findViewById(R.id.writeCommentEditText);
        ImageButton sendButton=(ImageButton)commentariesDialog.findViewById(R.id.sendImageButton);

        CustomEditText.CONFIG_EDIT_TEXT(writeCommentEditText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    if(writeCommentEditText.getText().toString().equals(""))
                        Toast.makeText(CompanyDashboardActivity.this,"Empty comment!",Toast.LENGTH_LONG).show();
                    else {
                        ProgressBar bufferingProgressBar = (ProgressBar) commentariesDialog.findViewById(R.id.postingBufferingProgressBar);
                        ImageButton sendButton = (ImageButton) commentariesDialog.findViewById(R.id.sendImageButton);

                        sendButton.setImageResource(R.drawable.send_icon_disabled);

                        bufferingProgressBar.setVisibility(View.VISIBLE);

                        newCommentTask = new ReviewBackgroundTask(CompanyDashboardActivity.this, bufferingProgressBar, commentariesDialog, view);
                        newCommentTask.execute("newComment", CURRENT_COMPANY_ID, String.valueOf(MainMenuActivity.CURRENT_USER_ID), writeCommentEditText.getText().toString());


                    }

                }
            }
        });

        commentariesDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {

                try{
                    newCommentTask.cancel(true);
                }
                catch(Exception e){}

                try{
                    CommentariesListAdapter.likeManagerTask.cancel(true);
                }
                catch(Exception e){}

            }
        });

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

        try{
            ratingTask.cancel(true);
        }
        catch(Exception e){}

        try{
            companyDashboardDataTask.cancel(true);
        }
        catch(Exception e){}

        try{
            removeCompanyTask.cancel(true);
        }
        catch(Exception e){}

        General.CONNECTION_BUFFERING=false;

        super.onBackPressed();
    }
}
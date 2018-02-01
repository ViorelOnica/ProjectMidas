package com.example.sunquest.midas_project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SunQuest on 12/17/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {
    Dialog userAvatarDialog;
    static Bitmap bitmap;
    UploadBackgroundTask userAvatarUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings_activity_layout);

        final TextView usernameTextView=(TextView)findViewById(R.id.usernameTextView);
        ImageButton avatarImageButton=(ImageButton)findViewById(R.id.avatarImageButton);
        RelativeLayout birthdateArea=(RelativeLayout) findViewById(R.id.birthdateArea);
        RelativeLayout currentCityArea=(RelativeLayout) findViewById(R.id.currentCityArea);
        RelativeLayout phoneNumberArea=(RelativeLayout) findViewById(R.id.phoneNumberArea);
        final RelativeLayout editPasswordArea=(RelativeLayout)findViewById(R.id.editPasswordArea);

        ImageButton doneImageButton=(ImageButton)findViewById(R.id.doneButton);

        usernameTextView.setText(MainMenuActivity.CURRENT_USER_DATA[1]);

        String path="http://"+General.SERVER_IP+"/client/uploads/user"+String.valueOf(MainMenuActivity.CURRENT_USER_ID)+".png";
        if(MainMenuActivity.CURRENT_USER_DATA[8].equals("yes")){
            Picasso.with(this)
                    .load(path)
                    .placeholder(R.drawable.no_image_icon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(avatarImageButton);
        }

        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog editUsernameDialog=new Dialog(AccountSettingsActivity.this);
                configEditUsernameDialog(editUsernameDialog);

                final CustomEditText usernameEditText=(CustomEditText)editUsernameDialog.findViewById(R.id.usernameEditText);
                ImageButton saveButton=(ImageButton)editUsernameDialog.findViewById(R.id.saveButton);
                TextView saveText=(TextView)editUsernameDialog.findViewById(R.id.saveText);
                ImageButton cancelButton=(ImageButton)editUsernameDialog.findViewById(R.id.cancelButton);
                ImageView usernameValidationSign=(ImageView)editUsernameDialog.findViewById(R.id.usernameValidationSign);

                usernameEditText.setText(MainMenuActivity.CURRENT_USER_DATA[1]);

                General.TRACK_USERNAME_CHANGES(usernameEditText,usernameValidationSign,saveButton,saveText,true);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING) {

                            TextView editingFailedMessageTextView = (TextView) editUsernameDialog.findViewById(R.id.editingFailedMessageTextView);
                            editingFailedMessageTextView.setVisibility(View.INVISIBLE);

                            editingBufferingPopupOnEditDialog(editUsernameDialog);

                            updateUsername(usernameEditText, editUsernameDialog);

                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editUsernameDialog.dismiss();
                    }
                });
            }
        });

        avatarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    userAvatarDialog = new Dialog(AccountSettingsActivity.this);
                    configUserAvatarDialog(userAvatarDialog);

                    ImageButton avatarImageButtonFromUserAvatarDialog = (ImageButton) userAvatarDialog.findViewById(R.id.avatarImageButton);
                    ImageButton uploadButton = (ImageButton) userAvatarDialog.findViewById(R.id.saveButton);
                    ImageButton cancelButton = (ImageButton) userAvatarDialog.findViewById(R.id.cancelButton);

                    avatarImageButtonFromUserAvatarDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!General.CONNECTION_BUFFERING)
                                selectImage();
                        }
                    });

                    uploadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!General.CONNECTION_BUFFERING) {

                                TextView uploadFailedTextView=(TextView)userAvatarDialog.findViewById(R.id.uploadFailedMessageTextView);
                                uploadFailedTextView.setVisibility(View.INVISIBLE);

                                RelativeLayout uploadingBufferingArea=(RelativeLayout)userAvatarDialog.findViewById(R.id.uploadingBufferingArea);
                                uploadingBufferingArea.setVisibility(View.VISIBLE);

                                userAvatarUploadTask=new UploadBackgroundTask(AccountSettingsActivity.this,userAvatarDialog);
                                userAvatarUploadTask.execute("uploadUserAvatar", String.valueOf(MainMenuActivity.CURRENT_USER_ID), imageToString(bitmap));
                            }
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try{
                                userAvatarUploadTask.cancel(true);
                            }
                            catch(Exception e){}

                            General.CONNECTION_BUFFERING=false;
                            userAvatarDialog.dismiss();

                        }
                    });
                }

            }
        });

        birthdateArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog editBirthdateDialog=new Dialog(AccountSettingsActivity.this);

                configEditBirthdateDialog(editBirthdateDialog);

                final Spinner monthSpinner=(Spinner)editBirthdateDialog.findViewById(R.id.monthSpinner);
                ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(AccountSettingsActivity.this,R.array.month_array, R.layout.monthspinner_text_properties);
                monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_text_properties);
                monthSpinner.setAdapter(monthAdapter);

                final CustomEditText dayEditText=(CustomEditText)editBirthdateDialog.findViewById(R.id.dayEditText);
                final CustomEditText yearEditText=(CustomEditText)editBirthdateDialog.findViewById(R.id.yearEditText);

                ImageButton saveButton=(ImageButton)editBirthdateDialog.findViewById(R.id.saveButton);
                TextView saveText=(TextView) editBirthdateDialog.findViewById(R.id.saveText);
                ImageButton cancelButton=(ImageButton)editBirthdateDialog.findViewById(R.id.cancelButton);

                ImageView birthdateValidationSign=(ImageView)editBirthdateDialog.findViewById(R.id.birthdateValidationSign);

                if(!MainMenuActivity.CURRENT_USER_DATA[5].equals(" ")){
                    String[] dateValues=MainMenuActivity.CURRENT_USER_DATA[5].split("-");
                    monthSpinner.setSelection(((ArrayAdapter<String>)monthSpinner.getAdapter()).getPosition(dateValues[0]));
                    dayEditText.setText(dateValues[1]);
                    yearEditText.setText(dateValues[2]);

                    General.TRACK_BIRTHDATE_CHANGES(monthSpinner,dayEditText,yearEditText,birthdateValidationSign,saveButton,saveText,true);
                }
                else
                    General.TRACK_BIRTHDATE_CHANGES(monthSpinner,dayEditText,yearEditText,birthdateValidationSign,saveButton,saveText,false);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING) {

                            TextView editingFailedMessageTextView = (TextView) editBirthdateDialog.findViewById(R.id.editingFailedMessageTextView);
                            editingFailedMessageTextView.setVisibility(View.INVISIBLE);

                            editingBufferingPopupOnEditDialog(editBirthdateDialog);

                            String birthdate = monthSpinner.getSelectedItem() + "-" + dayEditText.getText().toString() + "-" + yearEditText.getText().toString();

                            updateBirthday(birthdate, editBirthdateDialog);

                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editBirthdateDialog.dismiss();
                    }
                });

            }
        });

        currentCityArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog editCurrentCityDialog=new Dialog(AccountSettingsActivity.this);
                configEditCurrentCityDialog(editCurrentCityDialog);

                final CustomEditText currentCityEditText=(CustomEditText)editCurrentCityDialog.findViewById(R.id.currentCityEditText);
                ImageButton saveButton=(ImageButton)editCurrentCityDialog.findViewById(R.id.saveButton);
                TextView saveText=(TextView)editCurrentCityDialog.findViewById(R.id.saveText);
                ImageButton cancelButton=(ImageButton)editCurrentCityDialog.findViewById(R.id.cancelButton);

                ImageView currentCityValidationSign=(ImageView)editCurrentCityDialog.findViewById(R.id.currentCityValidationSign);

                if(!MainMenuActivity.CURRENT_USER_DATA[6].equals(" ")) {
                    currentCityEditText.setText(MainMenuActivity.CURRENT_USER_DATA[6]);
                    General.TRACK_CURRENTCITY_CHANGES(currentCityEditText,currentCityValidationSign,saveButton,saveText,true);
                }
                else
                    General.TRACK_CURRENTCITY_CHANGES(currentCityEditText,currentCityValidationSign,saveButton,saveText,false);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING) {

                            TextView editingFailedMessageTextView = (TextView) editCurrentCityDialog.findViewById(R.id.editingFailedMessageTextView);
                            editingFailedMessageTextView.setVisibility(View.INVISIBLE);

                            editingBufferingPopupOnEditDialog(editCurrentCityDialog);

                            updateCurrentCity(currentCityEditText, editCurrentCityDialog);

                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editCurrentCityDialog.dismiss();
                    }
                });

            }
        });

        phoneNumberArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog editPhoneNumberDialog=new Dialog(AccountSettingsActivity.this);
                configEditPhoneNumberDialog(editPhoneNumberDialog);

                final CustomEditText phoneNumberEditText=(CustomEditText)editPhoneNumberDialog.findViewById(R.id.phoneNumberEditText);
                ImageButton saveButton=(ImageButton)editPhoneNumberDialog.findViewById(R.id.saveButton);
                TextView saveText=(TextView)editPhoneNumberDialog.findViewById(R.id.saveText);
                ImageButton cancelButton=(ImageButton)editPhoneNumberDialog.findViewById(R.id.cancelButton);

                ImageView phoneNumberValidationSign=(ImageView)editPhoneNumberDialog.findViewById(R.id.phoneNumberValidationSign);

                if(!MainMenuActivity.CURRENT_USER_DATA[7].equals(" ")) {
                    phoneNumberEditText.setText(MainMenuActivity.CURRENT_USER_DATA[7]);
                    General.TRACK_PHONENUMBER_CHANGES(phoneNumberEditText,phoneNumberValidationSign,saveButton,saveText,true);
                }
                else
                    General.TRACK_PHONENUMBER_CHANGES(phoneNumberEditText,phoneNumberValidationSign,saveButton,saveText,false);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING) {

                            TextView editingFailedMessageTextView = (TextView) editPhoneNumberDialog.findViewById(R.id.editingFailedMessageTextView);
                            editingFailedMessageTextView.setVisibility(View.INVISIBLE);

                            editingBufferingPopupOnEditDialog(editPhoneNumberDialog);

                            updatePhoneNumber(phoneNumberEditText, editPhoneNumberDialog);

                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editPhoneNumberDialog.dismiss();
                    }
                });

            }
        });

        editPasswordArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog editPasswordDialog=new Dialog(AccountSettingsActivity.this);
                configEditPasswordDialog(editPasswordDialog);

                CustomEditText oldPasswordEditText=(CustomEditText)editPasswordDialog.findViewById(R.id.oldPasswordEditText);
                final CustomEditText newPasswordEditText=(CustomEditText)editPasswordDialog.findViewById(R.id.newPasswordEditText);
                CustomEditText confirmNewPasswordEditText=(CustomEditText)editPasswordDialog.findViewById(R.id.confirmNewPasswordEditText);

                ImageView oldPasswordValidationSign=(ImageView)editPasswordDialog.findViewById(R.id.oldPasswordValidationSign);
                ImageView newPasswordValidationSign=(ImageView)editPasswordDialog.findViewById(R.id.newPasswordValidationSign);
                ImageView confirmNewPasswordValidationSign=(ImageView)editPasswordDialog.findViewById(R.id.confirmNewPasswordValidationSign);


                ImageButton saveButton=(ImageButton)editPasswordDialog.findViewById(R.id.saveButton);
                TextView saveText=(TextView)editPasswordDialog.findViewById(R.id.saveText);
                ImageButton cancelButton=(ImageButton)editPasswordDialog.findViewById(R.id.cancelButton);

                General.TRACK_NEWPASSWORD_CHANGES(oldPasswordEditText,newPasswordEditText,confirmNewPasswordEditText,oldPasswordValidationSign,newPasswordValidationSign,confirmNewPasswordValidationSign,saveButton,saveText);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING) {

                            TextView editingFailedMessageTextView = (TextView) editPasswordDialog.findViewById(R.id.editingFailedMessageTextView);
                            editingFailedMessageTextView.setVisibility(View.INVISIBLE);

                            editingBufferingPopupOnEditDialog(editPasswordDialog);

                            updatePassword(newPasswordEditText, editPasswordDialog);

                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editPasswordDialog.dismiss();
                    }
                });

            }
        });

        doneImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                onBackPressed();

            }
        });

    }

    private void updateUsername(CustomEditText usernameEditText, final Dialog editUsernameDialog){
        String method="updateUsername";
        final UserDataBackgroundTask updateUserDataTask=new UserDataBackgroundTask(this,editUsernameDialog);
        updateUserDataTask.execute(method,String.valueOf(MainMenuActivity.CURRENT_USER_ID),usernameEditText.getText().toString());


        ImageButton cancelButton=(ImageButton)editUsernameDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDataTask.cancel(true);
                General.CONNECTION_BUFFERING=false;
                editUsernameDialog.dismiss();
            }
        });
    }

    private void updateBirthday(String birthdate, final Dialog editBirthdateDialog){
        String method="updateBirthdate";
        final UserDataBackgroundTask updateUserDataTask=new UserDataBackgroundTask(this,editBirthdateDialog);
        updateUserDataTask.execute(method,String.valueOf(MainMenuActivity.CURRENT_USER_ID),birthdate);


        ImageButton cancelButton=(ImageButton)editBirthdateDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDataTask.cancel(true);
                General.CONNECTION_BUFFERING=false;
                editBirthdateDialog.dismiss();
            }
        });
    }

    private void updateCurrentCity(CustomEditText currentCityEditText, final Dialog editCurrentCityDialog){
        String method="updateCurrentCity";
        final UserDataBackgroundTask updateUserDataTask=new UserDataBackgroundTask(this,editCurrentCityDialog);
        updateUserDataTask.execute(method,String.valueOf(MainMenuActivity.CURRENT_USER_ID),currentCityEditText.getText().toString());


        ImageButton cancelButton=(ImageButton)editCurrentCityDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDataTask.cancel(true);
                General.CONNECTION_BUFFERING=false;
                editCurrentCityDialog.dismiss();
            }
        });
    }

    private void updatePhoneNumber(CustomEditText phoneNumberEditText, final Dialog editPhoneNumberDialog){
        String method="updatePhoneNumber";
        final UserDataBackgroundTask updateUserDataTask=new UserDataBackgroundTask(this,editPhoneNumberDialog);
        updateUserDataTask.execute(method,String.valueOf(MainMenuActivity.CURRENT_USER_ID),phoneNumberEditText.getText().toString());


        ImageButton cancelButton=(ImageButton)editPhoneNumberDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDataTask.cancel(true);
                General.CONNECTION_BUFFERING=false;
                editPhoneNumberDialog.dismiss();
            }
        });
    }

    private void updatePassword(CustomEditText passwordEditText, final Dialog editPasswordDialog){
        String method="updatePassword";
        final UserDataBackgroundTask updateUserDataTask=new UserDataBackgroundTask(this,editPasswordDialog);
        updateUserDataTask.execute(method,String.valueOf(MainMenuActivity.CURRENT_USER_ID),passwordEditText.getText().toString());


        ImageButton cancelButton=(ImageButton)editPasswordDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDataTask.cancel(true);
                General.CONNECTION_BUFFERING=false;
                editPasswordDialog.dismiss();
            }
        });
    }

    private void editingBufferingPopupOnEditDialog(Dialog editDialog){
        RelativeLayout editingBufferingArea=(RelativeLayout)editDialog.findViewById(R.id.editingBufferingArea);
        editingBufferingArea.setVisibility(View.VISIBLE);
    }

    private void configEditUsernameDialog(Dialog editUsernameDialog){
        editUsernameDialog.setContentView(R.layout.edit_username_dialog);
        editUsernameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editUsernameDialog.setCanceledOnTouchOutside(false);
        editUsernameDialog.show();
    }

    private void configUserAvatarDialog(Dialog userAvatarDialog){
        userAvatarDialog.setContentView(R.layout.edit_user_avatar_dialog);
        userAvatarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        userAvatarDialog.setCanceledOnTouchOutside(false);
        userAvatarDialog.show();
    }

    private void configEditBirthdateDialog(Dialog editBirthdateDialog){
        editBirthdateDialog.setContentView(R.layout.edit_birthdate_dialog);
        editBirthdateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editBirthdateDialog.setCanceledOnTouchOutside(false);
        editBirthdateDialog.show();
    }

    private void configEditCurrentCityDialog(Dialog editCurrentCityDialog){
        editCurrentCityDialog.setContentView(R.layout.edit_currentcity_dialog);
        editCurrentCityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editCurrentCityDialog.setCanceledOnTouchOutside(false);
        editCurrentCityDialog.show();
    }

    private void configEditPhoneNumberDialog(Dialog editPhoneNumberDialog){
        editPhoneNumberDialog.setContentView(R.layout.edit_phonenumber_dialog);
        editPhoneNumberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editPhoneNumberDialog.setCanceledOnTouchOutside(false);
        editPhoneNumberDialog.show();
    }

    private void configEditPasswordDialog(Dialog editPasswordDialog){
        editPasswordDialog.setContentView(R.layout.edit_password_dialog);
        editPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editPasswordDialog.setCanceledOnTouchOutside(false);
        editPasswordDialog.show();
    }

    private void selectImage(){
        if(!General.CONNECTION_BUFFERING) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){

            Uri path=data.getData();

            try {

                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                ImageButton avatarImageButtonFromUserAvatarDialog=(ImageButton)userAvatarDialog.findViewById(R.id.avatarImageButton);
                avatarImageButtonFromUserAvatarDialog.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    private void configConnectionLostDialog(Dialog connectionLostDialog){
        connectionLostDialog.setContentView(R.layout.connection_lost_dialog);
        connectionLostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        connectionLostDialog.setCanceledOnTouchOutside(false);
        connectionLostDialog.show();
    }

    private void onConnectionLost(){
        Dialog connectionLostDialog=new Dialog(AccountSettingsActivity.this);

        configConnectionLostDialog(connectionLostDialog);

        ImageButton reconnectButton=(ImageButton)connectionLostDialog.findViewById(R.id.reconnectImageButton);
        ImageButton exitButton=(ImageButton)connectionLostDialog.findViewById(R.id.exitImageButton);

        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AccountSettingsActivity.this.finish();
                AccountSettingsActivity.this.startActivity(new Intent(AccountSettingsActivity.this,AccountSettingsActivity.class));
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AccountSettingsActivity.this.finish();
                AccountSettingsActivity.this.startActivity(new Intent(AccountSettingsActivity.this,LoginActivity.class));
            }
        });
    }

}
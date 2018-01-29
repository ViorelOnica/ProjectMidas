package com.example.sunquest.midas_project;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        ImageButton createAccountImageButton=(ImageButton)findViewById(R.id.createAccountImageButton);
        TextView recoverTextView=(TextView)findViewById(R.id.recoverTextView);
        ImageButton loginImageButton=(ImageButton)findViewById(R.id.loginButton);

        final CustomEditText usernameEditText=(CustomEditText)findViewById(R.id.usernameEditText);
        final CustomEditText passwordEditText=(CustomEditText)findViewById(R.id.passwordEditText);

        CustomEditText.CONFIG_EDIT_TEXT(usernameEditText);
        CustomEditText.CONFIG_EDIT_TEXT(passwordEditText);

        final TextView loginFailedMessageTextView = (TextView)findViewById(R.id.loginFailedMessageTextView);

        createAccountImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginFailedMessageTextView.setVisibility(View.INVISIBLE);

                usernameEditText.setCursorVisible(false);
                passwordEditText.setCursorVisible(false);

                final Dialog createAccountDialog=new Dialog(LoginActivity.this);
                configCreateAccountDialogProperties(createAccountDialog);

                final CustomEditText usernameEditTextFromDialog=(CustomEditText)createAccountDialog.findViewById(R.id.usernameEditText);
                final CustomEditText passwordEditTextFromDialog=(CustomEditText)createAccountDialog.findViewById(R.id.passwordEditText);
                CustomEditText confirmPasswordEditTextFromDialog=(CustomEditText)createAccountDialog.findViewById(R.id.confirmPasswordEditText);
                final CustomEditText emailEditTextFromDialog=(CustomEditText)createAccountDialog.findViewById(R.id.emailEditText);

                CustomEditText.CONFIG_EDIT_TEXT(usernameEditTextFromDialog);
                CustomEditText.CONFIG_EDIT_TEXT(passwordEditTextFromDialog);
                CustomEditText.CONFIG_EDIT_TEXT(confirmPasswordEditTextFromDialog);
                CustomEditText.CONFIG_EDIT_TEXT(emailEditTextFromDialog);

                ImageView usernameValidationSign=(ImageView)createAccountDialog.findViewById(R.id.usernameValidationSign);
                ImageView passwordValidationSign=(ImageView)createAccountDialog.findViewById(R.id.passwordValidationSign);
                ImageView confirmPasswordValidationSign=(ImageView)createAccountDialog.findViewById(R.id.confirmPasswordValidationSign);
                ImageView emailValidationSign=(ImageView)createAccountDialog.findViewById(R.id.emailValidationSign);

                ImageButton createAccountImageButtonFromDialog=(ImageButton)createAccountDialog.findViewById(R.id.createAccountImageButton);
                TextView createAccountTextViewFromDialog=(TextView)createAccountDialog.findViewById(R.id.createAccountTextView);
                ImageButton cancelButton=(ImageButton)createAccountDialog.findViewById(R.id.cancelImageButton);

                General.TRACK_CHANGES(usernameEditTextFromDialog,passwordEditTextFromDialog,confirmPasswordEditTextFromDialog,emailEditTextFromDialog, createAccountImageButtonFromDialog, createAccountTextViewFromDialog, usernameValidationSign, passwordValidationSign, confirmPasswordValidationSign, emailValidationSign);

                createAccountImageButtonFromDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!General.CONNECTION_BUFFERING) {
                            TextView loginFailedMessageTextViewOnCreateAccountDialog = (TextView) createAccountDialog.findViewById(R.id.loginFailedMessageTextView);
                            loginFailedMessageTextViewOnCreateAccountDialog.setVisibility(View.INVISIBLE);

                            connectingBufferingPopupOnCreateAccountDialog(createAccountDialog);

                            userRegister(usernameEditTextFromDialog, passwordEditTextFromDialog, emailEditTextFromDialog, "user", createAccountDialog);
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createAccountDialog.dismiss();
                    }
                });

            }
        });

        recoverTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginFailedMessageTextView.setVisibility(View.INVISIBLE);

                final Dialog recoverDialog=new Dialog(LoginActivity.this);
                configRecoverDialogProperties(recoverDialog);

                CustomEditText emailEditText=(CustomEditText)recoverDialog.findViewById(R.id.emailEditText);
                ImageButton recoverButton=(ImageButton)recoverDialog.findViewById(R.id.recoverImageButton);
                ImageButton cancelButton=(ImageButton)recoverDialog.findViewById(R.id.cancelImageButton);

                CustomEditText.CONFIG_EDIT_TEXT(emailEditText);

                recoverButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!General.CONNECTION_BUFFERING){
                            //send recovery mail
                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recoverDialog.dismiss();
                    }
                });


            }
        });

        loginImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!General.CONNECTION_BUFFERING) {

                    loginFailedMessageTextView.setVisibility(View.INVISIBLE);

                    connectingBufferingPopupOnLoginActivity();

                    userLogin(usernameEditText, passwordEditText);

                    usernameEditText.setCursorVisible(false);
                    passwordEditText.setCursorVisible(false);

                    hideKeyboard();
                }
            }
        });

    }

    private void configCreateAccountDialogProperties(Dialog createAccountDialog){
        createAccountDialog.setContentView(R.layout.create_account_dialog);
        createAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        createAccountDialog.setCanceledOnTouchOutside(false);
        createAccountDialog.show();
    }

    private void configRecoverDialogProperties(Dialog recoverDialog){
        recoverDialog.setContentView(R.layout.recover_dialog);
        recoverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        recoverDialog.setCanceledOnTouchOutside(false);
        recoverDialog.show();
    }

    private void userRegister(CustomEditText usernameEditText, CustomEditText passwordEditText, CustomEditText emailEditText, String user_type, final Dialog createAccountDialog){
        String method="register";
        final LoginActivityBackgroundTask backgroundTask=new LoginActivityBackgroundTask(this,createAccountDialog);
        backgroundTask.execute(method,usernameEditText.getText().toString(),passwordEditText.getText().toString(),emailEditText.getText().toString(),user_type);

        ImageButton cancelButton=(ImageButton)createAccountDialog.findViewById(R.id.cancelImageButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccountDialog.dismiss();
                backgroundTask.cancel(true);
                General.CONNECTION_BUFFERING=false;

            }
        });

    }

    private void userLogin(CustomEditText usernameEditText, CustomEditText passwordEditText){
        String method="login";
        LoginActivityBackgroundTask backgroundTask=new LoginActivityBackgroundTask(this);
        backgroundTask.execute(method,usernameEditText.getText().toString(),passwordEditText.getText().toString());
    }

    private void connectingBufferingPopupOnLoginActivity(){
        RelativeLayout connectionBufferingArea=(RelativeLayout)findViewById(R.id.connectionBufferingArea);
        connectionBufferingArea.setVisibility(View.VISIBLE);
    }

    private void connectingBufferingPopupOnCreateAccountDialog(Dialog createAccountDialog){
        RelativeLayout connectionBufferingArea=(RelativeLayout)createAccountDialog.findViewById(R.id.connectionBufferingArea);
        connectionBufferingArea.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

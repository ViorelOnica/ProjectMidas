package com.example.sunquest.midas_project;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SunQuest on 12/16/2017.
 */

public class General {

    protected static String SERVER_IP="185.115.92.57";

    protected static boolean CONFIGURATION_CHANGED;

    protected static boolean validation1;
    protected static boolean validation2;
    protected static boolean validation3;
    protected static boolean validation4;
    protected static boolean validation5;
    protected static boolean logoInserted;

    protected static boolean validMonth;
    protected static boolean validDay;
    protected static boolean validYear;

    protected static boolean CONNECTION_BUFFERING=false;

    protected static boolean spinnerSelectionTest;

    protected static void CONFIG_BUTTON(ImageButton button, TextView text, boolean enable){
        if(enable==false) {
            button.setEnabled(false);
            button.setImageResource(R.drawable.rectangle_shape_red);
            text.setTextColor(Color.parseColor("#ad0000"));
        }
        else{
            button.setEnabled(true);
            button.setImageResource(R.drawable.rectangle_shape);
            text.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    protected static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    protected static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    protected static void TRACK_CHANGES(CustomEditText usernameEditText, final CustomEditText passwordEditText, final CustomEditText confirmPasswordEditText, CustomEditText emailEditText, final ImageButton createAccountImageButtonFromDialog, final TextView createAccountTextViewFromDialog, final ImageView usernameValidationSign, final ImageView passwordValidationSign, final ImageView confirmPasswordValidationSign, final ImageView emailValidationSign){

        validation1=false;
        validation2=false;
        validation3=false;
        validation4=false;

        CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,false);

        usernameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CONFIGURATION_CHANGED = true;
                for (i = 0; i < charSequence.length(); i++) {
                    if (((charSequence.charAt(i) >= 65 && charSequence.charAt(i) <= 90) || (charSequence.charAt(i) >= 97 && charSequence.charAt(i) <= 122) || (charSequence.charAt(i) >= 48 && charSequence.charAt(i) <= 57)) && charSequence.length() >= 3)
                        validation1=true;
                    else {
                        validation1=false;
                        break;
                    }
                }

                if(validation1){
                    usernameValidationSign.setVisibility(View.VISIBLE);
                    usernameValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    usernameValidationSign.setVisibility(View.VISIBLE);
                    usernameValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3&&validation4)
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,true);
                else
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,false);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CONFIGURATION_CHANGED = true;
                for (i = 0; i < charSequence.length(); i++) {
                    if (((charSequence.charAt(i) >= 65 && charSequence.charAt(i) <= 90) || (charSequence.charAt(i) >= 97 && charSequence.charAt(i) <= 122) || (charSequence.charAt(i) >= 48 && charSequence.charAt(i) <= 57)) && charSequence.length() >= 3)
                        validation2=true;
                    else {
                        validation2=false;
                        break;
                    }
                }

                if(validation2){
                    passwordValidationSign.setVisibility(View.VISIBLE);
                    passwordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    passwordValidationSign.setVisibility(View.VISIBLE);
                    passwordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(charSequence.toString().equals(confirmPasswordEditText.getText().toString())&&validation2)
                    validation3=true;
                else
                    validation3=false;

                if(validation2&&validation3)
                {
                    confirmPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmPasswordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    confirmPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmPasswordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3&&validation4)
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,true);
                else
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,false);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals(passwordEditText.getText().toString()))
                    validation3 = true;
                else
                    validation3 = false;

                if(validation3&&validation2)
                {
                    confirmPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmPasswordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    confirmPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmPasswordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3&&validation4)
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,true);
                else
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isValidEmail(charSequence))
                    validation4=true;
                else {
                    validation4 = false;
                }

                if(validation4)
                {
                    emailValidationSign.setVisibility(View.VISIBLE);
                    emailValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    emailValidationSign.setVisibility(View.VISIBLE);
                    emailValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3&&validation4)
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,true);

                else
                    CONFIG_BUTTON(createAccountImageButtonFromDialog,createAccountTextViewFromDialog,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    protected static void TRACK_USERNAME_CHANGES(final CustomEditText usernameEditText, final ImageView usernameValidationSign, final ImageButton saveButton, final TextView saveText, boolean status){

        validation1=status;

        CONFIG_BUTTON(saveButton,saveText,status);

        usernameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CONFIGURATION_CHANGED = true;
                for (i = 0; i < charSequence.length(); i++) {
                    if (((charSequence.charAt(i) >= 65 && charSequence.charAt(i) <= 90) || (charSequence.charAt(i) >= 97 && charSequence.charAt(i) <= 122) || (charSequence.charAt(i) >= 48 && charSequence.charAt(i) <= 57)) && charSequence.length() >= 3)
                        validation1=true;
                    else {
                        validation1=false;
                        break;
                    }
                }

                if(validation1){
                    CONFIG_BUTTON(saveButton,saveText,true);

                    usernameValidationSign.setVisibility(View.VISIBLE);
                    usernameValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    CONFIG_BUTTON(saveButton,saveText,false);

                    usernameValidationSign.setVisibility(View.VISIBLE);
                    usernameValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



    }

    protected static void TRACK_BIRTHDATE_CHANGES(Spinner monthSpinner, CustomEditText dayEditText, CustomEditText yearEditText, final ImageView birthdateValidationSign, final ImageButton saveButton, final TextView saveText, boolean status){

        CONFIG_BUTTON(saveButton,saveText,status);

        if(status) {
            validation1 = true;
            validation2 = true;
            validation3 = true;
        }
        else{
            validation1 = false;
            validation2 = false;
            validation3 = false;
        }

        spinnerSelectionTest=false;

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerSelectionTest) {
                    if (i != 0)
                        validation1 = true;
                    else
                        validation1 = false;

                    if (validation1 && validation2 && validation3) {
                        CONFIG_BUTTON(saveButton, saveText, true);

                        birthdateValidationSign.setVisibility(View.VISIBLE);
                        birthdateValidationSign.setImageResource(R.drawable.valid_v_sign);
                    } else {
                        CONFIG_BUTTON(saveButton, saveText, false);

                        birthdateValidationSign.setVisibility(View.VISIBLE);
                        birthdateValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    }
                }
                spinnerSelectionTest=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(isNumeric(charSequence.toString())&&Integer.parseInt(charSequence.toString())>=1&&Integer.parseInt(charSequence.toString())<=31)
                    validation2=true;
                else
                    validation2=false;

                if(validation1&&validation2&&validation3){
                    CONFIG_BUTTON(saveButton,saveText,true);

                    birthdateValidationSign.setVisibility(View.VISIBLE);
                    birthdateValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    CONFIG_BUTTON(saveButton,saveText,false);

                    birthdateValidationSign.setVisibility(View.VISIBLE);
                    birthdateValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        yearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isNumeric(charSequence.toString())&&Integer.parseInt(charSequence.toString())>=1890&&Integer.parseInt(charSequence.toString())<=2017)
                    validation3=true;
                else
                    validation3=false;

                if(validation1&&validation2&&validation3){
                    CONFIG_BUTTON(saveButton,saveText,true);

                    birthdateValidationSign.setVisibility(View.VISIBLE);
                    birthdateValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    CONFIG_BUTTON(saveButton,saveText,false);

                    birthdateValidationSign.setVisibility(View.VISIBLE);
                    birthdateValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected static void TRACK_CURRENTCITY_CHANGES(final CustomEditText currentCityEditText, final ImageView currentCityValidationSign, final ImageButton saveButton, final TextView saveText, boolean status){

        validation1=status;

        CONFIG_BUTTON(saveButton,saveText,status);

        currentCityEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CONFIGURATION_CHANGED = true;
                for (i = 0; i < charSequence.length(); i++) {
                    if (((charSequence.charAt(i) >= 65 && charSequence.charAt(i) <= 90) || (charSequence.charAt(i) >= 97 && charSequence.charAt(i) <= 122) || (charSequence.charAt(i) >= 48 && charSequence.charAt(i) <= 57)) && charSequence.length() >= 3)
                        validation1=true;
                    else {
                        validation1=false;
                        break;
                    }
                }

                if(validation1){
                    CONFIG_BUTTON(saveButton,saveText,true);

                    currentCityValidationSign.setVisibility(View.VISIBLE);
                    currentCityValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    CONFIG_BUTTON(saveButton,saveText,false);

                    currentCityValidationSign.setVisibility(View.VISIBLE);
                    currentCityValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



    }

    protected static void TRACK_PHONENUMBER_CHANGES(final CustomEditText phoneNumberEditText, final ImageView phoneNumberValidationSign, final ImageButton saveButton, final TextView saveText, boolean status){

        validation1=status;

        CONFIG_BUTTON(saveButton,saveText,status);

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                CONFIGURATION_CHANGED = true;

                if(isNumeric(charSequence.toString())&&charSequence.length()>=3)
                    validation1=true;
                else
                    validation1=false;

                if(validation1){
                    CONFIG_BUTTON(saveButton,saveText,true);

                    phoneNumberValidationSign.setVisibility(View.VISIBLE);
                    phoneNumberValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    CONFIG_BUTTON(saveButton,saveText,false);

                    phoneNumberValidationSign.setVisibility(View.VISIBLE);
                    phoneNumberValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

    }

    protected static void TRACK_NEWPASSWORD_CHANGES(final CustomEditText oldPasswordEditText, final CustomEditText newPasswordEditText, final CustomEditText confirmNewPasswordEditText, final ImageView oldPasswordValidationSign, final ImageView newPasswordValidationSign, final ImageView confirmNewPasswordValidationSign, final ImageButton saveButton, final TextView saveText){

        CONFIG_BUTTON(saveButton,saveText,false);

        validation1=false;
        validation2=false;
        validation3=false;

        oldPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals(MainMenuActivity.CURRENT_USER_DATA[2])) {
                    validation1 = true;

                    oldPasswordValidationSign.setVisibility(View.VISIBLE);
                    oldPasswordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else {
                    validation1 = false;

                    oldPasswordValidationSign.setVisibility(View.VISIBLE);
                    oldPasswordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                for (i = 0; i < charSequence.length(); i++) {
                    if (((charSequence.charAt(i) >= 65 && charSequence.charAt(i) <= 90) || (charSequence.charAt(i) >= 97 && charSequence.charAt(i) <= 122) || (charSequence.charAt(i) >= 48 && charSequence.charAt(i) <= 57)) && charSequence.length() >= 3)
                        validation2=true;
                    else {
                        validation2=false;
                        break;
                    }
                }

                if(validation2){
                    newPasswordValidationSign.setVisibility(View.VISIBLE);
                    newPasswordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    newPasswordValidationSign.setVisibility(View.VISIBLE);
                    newPasswordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(charSequence.toString().equals(confirmNewPasswordEditText.getText().toString())&&validation2)
                    validation3=true;
                else
                    validation3=false;

                if(validation2&&validation3)
                {
                    confirmNewPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmNewPasswordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    confirmNewPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmNewPasswordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirmNewPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals(newPasswordEditText.getText().toString()))
                    validation3 = true;
                else
                    validation3 = false;

                if(validation3&&validation2)
                {
                    confirmNewPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmNewPasswordValidationSign.setImageResource(R.drawable.valid_v_sign);
                }
                else{
                    confirmNewPasswordValidationSign.setVisibility(View.VISIBLE);
                    confirmNewPasswordValidationSign.setImageResource(R.drawable.invalid_x_sign);
                }

                if(validation1&&validation2&&validation3)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    protected static void TRACK_NEWCOMPANY_CHANGES(CustomEditText companyNameEditText, CustomEditText revenueEditText, CustomEditText employeesNumberEditText, Spinner monthSpinner, CustomEditText dayEditText, CustomEditText yearEditText, CustomEditText descriptionEditText, final ImageView companyNameValidationSign, final ImageView revenueValidationSign, final ImageView employeesNumberValidationSign, final ImageView foundationDateValidationSign, final ImageView descriptionValidationSign, final ImageButton saveButton, final TextView saveText){

        CONFIG_BUTTON(saveButton,saveText,false);

        spinnerSelectionTest=false;

        validation1=false;
        validation2=false;
        validation3=false;
        validation4=false;
        validation5=false;

        validMonth=false;
        validDay=false;
        validYear=false;

        logoInserted=false;

        companyNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                for (i = 0; i < charSequence.length(); i++) {
                    if (((charSequence.charAt(i) >= 65 && charSequence.charAt(i) <= 90) || (charSequence.charAt(i) >= 97 && charSequence.charAt(i) <= 122) || (charSequence.charAt(i) >= 48 && charSequence.charAt(i) <= 57)) && charSequence.length() >= 2)
                        validation1=true;
                    else {
                        validation1=false;
                        break;
                    }
                }

                if(validation1){
                    companyNameValidationSign.setImageResource(R.drawable.valid_v_sign);
                    companyNameValidationSign.setVisibility(View.VISIBLE);
                }
                else{
                    companyNameValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    companyNameValidationSign.setVisibility(View.VISIBLE);
                }

                if(validation1&&validation2&&validation3&&validation4&&validation5&&logoInserted)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        revenueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isNumeric(charSequence.toString())&&Integer.parseInt(charSequence.toString())>=0)
                {
                    validation2=true;

                    revenueValidationSign.setImageResource(R.drawable.valid_v_sign);
                    revenueValidationSign.setVisibility(View.VISIBLE);
                }
                else{
                    validation2=false;

                    revenueValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    revenueValidationSign.setVisibility(View.VISIBLE);
                }


                if(validation1&&validation2&&validation3&&validation4&&validation5&&logoInserted)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        employeesNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isNumeric(charSequence.toString())&&Integer.parseInt(charSequence.toString())>0)
                {
                    validation3=true;

                    employeesNumberValidationSign.setImageResource(R.drawable.valid_v_sign);
                    employeesNumberValidationSign.setVisibility(View.VISIBLE);
                }
                else{
                    validation3=false;

                    employeesNumberValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    employeesNumberValidationSign.setVisibility(View.VISIBLE);
                }


                if(validation1&&validation2&&validation3&&validation4&&validation5)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerSelectionTest) {
                    if (i != 0)
                        validMonth = true;
                    else
                        validMonth = false;

                    if (validMonth && validDay && validYear) {
                        validation4 = true;

                        foundationDateValidationSign.setImageResource(R.drawable.valid_v_sign);
                        foundationDateValidationSign.setVisibility(View.VISIBLE);
                    } else {
                        validation4 = false;

                        foundationDateValidationSign.setImageResource(R.drawable.invalid_x_sign);
                        foundationDateValidationSign.setVisibility(View.VISIBLE);
                    }

                    if (validation1 && validation2 && validation3 && validation4 && validation5&&logoInserted)
                        CONFIG_BUTTON(saveButton, saveText, true);
                    else
                        CONFIG_BUTTON(saveButton, saveText, false);
                }
                spinnerSelectionTest=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(isNumeric(charSequence.toString())&&Integer.parseInt(charSequence.toString())>=1&&Integer.parseInt(charSequence.toString())<=31)
                    validDay=true;
                else
                    validDay=false;

                if(validMonth&&validDay&&validYear){
                    validation4=true;

                    foundationDateValidationSign.setImageResource(R.drawable.valid_v_sign);
                    foundationDateValidationSign.setVisibility(View.VISIBLE);
                }
                else{
                    validation4=false;

                    foundationDateValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    foundationDateValidationSign.setVisibility(View.VISIBLE);
                }

                if(validation1&&validation2&&validation3&&validation4&&validation5&&logoInserted)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        yearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(isNumeric(charSequence.toString())&&Integer.parseInt(charSequence.toString())>=1500&&Integer.parseInt(charSequence.toString())<=2017)
                    validYear=true;
                else
                    validYear=false;

                if(validMonth&&validDay&&validYear){
                    validation4=true;

                    foundationDateValidationSign.setImageResource(R.drawable.valid_v_sign);
                    foundationDateValidationSign.setVisibility(View.VISIBLE);
                }
                else{
                    validation4=false;

                    foundationDateValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    foundationDateValidationSign.setVisibility(View.VISIBLE);
                }

                if(validation1&&validation2&&validation3&&validation4&&validation5)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=50&&charSequence.length()<=500) {
                    validation5 = true;

                    descriptionValidationSign.setImageResource(R.drawable.valid_v_sign);
                    descriptionValidationSign.setVisibility(View.VISIBLE);
                }
                else {
                    validation5 = false;

                    descriptionValidationSign.setImageResource(R.drawable.invalid_x_sign);
                    descriptionValidationSign.setVisibility(View.VISIBLE);
                }

                if(validation1&&validation2&&validation3&&validation4&&validation5&&logoInserted)
                    CONFIG_BUTTON(saveButton,saveText,true);
                else
                    CONFIG_BUTTON(saveButton,saveText,false);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


}

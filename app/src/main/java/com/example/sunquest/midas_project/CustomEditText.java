package com.example.sunquest.midas_project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

/**
 * Created by SunQuest on 12/4/2017.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    public CustomEditText(Context context) {

        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //This event is triggered everytime the user presses back key, so we hide the CustomEditText cursor everytime he does that
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            this.setCursorVisible(false);
        return super.dispatchKeyEvent(event);
    }

    //Shows and hides the cursor whenever you focus or lose focus over the EditText
    protected static void CONFIG_EDIT_TEXT(final CustomEditText customEditText){
        customEditText.setCursorVisible(false);
        customEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE)
                    customEditText.setCursorVisible(false);

                return false;
            }
        });

        customEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customEditText.setCursorVisible(true);
                return false;
            }
        });
    }
}
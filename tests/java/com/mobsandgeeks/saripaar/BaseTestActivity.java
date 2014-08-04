package com.mobsandgeeks.saripaar;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Required;

/**
 * Created by maxchursin on 8/3/14.
 */
public class BaseTestActivity extends Activity {

    @Required
    TextView textViewRequired;
    Validator validator;
    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = new LinearLayout(this);
        rootView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        textViewRequired = new TextView(this);
        addTestView(textViewRequired);
        setContentView(rootView);
        validator = new Validator(this);
    }

    protected void addTestView(TextView textViewRequired) {
        textViewRequired.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        rootView.addView(textViewRequired);
    }
}

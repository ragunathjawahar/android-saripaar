package com.mobsandgeeks.saripaar.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.tests.validation.HometownZipCode;

import java.util.List;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public class CustomAnnotationActivity extends Activity
        implements Validator.ValidationListener, View.OnClickListener {

    @HometownZipCode
    private EditText mZipCodeEditText;

    private TextView mResultTextView;
    private Button mSaripaarButton;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_annotation);

        // UI References
        mZipCodeEditText = (EditText) findViewById(R.id.zipCodeEditText);
        RadioButton registerAnnotationRadioButton =
            (RadioButton) findViewById(R.id.registerAnnotationRadioButton);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        mSaripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        registerAnnotationRadioButton.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Validator.register(HometownZipCode.class);
                    }
                }
            }
        );
        mSaripaarButton.setOnClickListener(this);
    }

    @Override
    public void onSuccess() {
        mResultTextView.setText("SUCCESS");
    }

    @Override
    public void onFailure(List<ValidationError> errors) {
        mResultTextView.setText("FAILURE");
    }

    @Override
    public void onClick(View v) {
        try {
            mValidator.validate();
        } catch (IllegalStateException e) {
            mResultTextView.setText("CRASH");
        }
    }

}

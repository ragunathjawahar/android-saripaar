package com.mobsandgeeks.saripaar.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.exception.ConversionException;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public class CustomViewDataAdapterActivity extends Activity
        implements Validator.ValidationListener, View.OnClickListener,
                CompoundButton.OnCheckedChangeListener {

    @Email
    private FloatLabeledEditText mEmailFloatLabeledEditText;

    private RadioButton mRegisterAdapterRadioButton;
    private TextView mResultTextView;
    private Button mSaripaarButton;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);

        // UI References
        mEmailFloatLabeledEditText =
            (FloatLabeledEditText) findViewById(R.id.emailFloatLabelEditText);
        mRegisterAdapterRadioButton = (RadioButton) findViewById(R.id.registerAdapterRadioButton);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        mSaripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        mSaripaarButton.setOnClickListener(this);
        mRegisterAdapterRadioButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onSuccess() {
        mResultTextView.setText("SUCCESS");
    }

    @Override
    public void onFailure(List<ValidationError> errors) {
        // We have just one field anyway
        mResultTextView.setText("EMAIL");
    }

    @Override
    public void onClick(View v) {
        try {
            mValidator.validate();
        } catch (UnsupportedOperationException e) {
            mResultTextView.setText("CRASH");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Validator.registerAdapter(FloatLabeledEditText.class,
                new ViewDataAdapter<FloatLabeledEditText, String>() {

                    @Override
                    public String getData(FloatLabeledEditText flet) throws ConversionException {
                        return flet.getEditText().getText().toString();
                    }
                }
            );
        }
    }
}

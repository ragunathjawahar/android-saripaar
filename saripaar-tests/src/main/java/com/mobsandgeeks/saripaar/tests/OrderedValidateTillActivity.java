package com.mobsandgeeks.saripaar.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Size;

import java.util.List;

/**
 * This {@link android.app.Activity} is used for testing the
 * {@link com.mobsandgeeks.saripaar.Validator#validateTill(android.view.View)} method on
 * 'ordered' fields.
 */
public class OrderedValidateTillActivity extends Activity
        implements Validator.ValidationListener, RadioGroup.OnCheckedChangeListener,
                View.OnFocusChangeListener {

    // Fields
    @NotEmpty(order = 1)
    private EditText mNameEditText;

    @NotEmpty(order = 2)
    private EditText mAddressEditText;

    @Email(order = 3)
    private EditText mEmailEditText;

    @NotEmpty(order = 4)
    @Size(order = 5, min = 10, max = 10)
    private EditText mPhoneEditText;

    private TextView mResultTextView;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_ordered_validate_till_before);

        // UI References
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mAddressEditText = (EditText) findViewById(R.id.addressEditText);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        RadioGroup modeRadioGroup = (RadioGroup) findViewById(R.id.modeRadioGroup);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        mNameEditText.setOnFocusChangeListener(this);
        mAddressEditText.setOnFocusChangeListener(this);
        mEmailEditText.setOnFocusChangeListener(this);
        mPhoneEditText.setOnFocusChangeListener(this);
        modeRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onSuccess() {
        mResultTextView.setText("SUCCESS");
    }

    @Override
    public void onFailure(List<ValidationError> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ValidationError error : errors) {
            TextView view = (TextView) error.getView();
            stringBuilder.append(view.getHint().toString().toUpperCase()).append(" ");
        }
        mResultTextView.setText(stringBuilder.toString());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.burstRadioButton:
                mValidator.setValidationMode(Validator.Mode.BURST);
                break;

            case R.id.immediateRadioButton:
                mValidator.setValidationMode(Validator.Mode.IMMEDIATE);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mValidator.validateTill(v);
        }
    }
}

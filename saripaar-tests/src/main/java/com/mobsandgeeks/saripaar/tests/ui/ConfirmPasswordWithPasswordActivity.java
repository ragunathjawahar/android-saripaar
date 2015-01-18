package com.mobsandgeeks.saripaar.tests.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.tests.R;

import java.util.List;


public class ConfirmPasswordWithPasswordActivity extends Activity
            implements View.OnClickListener, Validator.ValidationListener {

    @Password
    private EditText mPasswordEditText;

    @ConfirmPassword
    private EditText mConfirmPasswordEditText;

    private TextView mResultTextView;
    private Button mSaripaarButton;

    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password_with_password);

        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        mSaripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Event listeners
        mSaripaarButton.setOnClickListener(this);

        // Validation
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
    }

    @Override
    public void onClick(View v) {
        mValidator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        mResultTextView.setText(R.string.success);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        mResultTextView.setText(Common.getFailedFieldNames(errors));
    }
}

package com.mobsandgeeks.saripaar.tests.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Optional;
import com.mobsandgeeks.saripaar.tests.R;

import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class OptionalActivity extends Activity
        implements Validator.ValidationListener, View.OnClickListener {

    // Fields
    @Optional @Email EditText mEmailEditText;
    @Optional @Checked CheckBox mEmailUpdatesEditText;

    // Attributes
    private Validator mValidator;
    private TextView mResultTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional);

        // UI References
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mEmailUpdatesEditText = (CheckBox) findViewById(R.id.emailUpdatesCheckBox);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        Button saripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        saripaarButton.setOnClickListener(this);
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

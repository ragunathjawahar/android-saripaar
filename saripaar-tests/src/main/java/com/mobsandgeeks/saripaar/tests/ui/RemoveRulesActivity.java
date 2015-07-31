package com.mobsandgeeks.saripaar.tests.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.tests.R;

import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class RemoveRulesActivity extends Activity
        implements Validator.ValidationListener, View.OnClickListener {

    // Fields
    @Email
    private EditText mEmailEditText;

    private RadioButton mAddQuickRuleRadioButton;
    private RadioButton mRemoveRulesRadioButton;
    private TextView mResultTextView;
    private Button mSaripaarButton;

    // Attributes
    private Validator mValidator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_rules);

        // UI References
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mAddQuickRuleRadioButton = (RadioButton) findViewById(R.id.addQuickRuleRadioButton);
        mRemoveRulesRadioButton = (RadioButton) findViewById(R.id.removeRulesRadioButton);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        mSaripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);

        // Event listeners
        mSaripaarButton.setOnClickListener(this);
        mValidator.setValidationListener(this);
        mAddQuickRuleRadioButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mValidator.put(mEmailEditText, new QuickRule<EditText>() {
                            @Override
                            public boolean isValid(EditText editText) {
                                String email = editText.getText().toString();
                                return email.endsWith("mobsandgeeks.com");
                            }

                            @Override
                            public String getMessage(Context context) {
                                return "Only allow emails from \"mobsandgeeks.com\" :P";
                            }
                        });
                    }
                });

        mRemoveRulesRadioButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mValidator.removeRules(mEmailEditText);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        try {
            mValidator.validate();
        } catch (IllegalStateException e) {
            mResultTextView.setText(R.string.crash);
        }
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

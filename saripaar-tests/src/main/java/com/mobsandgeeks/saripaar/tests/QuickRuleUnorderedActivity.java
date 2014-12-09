package com.mobsandgeeks.saripaar.tests;

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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public class QuickRuleUnorderedActivity extends Activity
        implements Validator.ValidationListener, CompoundButton.OnCheckedChangeListener,
                View.OnClickListener {

    @NotEmpty
    private EditText mZipCodeEditText;
    private EditText mAirtelNumberEditText;

    private TextView mResultTextView;
    private RadioButton mUseQuickRuleRadioButton;
    private Button mSaripaarButton;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unordered_quick_rule);

        // UI References
        mZipCodeEditText = (EditText) findViewById(R.id.zipCodeEditText);
        mAirtelNumberEditText = (EditText) findViewById(R.id.airtelNumberEditText);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        mUseQuickRuleRadioButton = (RadioButton) findViewById(R.id.useQuickRuleRadioButton);
        mSaripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event Listeners
        mUseQuickRuleRadioButton.setOnCheckedChangeListener(this);
        mSaripaarButton.setOnClickListener(this);
    }

    @Override
    public void onValidationSucceeded() {
        mResultTextView.setText("SUCCESS");
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ValidationError error : errors) {
            EditText editText = (EditText) error.getView();
            stringBuilder.append(editText.getHint().toString().toUpperCase().replaceAll(" ", "_"))
                .append(" ");
        }
        mResultTextView.setText(stringBuilder.toString());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mValidator.put(mAirtelNumberEditText, new QuickRule<TextView>() {

            @Override
            public boolean isValid(TextView textView) {
                final String phoneNumber = textView.getText().toString().trim();
                return phoneNumber.length() == 10 && phoneNumber.startsWith("999");
            }

            @Override
            public String getMessage(Context context) {
                return "Not an Airtel number :(";
            }
        });
    }

    @Override
    public void onClick(View v) {
        mValidator.validate();
    }
}

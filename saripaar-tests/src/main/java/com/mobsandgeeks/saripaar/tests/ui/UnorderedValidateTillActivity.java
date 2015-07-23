/*
 * Copyright (C) 2014 Mobs & Geeks
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobsandgeeks.saripaar.tests.ui;

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
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.tests.R;

import java.util.List;

/**
 * This {@link android.app.Activity} is used for testing the
 * {@link com.mobsandgeeks.saripaar.Validator#validateTill(android.view.View)} method on
 * 'unordered' fields.
 */
public class UnorderedValidateTillActivity extends Activity
        implements Validator.ValidationListener, RadioGroup.OnCheckedChangeListener,
                View.OnFocusChangeListener {

    // Fields
    @NotEmpty
    private EditText mNameEditText;

    @NotEmpty
    private EditText mAddressEditText;

    @Email
    private EditText mEmailEditText;

    @NotEmpty
    @Length
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
    public void onValidationSucceeded() {
        mResultTextView.setText(R.string.success);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        mResultTextView.setText(Common.getFailedFieldNames(errors));
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
            try {
                mValidator.validateTill(v);
            } catch (IllegalStateException e) {
                mResultTextView.setText("CRASH");
            }
        }
    }
}

/*
 * Copyright (C) 2016 Mobs & Geeks
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.tests.R;

import java.util.List;

/**
 * This {@link Activity} is used for testing the
 * {@link Validator#validateInvisibleViews(boolean)} method.
 */
public class ValidateInvisibleViewsActivity extends Activity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
                Validator.ValidationListener {

    // Fields
    @NotEmpty
    private EditText mNameEditText;

    @NotEmpty
    private EditText mEmailEditText;

    private TextView mResultTextView;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_invisible_views);

        // View references
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        RadioButton showViewRadioButton = (RadioButton) findViewById(R.id.showRadioButton);
        RadioButton hideViewRadioButton = (RadioButton) findViewById(R.id.hideRadioButton);
        CheckBox validateHiddenViewsCheckBox =
                (CheckBox) findViewById(R.id.validateHiddenViewsCheckBox);
        Button saripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        showViewRadioButton.setOnCheckedChangeListener(this);
        hideViewRadioButton.setOnCheckedChangeListener(this);
        validateHiddenViewsCheckBox.setOnCheckedChangeListener(this);
        saripaarButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mValidator.validate();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        switch (compoundButton.getId()) {
            case R.id.showRadioButton:
                mEmailEditText.setVisibility(View.VISIBLE);
                break;

            case R.id.hideRadioButton:
                mEmailEditText.setVisibility(View.INVISIBLE);
                break;

            case R.id.validateHiddenViewsCheckBox:
                mValidator.validateInvisibleViews(checked);
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        mResultTextView.setText(R.string.success);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        mResultTextView.setText(R.string.failure);
    }
}

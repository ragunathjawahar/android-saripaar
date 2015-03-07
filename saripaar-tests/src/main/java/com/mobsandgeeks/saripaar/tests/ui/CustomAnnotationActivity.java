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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.tests.R;
import com.mobsandgeeks.saripaar.tests.ui.validation.HometownZipCode;

import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
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
                        Validator.registerAnnotation(HometownZipCode.class);
                    }
                }
            }
        );
        mSaripaarButton.setOnClickListener(this);
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
    public void onClick(View v) {
        try {
            mValidator.validate();
        } catch (IllegalStateException e) {
            mResultTextView.setText("CRASH");
        }
    }

}

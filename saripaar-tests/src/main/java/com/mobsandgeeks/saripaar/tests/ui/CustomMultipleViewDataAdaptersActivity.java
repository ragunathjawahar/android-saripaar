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
import android.widget.RadioButton;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.exception.ConversionException;
import com.mobsandgeeks.saripaar.tests.R;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class CustomMultipleViewDataAdaptersActivity extends Activity
        implements Validator.ValidationListener, View.OnClickListener,
                CompoundButton.OnCheckedChangeListener {

    @Email
    @Order(1)
    private FloatLabeledEditText mEmailFloatLabeledEditText;

    @Max(2000)
    @Order(2)
    private FloatLabeledEditText mMaxFloatLabeledEditText;

    private TextView mResultTextView;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_multiple_view_data_adapters);

        // UI References
        mEmailFloatLabeledEditText =
            (FloatLabeledEditText) findViewById(R.id.emailFloatLabelEditText);
        mMaxFloatLabeledEditText = (FloatLabeledEditText) findViewById(R.id.maxFloatLabelEditText);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        RadioButton registerEmailAdapterRadioButton =
            (RadioButton) findViewById(R.id.registerEmailAdapterRadioButton);
        RadioButton registerMaxAdapterRadioButton =
            (RadioButton) findViewById(R.id.registerMaxAdapterRadioButton);
        Button saripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validator
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        saripaarButton.setOnClickListener(this);
        registerEmailAdapterRadioButton.setOnCheckedChangeListener(this);
        registerMaxAdapterRadioButton.setOnCheckedChangeListener(this);
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
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            mResultTextView.setText("CRASH");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ViewDataAdapter viewDataAdapter;

            switch (buttonView.getId()) {
                case R.id.registerEmailAdapterRadioButton:
                    viewDataAdapter = new FletStringAdapter();
                    break;

                case R.id.registerMaxAdapterRadioButton:
                    viewDataAdapter = new FletIntegerAdapter();
                    break;

                default:
                    throw new RuntimeException("This should never happen.");
            }
            mValidator.registerAdapter(FloatLabeledEditText.class, viewDataAdapter);
        }
    }

    static class FletStringAdapter implements ViewDataAdapter<FloatLabeledEditText, String> {

        @Override
        public String getData(FloatLabeledEditText flet) throws ConversionException {
            return flet.getEditText().getText().toString();
        }
    }

    static class FletIntegerAdapter implements ViewDataAdapter<FloatLabeledEditText, Integer> {

        @Override
        public Integer getData(FloatLabeledEditText flet) throws ConversionException {
            String numberText = flet.getEditText().getText().toString().trim();
            int number;
            try {
                number = Integer.parseInt(numberText);
            } catch (NumberFormatException e) {
                String message = String.format(
                    "Unable to convert %s to Integer.", numberText);
                throw new ConversionException(message);
            }

            return number;
        }
    }

}

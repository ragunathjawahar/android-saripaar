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
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;
import com.mobsandgeeks.saripaar.tests.R;
import com.mobsandgeeks.saripaar.tests.ui.validation.To;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class CustomAnnotationWithAdapterActivity extends Activity
        implements Validator.ValidationListener, View.OnClickListener,
            CompoundButton.OnCheckedChangeListener {

    @To(50)
    private SeekBar mSeekBar;

    private RadioButton mRegisterAnnotationRadioButton;
    private TextView mResultTextView;
    private Button mSaripaarButton;

    // Attributes
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_annotation_with_adapter);

        // UI References
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mRegisterAnnotationRadioButton =
            (RadioButton) findViewById(R.id.registerAnnotationRadioButton);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
        mSaripaarButton = (Button) findViewById(R.id.saripaarButton);

        // Validation
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        // Event listeners
        mRegisterAnnotationRadioButton.setOnCheckedChangeListener(this);
        mSaripaarButton.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        try {
            mValidator.validate();
        } catch (IllegalStateException e) {
            mResultTextView.setText("CRASH");
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationSucceeded() {
        mResultTextView.setText(R.string.success);
    }

    @Override
    public void onValidationFailed(final List<ValidationError> errors) {
        mResultTextView.setText(R.string.failure);
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (isChecked) {
            SeekBarIntegerAdapter seekBarIntegerAdapter = new SeekBarIntegerAdapter();
            Validator.registerAnnotation(To.class, SeekBar.class, seekBarIntegerAdapter);
        }
    }

    static class SeekBarIntegerAdapter implements ViewDataAdapter<SeekBar, Integer> {

        @Override
        public Integer getData(final SeekBar seekBar) throws ConversionException {
            return seekBar.getProgress();
        }

        @Override
        public <T extends Annotation> boolean containsOptionalValue(
                final SeekBar view, final T ruleAnnotation) {
            return false;
        }
    }

}

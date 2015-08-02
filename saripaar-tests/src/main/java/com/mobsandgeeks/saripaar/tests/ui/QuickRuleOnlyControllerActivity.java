/*
 * Copyright (C) 2015 Mobs & Geeks
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
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.tests.R;

import java.util.List;


public class QuickRuleOnlyControllerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.activity_quick_rule_only, null);
        setContentView(view);

        // Controller
        new Controller(view);
    }

    static class Controller implements View.OnClickListener, Validator.ValidationListener {
        // UI
        private EditText mOneOnlyEditText;
        private TextView mResultTextView;
        private Button mSaripaarButton;

        // Attributes
        private Validator mValidator;

        Controller(final View rootView) {
            // UI
            mOneOnlyEditText = (EditText) rootView.findViewById(R.id.oneOnlyEditText);
            mResultTextView = (TextView) rootView.findViewById(R.id.resultTextView);
            mSaripaarButton = (Button) rootView.findViewById(R.id.saripaarButton);

            // Validation
            mValidator = new Validator(this);
            mValidator.setValidationListener(this);

            // Add a quick rule
            mValidator.put(mOneOnlyEditText, new QuickRule<TextView>() {

                @Override
                public boolean isValid(TextView textView) {
                    return "1".equals(textView.getText().toString());
                }

                @Override
                public String getMessage(Context context) {
                    return "Enter 1, nothing else.";
                }
            });

            // Event listeners
            mSaripaarButton.setOnClickListener(this);
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
            mResultTextView.setText(R.string.failure);
        }
    }
}

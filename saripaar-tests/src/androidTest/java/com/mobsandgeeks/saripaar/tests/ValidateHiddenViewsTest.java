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

package com.mobsandgeeks.saripaar.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ui.ValidateInvisibleViewsActivity;

public class ValidateHiddenViewsTest
        extends ActivityInstrumentationTestCase2<ValidateInvisibleViewsActivity> {

    // UI References
    private TextView mResultTextView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testSkipHidden_success() {
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.clickView(R.id.hideRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testValidateHidden_failure() {
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.clickView(R.id.hideRadioButton);
        EspressoHelper.clickView(R.id.validateHiddenViewsCheckBox);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_FAILURE, mResultTextView);
    }

    public void testValidateValidHiddenField_success() {
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.clickView(R.id.hideRadioButton);
        EspressoHelper.clickView(R.id.validateHiddenViewsCheckBox);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public ValidateHiddenViewsTest() {
        super(ValidateInvisibleViewsActivity.class);
    }

}

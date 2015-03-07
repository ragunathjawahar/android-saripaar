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

package com.mobsandgeeks.saripaar.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ui.RemoveRulesActivity;

public class RemoveRulesTest
        extends ActivityInstrumentationTestCase2<RemoveRulesActivity> {

    private TextView mResultTextView;

    public RemoveRulesTest() {
        super(RemoveRulesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testAddQuickRulesValidEmail_success() {
        EspressoHelper.type(R.id.emailEditText, "rj@mobsandgeeks.com");
        EspressoHelper.clickView(R.id.addQuickRuleRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testAddQuickRulesInvalidEmail_failure() {
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.clickView(R.id.addQuickRuleRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.FIELD_EMAIL, mResultTextView);
    }

    public void testRemoveRulesWithoutQuickRule_crash() {
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.clickView(R.id.removeRulesRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

    public void testRemoveRulesWithQuickRule_crash() {
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.clickView(R.id.addQuickRuleRadioButton);
        EspressoHelper.clickView(R.id.removeRulesRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }
}

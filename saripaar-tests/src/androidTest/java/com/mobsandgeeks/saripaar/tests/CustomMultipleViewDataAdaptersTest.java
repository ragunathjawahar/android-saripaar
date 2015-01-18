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

package com.mobsandgeeks.saripaar.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ui.CustomMultipleViewDataAdaptersActivity;

public class CustomMultipleViewDataAdaptersTest
        extends ActivityInstrumentationTestCase2<CustomMultipleViewDataAdaptersActivity> {

    private TextView mResultTextView;

    public CustomMultipleViewDataAdaptersTest() {
        super(CustomMultipleViewDataAdaptersActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void test0InvalidEmailNoEmailAdapterInvalidMaxNoMaxAdapter_crash() {
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

    public void test1InvalidEmailWithEmailAdapterInvalidMaxNoMaxAdapter_crash() {
        TestHelper.clickView(R.id.registerEmailAdapterRadioButton);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

    public void test2InvalidEmailWithEmailAdapterInvalidMaxWithMaxAdapter_failure() {
        TestHelper.clickView(R.id.registerEmailAdapterRadioButton);
        TestHelper.clickView(R.id.registerMaxAdapterRadioButton);
        TestHelper.clickView(R.id.saripaarButton);

        String result = String.format("%s %s", Constants.FIELD_EMAIL, Constants.FIELD_MAX);
        TestHelper.checkForText(result, mResultTextView);
    }

    public void test3ValidFieldsWithAdapters_success() {
        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        TestHelper.clickView(R.id.registerEmailAdapterRadioButton);

        TestHelper.type(R.id.maxEditText, "1947");
        TestHelper.clickView(R.id.registerMaxAdapterRadioButton);

        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

}

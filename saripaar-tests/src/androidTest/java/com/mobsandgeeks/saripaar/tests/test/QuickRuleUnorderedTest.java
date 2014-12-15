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

package com.mobsandgeeks.saripaar.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.R;
import com.mobsandgeeks.saripaar.tests.QuickRuleUnorderedActivity;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class QuickRuleUnorderedTest
        extends ActivityInstrumentationTestCase2<QuickRuleUnorderedActivity> {

    private TextView mResultTextView;

    public QuickRuleUnorderedTest() {
        super(QuickRuleUnorderedActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testInvalidZipCodeNoQuickRule_failure() {
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_ZIP_CODE, mResultTextView);
    }

    public void testValidZipCodeNoQuickRule_success() {
        TestHelper.type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testValidZipCodeAirtelNumberQuickRule_failure() {
        TestHelper.type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        TestHelper.clickView(R.id.useQuickRuleRadioButton);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_AIRTEL_NUMBER, mResultTextView);
    }

    public void testInvalidZipCodeInvalidAirtelNumberQuickRule_failure() {
        TestHelper.clickView(R.id.useQuickRuleRadioButton);
        TestHelper.clickView(R.id.saripaarButton);

        String result = String.format("%s %s",
            Constants.FIELD_ZIP_CODE, Constants.FIELD_AIRTEL_NUMBER);
        TestHelper.checkForText(result, mResultTextView);
    }

    public void testZipCodeAirtelNumberQuickRuleValid_success() {
        TestHelper.clickView(R.id.useQuickRuleRadioButton);
        TestHelper.type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        TestHelper.type(R.id.airtelNumberEditText, Constants.AIRTEL_NUMBER);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

}

/*
 * Copyright (C) 2014 Mobs and Geeks
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

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
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
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_ZIP_CODE);
    }

    public void testValidZipCodeNoQuickRule_success() {
        type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_SUCCESS);
    }

    public void testValidZipCodeAirtelNumberQuickRule_failure() {
        type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        clickView(R.id.useQuickRuleRadioButton);
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_AIRTEL_NUMBER);
    }

    public void testInvalidZipCodeInvalidAirtelNumberQuickRule_failure() {
        clickView(R.id.useQuickRuleRadioButton);
        clickView(R.id.saripaarButton);

        String result = String.format("%s %s",
            Constants.FIELD_ZIP_CODE, Constants.FIELD_AIRTEL_NUMBER);
        checkForText(result);
    }

    public void testZipCodeAirtelNumberQuickRuleValid_success() {
        clickView(R.id.useQuickRuleRadioButton);
        type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        type(R.id.airtelNumberEditText, Constants.AIRTEL_NUMBER);
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_SUCCESS);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void type(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    private void checkForText(String expectedText) {
        String actualText = mResultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }
}

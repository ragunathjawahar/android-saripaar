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

import com.mobsandgeeks.saripaar.tests.QuickRuleOrderedActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public class QuickRuleOrderedTest
        extends ActivityInstrumentationTestCase2<QuickRuleOrderedActivity> {

    private TextView mResultTextView;

    public QuickRuleOrderedTest() {
        super(QuickRuleOrderedActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testInvalidZipCodeInvalidEmailNoQuickRule_failure() {
        clickView(R.id.saripaarButton);
        String result = String.format("%s %s", Constants.FIELD_ZIP_CODE, Constants.FIELD_EMAIL);
        checkForText(result);
    }

    public void testAllInvalidWithQuickRule_failure() {
        clickView(R.id.useQuickRuleRadioButton);
        clickView(R.id.saripaarButton);
        String result = String.format("%s %s %s",
            Constants.FIELD_ZIP_CODE, Constants.FIELD_AIRTEL_NUMBER, Constants.FIELD_EMAIL);
        checkForText(result);
    }

    public void testAllValidButQuickRule_failure() {
        type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        type(R.id.emailEditText, Constants.EMAIL);
        clickView(R.id.useQuickRuleRadioButton);
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_AIRTEL_NUMBER);
    }

    public void testAllValidWithQuickRule_success() {
        type(R.id.zipCodeEditText, Constants.ZIP_CODE);
        type(R.id.airtelNumberEditText, Constants.AIRTEL_NUMBER);
        type(R.id.emailEditText, Constants.EMAIL);
        clickView(R.id.useQuickRuleRadioButton);
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

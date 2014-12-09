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

import com.mobsandgeeks.saripaar.tests.CustomMultipleViewDataAdaptersActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

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
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_CRASH);
    }

    public void test1InvalidEmailWithEmailAdapterInvalidMaxNoMaxAdapter_crash() {
        clickView(R.id.registerEmailAdapterRadioButton);
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_CRASH);
    }

    public void test2InvalidEmailWithEmailAdapterInvalidMaxWithMaxAdapter_failure() {
        clickView(R.id.registerEmailAdapterRadioButton);
        clickView(R.id.registerMaxAdapterRadioButton);
        clickView(R.id.saripaarButton);

        String result = String.format("%s %s", Constants.FIELD_EMAIL, Constants.FIELD_MAX);
        checkForText(result);
    }

    public void test3ValidFieldsWithAdapters_success() {
        type(R.id.emailEditText, Constants.EMAIL);
        clickView(R.id.registerEmailAdapterRadioButton);

        type(R.id.maxEditText, "1947");
        clickView(R.id.registerMaxAdapterRadioButton);

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

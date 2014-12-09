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

import com.mobsandgeeks.saripaar.tests.OrderedValidateBeforeActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class OrderedValidateBeforeTest
        extends ActivityInstrumentationTestCase2<OrderedValidateBeforeActivity> {

    // UI References
    private TextView mResultTextView;

    public OrderedValidateBeforeTest() {
        super(OrderedValidateBeforeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    /* ============================================================================
     *  BURST Mode
     * ============================================================================
     */
    public void testBurstValidateBeforeFirstField() {
        type(R.id.nameEditText, Constants.NAME);
        checkForText("");
    }

    public void testBurstValidateBeforeLastField() {
        type(R.id.phoneEditText, Constants.PHONE);
        String text = String.format("%s %s %s",
                Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        checkForText(text);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidateBeforeFirstField() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        checkForText("");
    }

    public void testImmediateValidateBeforeLastField() {
        clickView(R.id.immediateRadioButton);
        type(R.id.phoneEditText, Constants.PHONE);
        checkForText(Constants.FIELD_NAME);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void type(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    private void checkForText(String expectedText) {
        String actualText = mResultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }

    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

}

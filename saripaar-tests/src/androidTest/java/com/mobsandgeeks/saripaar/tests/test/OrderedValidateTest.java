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

import com.mobsandgeeks.saripaar.tests.OrderedValidateActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class OrderedValidateTest
        extends ActivityInstrumentationTestCase2<OrderedValidateActivity> {

    // UI References
    private TextView mResultTextView;

    public OrderedValidateTest() {
        super(OrderedValidateActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Get references
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    /* ============================================================================
     *  BURST Mode
     * ============================================================================
     */
    public void testBurstValidName() {
        String resultText = String.format("%s %s %s %s",
            Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        testField(R.id.nameEditText, Constants.NAME, resultText);
    }

    public void testBurstValidAddress() {
        String resultText = String.format("%s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        testField(R.id.addressEditText, Constants.ADDRESS, resultText);
    }

    public void testBurstValidEmail() {
        String resultText = String.format("%s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        testField(R.id.emailEditText, Constants.EMAIL, resultText);
    }

    public void testBurstValidPhone() {
        String resultText = String.format("%s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        testField(R.id.phoneEditText, Constants.PHONE, resultText);
    }

    public void testBurstValidNamePhone() {
        String resultText = String.format("%s %s", Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.phoneEditText, Constants.PHONE);
        clickView(R.id.saripaarButton);
        checkForText(resultText);
    }

    public void testBurstInvalidAll() {
        clickView(R.id.saripaarButton);
        String text = String.format("%s %s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        checkForText(text);
    }

    public void testBurstValidAll() {
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.addressEditText, Constants.ADDRESS);
        type(R.id.emailEditText, Constants.EMAIL);
        type(R.id.phoneEditText, Constants.PHONE);
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_SUCCESS);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateInvalidAll() {
        clickView(R.id.immediateRadioButton);
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_NAME);
    }

    public void testImmediateValidName() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_ADDRESS);
    }

    public void testImmediateValidNameAddress() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.addressEditText, Constants.ADDRESS);
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_EMAIL);
    }

    public void testImmediateValidNameAddressEmail() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.addressEditText, Constants.ADDRESS);
        type(R.id.emailEditText, Constants.EMAIL);
        clickView(R.id.saripaarButton);
        String text = String.format("%s %s", Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        checkForText(text);
    }

    public void testImmediateValidAll() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.addressEditText, Constants.ADDRESS);
        type(R.id.emailEditText, Constants.EMAIL);
        type(R.id.phoneEditText, Constants.PHONE);
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_SUCCESS);
    }

    public void testImmediateValidAddressEmail() {
        clickView(R.id.immediateRadioButton);
        type(R.id.addressEditText, Constants.ADDRESS);
        type(R.id.emailEditText, Constants.EMAIL);
        clickView(R.id.saripaarButton);
        checkForText(Constants.FIELD_NAME);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void testField(int viewId, String textToType, String text) {
        type(viewId, textToType);
        clickView(R.id.saripaarButton);
        checkForText(text);
    }

    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    private void type(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    private void checkForText(String expectedText) {
        String actualText = mResultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }
}

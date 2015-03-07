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

import com.mobsandgeeks.saripaar.tests.ui.OrderedValidateActivity;

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
    public void testBurstValidName_failure() {
        String resultText = String.format("%s %s %s %s",
            Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        testField(R.id.nameEditText, Constants.NAME, resultText);
    }

    public void testBurstValidAddress_failure() {
        String resultText = String.format("%s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        testField(R.id.addressEditText, Constants.ADDRESS, resultText);
    }

    public void testBurstValidEmail_failure() {
        String resultText = String.format("%s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        testField(R.id.emailEditText, Constants.EMAIL, resultText);
    }

    public void testBurstValidPhone_failure() {
        String resultText = String.format("%s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        testField(R.id.phoneEditText, Constants.PHONE, resultText);
    }

    public void testBurstValidNamePhone_failure() {
        String resultText = String.format("%s %s", Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(resultText, mResultTextView);
    }

    public void testBurstInvalidAll_failure() {
        EspressoHelper.clickView(R.id.saripaarButton);
        String text = String.format("%s %s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        EspressoHelper.checkForText(text, mResultTextView);
    }

    public void testBurstValidAll_success() {
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateInvalidAll_failure() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.FIELD_NAME, mResultTextView);
    }

    public void testImmediateValidName_failure() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);
    }

    public void testImmediateValidNameAddress_failure() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.FIELD_EMAIL, mResultTextView);
    }

    public void testImmediateValidNameAddressEmail_failure() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.clickView(R.id.saripaarButton);
        String text = String.format("%s %s", Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        EspressoHelper.checkForText(text, mResultTextView);
    }

    public void testImmediateValidAll_success() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testImmediateValidAddressEmail_failure() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.FIELD_NAME, mResultTextView);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void testField(int viewId, String textToType, String text) {
        EspressoHelper.type(viewId, textToType);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(text, mResultTextView);
    }
}

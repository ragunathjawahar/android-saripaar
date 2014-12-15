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

import com.mobsandgeeks.saripaar.tests.OrderedValidateActivity;
import com.mobsandgeeks.saripaar.tests.R;

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
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(resultText, mResultTextView);
    }

    public void testBurstInvalidAll_failure() {
        TestHelper.clickView(R.id.saripaarButton);
        String text = String.format("%s %s %s %s %s",
            Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL,
            Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        TestHelper.checkForText(text, mResultTextView);
    }

    public void testBurstValidAll_success() {
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateInvalidAll_failure() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_NAME, mResultTextView);
    }

    public void testImmediateValidName_failure() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);
    }

    public void testImmediateValidNameAddress_failure() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_EMAIL, mResultTextView);
    }

    public void testImmediateValidNameAddressEmail_failure() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        TestHelper.clickView(R.id.saripaarButton);
        String text = String.format("%s %s", Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        TestHelper.checkForText(text, mResultTextView);
    }

    public void testImmediateValidAll_success() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testImmediateValidAddressEmail_failure() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_NAME, mResultTextView);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void testField(int viewId, String textToType, String text) {
        TestHelper.type(viewId, textToType);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(text, mResultTextView);
    }
}

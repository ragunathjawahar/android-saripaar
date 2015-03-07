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

import com.mobsandgeeks.saripaar.tests.ui.UnorderedValidateActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnorderedValidateTest
        extends ActivityInstrumentationTestCase2<UnorderedValidateActivity> {

    // UI References
    private TextView mResultTextView;

    public UnorderedValidateTest() {
        super(UnorderedValidateActivity.class);
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
        testField(R.id.nameEditText, Constants.NAME, Constants.FIELD_NAME);
    }

    public void testBurstValidAddress() {
        testField(R.id.addressEditText, Constants.ADDRESS, Constants.FIELD_ADDRESS);
    }

    public void testBurstValidEmail() {
        testField(R.id.emailEditText, Constants.EMAIL, Constants.FIELD_EMAIL);
    }

    public void testBurstValidPhone() {
        testField(R.id.phoneEditText, Constants.PHONE, Constants.FIELD_PHONE);
    }

    public void testBurstValidAll() {
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        EspressoHelper.clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_SUCCESS));
    }

    public void testBurstInvalidAll() {
        EspressoHelper.clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.FIELD_NAME, Constants.FIELD_ADDRESS,
                Constants.FIELD_EMAIL, Constants.FIELD_PHONE));
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidAllCrash() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        EspressoHelper.clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_CRASH));
    }

    public void testImmediateInvalidAllCrash() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_CRASH));
    }

    public void testImmediateValidOneCrash() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_CRASH));
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void testField(int viewId, String textToType, String fieldToIgnore) {
        List<String> words = new ArrayList<String>(
            Arrays.asList(Constants.FIELD_NAME, Constants.FIELD_ADDRESS,
                Constants.FIELD_EMAIL, Constants.FIELD_PHONE));
        boolean removed = words.remove(fieldToIgnore);
        if (!removed) {
            String message = String.format("Words does not contain '%s'.", fieldToIgnore);
            throw new IllegalArgumentException(message);
        }
        EspressoHelper.type(viewId, textToType);
        EspressoHelper.clickView(R.id.saripaarButton);
        checkForText(words);
    }

    private void checkForText(List<String> words) {
        String resultText = mResultTextView.getText().toString();
        for (String word : words) {
            assertTrue(resultText.contains(word));
        }
    }
}

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
import com.mobsandgeeks.saripaar.tests.UnorderedValidateActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

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
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.addressEditText, Constants.ADDRESS);
        type(R.id.emailEditText, Constants.EMAIL);
        type(R.id.phoneEditText, Constants.PHONE);
        clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_SUCCESS));
    }

    public void testBurstInvalidAll() {
        clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.FIELD_NAME, Constants.FIELD_ADDRESS,
                Constants.FIELD_EMAIL, Constants.FIELD_PHONE));
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidAllCrash() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        type(R.id.addressEditText, Constants.ADDRESS);
        type(R.id.emailEditText, Constants.EMAIL);
        type(R.id.phoneEditText, Constants.PHONE);
        clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_CRASH));
    }

    public void testImmediateInvalidAllCrash() {
        clickView(R.id.immediateRadioButton);
        clickView(R.id.saripaarButton);
        checkForText(Arrays.asList(Constants.STATE_CRASH));
    }

    public void testImmediateValidOneCrash() {
        clickView(R.id.immediateRadioButton);
        type(R.id.nameEditText, Constants.NAME);
        clickView(R.id.saripaarButton);
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
        type(viewId, textToType);
        clickView(R.id.saripaarButton);
        checkForText(words);
    }

    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    private void type(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    private void checkForText(List<String> words) {
        String resultText = mResultTextView.getText().toString();
        for (String word : words) {
            assertTrue(resultText.contains(word));
        }
    }
}

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

import com.mobsandgeeks.saripaar.tests.ui.OrderedValidateBeforeActivity;

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
    public void testBurstValidateBeforeFirstField_nothing() {
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.checkForText("", mResultTextView);
    }

    public void testBurstValidateBeforeLastField_failure() {
        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        String text = String.format("%s %s %s",
                Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        TestHelper.checkForText(text, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidateBeforeFirstField_nothing() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.checkForText("", mResultTextView);
    }

    public void testImmediateValidateBeforeLastField_failure() {
        TestHelper.clickView(R.id.immediateRadioButton);
        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        TestHelper.checkForText(Constants.FIELD_NAME, mResultTextView);
    }

}

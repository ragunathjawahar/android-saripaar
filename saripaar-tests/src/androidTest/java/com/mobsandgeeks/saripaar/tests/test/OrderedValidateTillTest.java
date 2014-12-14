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

import com.mobsandgeeks.saripaar.tests.OrderedValidateTillActivity;
import com.mobsandgeeks.saripaar.tests.R;

public class OrderedValidateTillTest
        extends ActivityInstrumentationTestCase2<OrderedValidateTillActivity> {

    // UI References
    private TextView mResultTextView;

    public OrderedValidateTillTest() {
        super(OrderedValidateTillActivity.class);
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
    public void testBurstValidateTill() {
        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        String text = String.format("%s %s %s",
                Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        TestHelper.checkForText(text, mResultTextView);

        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.checkForText(Constants.FIELD_NAME, mResultTextView);

        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        text = String.format("%s %s %s",
                Constants.FIELD_ADDRESS, Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        TestHelper.checkForText(text, mResultTextView);

        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);

        TestHelper.clickView(R.id.nameEditText);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidateTill() {
        TestHelper.clickView(R.id.immediateRadioButton);

        TestHelper.type(R.id.emailEditText, Constants.EMAIL);
        TestHelper.checkForText(Constants.FIELD_NAME, mResultTextView);

        TestHelper.type(R.id.nameEditText, Constants.NAME);
        TestHelper.checkForText(Constants.FIELD_NAME, mResultTextView);

        TestHelper.type(R.id.phoneEditText, Constants.PHONE);
        TestHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);

        TestHelper.type(R.id.addressEditText, Constants.ADDRESS);
        TestHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);

        TestHelper.clickView(R.id.nameEditText);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

}

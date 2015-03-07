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

import com.mobsandgeeks.saripaar.tests.ui.OrderedValidateTillActivity;

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
        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        String text = String.format("%s %s %s",
                Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        EspressoHelper.checkForText(text, mResultTextView);

        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.checkForText(Constants.FIELD_NAME, mResultTextView);

        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        text = String.format("%s %s %s",
                Constants.FIELD_ADDRESS, Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        EspressoHelper.checkForText(text, mResultTextView);

        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);

        EspressoHelper.clickView(R.id.nameEditText);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidateTill() {
        EspressoHelper.clickView(R.id.immediateRadioButton);

        EspressoHelper.type(R.id.emailEditText, Constants.EMAIL);
        EspressoHelper.checkForText(Constants.FIELD_NAME, mResultTextView);

        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.checkForText(Constants.FIELD_NAME, mResultTextView);

        EspressoHelper.type(R.id.phoneEditText, Constants.PHONE);
        EspressoHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);

        EspressoHelper.type(R.id.addressEditText, Constants.ADDRESS);
        EspressoHelper.checkForText(Constants.FIELD_ADDRESS, mResultTextView);

        EspressoHelper.clickView(R.id.nameEditText);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

}

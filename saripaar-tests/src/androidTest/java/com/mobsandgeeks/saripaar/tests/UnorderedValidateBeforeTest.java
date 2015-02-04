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

import com.mobsandgeeks.saripaar.tests.ui.UnorderedValidateBeforeActivity;

public class UnorderedValidateBeforeTest
        extends ActivityInstrumentationTestCase2<UnorderedValidateBeforeActivity> {

    // UI References
    private TextView mResultTextView;

    public UnorderedValidateBeforeTest() {
        super(UnorderedValidateBeforeActivity.class);
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
    public void testBurstValidateBeforeFirstField_crash() {
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidateTill_crash() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.type(R.id.nameEditText, Constants.NAME);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }
}

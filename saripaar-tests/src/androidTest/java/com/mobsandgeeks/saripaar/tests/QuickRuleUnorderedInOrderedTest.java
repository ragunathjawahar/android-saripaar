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

import com.mobsandgeeks.saripaar.tests.ui.QuickRuleUnorderedInOrderedActivity;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class QuickRuleUnorderedInOrderedTest
        extends ActivityInstrumentationTestCase2<QuickRuleUnorderedInOrderedActivity> {

    private TextView mResultTextView;

    public QuickRuleUnorderedInOrderedTest() {
        super(QuickRuleUnorderedInOrderedActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testInvalidZipCodeInvalidAirtelNumberNoEvenNumberQuickRule_failure() {
        EspressoHelper.clickView(R.id.saripaarButton);
        String result = String.format("%s %s",
            Constants.FIELD_ZIP_CODE, Constants.FIELD_AIRTEL_NUMBER);
        EspressoHelper.checkForText(result, mResultTextView);
    }

    public void testInvalidZipCodeInvalidAirtelNumberWithEvenNumberQuickRule_crash() {
        EspressoHelper.clickView(R.id.useQuickRuleRadioButton);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

}

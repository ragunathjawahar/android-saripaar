/*
 * Copyright (C) 2015 Mobs & Geeks
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

import com.mobsandgeeks.saripaar.tests.ui.UnorderedSequencingActivity;

public class UnorderedSequencingTest
        extends ActivityInstrumentationTestCase2<UnorderedSequencingActivity> {

    // UI References
    private TextView mResultTextView;

    public UnorderedSequencingTest() {
        super(UnorderedSequencingActivity.class);
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
    public void testBurst_failure() {
        EspressoHelper.clickView(R.id.burstRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);

        String[] messages = {
                "Message 3", "Message 4", "Message 5",
                "Message 6", "Message 1", "Message 2",
                "Message 7"
        };
        StringBuilder messageBuilder = new StringBuilder();
        for (String message : messages) {
            messageBuilder.append(message).append('\n');
        }

        String expectedErrorMessages = messageBuilder.toString().trim();
        EspressoHelper.checkForText(expectedErrorMessages, mResultTextView);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediate_crash() {
        EspressoHelper.clickView(R.id.immediateRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

}

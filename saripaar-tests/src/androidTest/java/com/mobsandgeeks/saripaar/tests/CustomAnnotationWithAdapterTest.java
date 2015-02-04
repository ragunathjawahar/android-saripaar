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
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ui.CustomAnnotationWithAdapterActivity;

public class CustomAnnotationWithAdapterTest
        extends ActivityInstrumentationTestCase2<CustomAnnotationWithAdapterActivity> {

    private SeekBar mSeekBar;
    private TextView mResultTextView;

    public CustomAnnotationWithAdapterTest() {
        super(CustomAnnotationWithAdapterActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CustomAnnotationWithAdapterActivity activity = getActivity();

        mSeekBar = (SeekBar) activity.findViewById(R.id.seekBar);
        mResultTextView = (TextView) activity.findViewById(R.id.resultTextView);
    }

    public void test0NoRegisteredAdapter_crash() {
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

    public void test1RegisteredAdapterSeekBarAboveRange_failure() {
        mSeekBar.setProgress(60);
        EspressoHelper.clickView(R.id.registerAnnotationRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_FAILURE, mResultTextView);
    }

    public void test2RegisteredAdapterSeekBarAboveRange_success() {
        mSeekBar.setProgress(25);
        EspressoHelper.clickView(R.id.registerAnnotationRadioButton);
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }
}

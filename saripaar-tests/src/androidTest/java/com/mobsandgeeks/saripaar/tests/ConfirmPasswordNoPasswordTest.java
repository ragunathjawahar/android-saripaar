package com.mobsandgeeks.saripaar.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ui.ConfirmPasswordNoPasswordActivity;

public class ConfirmPasswordNoPasswordTest
        extends ActivityInstrumentationTestCase2<ConfirmPasswordNoPasswordActivity> {

    private TextView mResultTextView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testOnlyConfirmPassword_crash() {
        String password = "there_lies_MY-S3creT";
        TestHelper.type(R.id.confirmPasswordEditText, password);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_CRASH, mResultTextView);
    }

    public ConfirmPasswordNoPasswordTest() {
        super(ConfirmPasswordNoPasswordActivity.class);
    }
}

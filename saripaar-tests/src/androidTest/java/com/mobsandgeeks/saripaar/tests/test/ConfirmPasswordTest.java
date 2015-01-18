package com.mobsandgeeks.saripaar.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ConfirmPasswordWithPasswordActivity;
import com.mobsandgeeks.saripaar.tests.R;

public class ConfirmPasswordTest
        extends ActivityInstrumentationTestCase2<ConfirmPasswordWithPasswordActivity> {

    private TextView mResultTextView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testValidPasswordAndConfirmPassword_success() {
        String password = "there_lies_MY-S3creT";
        TestHelper.type(R.id.passwordEditText, password);
        TestHelper.type(R.id.confirmPasswordEditText, password);
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testPasswordMismatch_failure() {
        TestHelper.type(R.id.passwordEditText, "password_one");
        TestHelper.type(R.id.confirmPasswordEditText, "password_1");
        TestHelper.clickView(R.id.saripaarButton);
        TestHelper.checkForText(Constants.FIELD_CONFIRM_PASSWORD, mResultTextView);
    }

    public ConfirmPasswordTest() {
        super(ConfirmPasswordWithPasswordActivity.class);
    }
}

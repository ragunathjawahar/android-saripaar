package com.mobsandgeeks.saripaar.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.ui.OptionalActivity;

/**
 * @author Ragunath Jawahar
 */
public class OptionalTest extends ActivityInstrumentationTestCase2<OptionalActivity> {

    // UI References
    private TextView mResultTextView;

    public OptionalTest() {
        super(OptionalActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Get references
        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    public void testOptionalAll_success() {
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.STATE_SUCCESS, mResultTextView);
    }

    public void testOptionalInvalidEmail_failure() {
        EspressoHelper.type(R.id.emailEditText, "Joey");
        EspressoHelper.clickView(R.id.saripaarButton);
        EspressoHelper.checkForText(Constants.FIELD_EMAIL, mResultTextView);
    }
}

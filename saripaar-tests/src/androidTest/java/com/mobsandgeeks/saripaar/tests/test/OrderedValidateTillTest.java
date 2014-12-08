package com.mobsandgeeks.saripaar.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.OrderedValidateTillActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

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
        type(R.id.emailEditText, Constants.EMAIL);
        String text = String.format("%s %s %s",
                Constants.FIELD_NAME, Constants.FIELD_ADDRESS, Constants.FIELD_EMAIL);
        checkForText(text);

        type(R.id.nameEditText, Constants.NAME);
        checkForText(Constants.FIELD_NAME);

        type(R.id.phoneEditText, Constants.PHONE);
        text = String.format("%s %s %s",
                Constants.FIELD_ADDRESS, Constants.FIELD_PHONE, Constants.FIELD_PHONE);
        checkForText(text);

        type(R.id.addressEditText, Constants.ADDRESS);
        checkForText(Constants.FIELD_ADDRESS);

        clickView(R.id.nameEditText);
        checkForText(Constants.STATE_SUCCESS);
    }

    /* ============================================================================
     *  IMMEDIATE Mode
     * ============================================================================
     */
    public void testImmediateValidateTill() {
        clickView(R.id.immediateRadioButton);

        type(R.id.emailEditText, Constants.EMAIL);
        checkForText(Constants.FIELD_NAME);

        type(R.id.nameEditText, Constants.NAME);
        checkForText(Constants.FIELD_NAME);

        type(R.id.phoneEditText, Constants.PHONE);
        checkForText(Constants.FIELD_ADDRESS);

        type(R.id.addressEditText, Constants.ADDRESS);
        checkForText(Constants.FIELD_ADDRESS);

        clickView(R.id.nameEditText);
        checkForText(Constants.STATE_SUCCESS);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void type(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    private void checkForText(String expectedText) {
        String actualText = mResultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }

    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

}

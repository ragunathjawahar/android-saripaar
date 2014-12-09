package com.mobsandgeeks.saripaar.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.QuickRuleUnorderedInOrderedActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
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
        clickView(R.id.saripaarButton);
        String result = String.format("%s %s",
            Constants.FIELD_ZIP_CODE, Constants.FIELD_AIRTEL_NUMBER);
        checkForText(result);
    }

    public void testInvalidZipCodeInvalidAirtelNumberWithEvenNumberQuickRule_crash() {
        clickView(R.id.useQuickRuleRadioButton);
        checkForText(Constants.STATE_CRASH);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    private void checkForText(String expectedText) {
        String actualText = mResultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }
}

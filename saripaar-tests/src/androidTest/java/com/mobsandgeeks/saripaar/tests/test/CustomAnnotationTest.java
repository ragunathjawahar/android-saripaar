package com.mobsandgeeks.saripaar.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.tests.CustomAnnotationActivity;
import com.mobsandgeeks.saripaar.tests.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class CustomAnnotationTest
        extends ActivityInstrumentationTestCase2<CustomAnnotationActivity> {

    private TextView mResultTextView;

    public CustomAnnotationTest() {
        super(CustomAnnotationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResultTextView = (TextView) getActivity().findViewById(R.id.resultTextView);
    }

    // Using 'testX' prefix, because of static variables in Validator > Registry.
    public void test0UnregisteredAnnotationWithNoOtherRules_crash() {
        type(R.id.zipCodeEditText, "635001");
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_CRASH);
    }

    public void test1ValidZipCode_success() {
        clickView(R.id.registerAnnotationRadioButton);
        type(R.id.zipCodeEditText, "635001");
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_SUCCESS);
    }

    public void test2InvalidZipCode_failure() {
        clickView(R.id.registerAnnotationRadioButton);
        type(R.id.zipCodeEditText, "600018");
        clickView(R.id.saripaarButton);
        checkForText(Constants.STATE_FAILURE);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void type(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    private void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    private void checkForText(String expectedText) {
        String actualText = mResultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }
}

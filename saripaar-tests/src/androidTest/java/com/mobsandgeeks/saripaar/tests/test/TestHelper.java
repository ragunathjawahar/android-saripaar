package com.mobsandgeeks.saripaar.tests.test;

import android.widget.TextView;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

public class TestHelper {

    public static void type(final int viewId, final String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    public static void clickView(final int viewId) {
        onView(withId(viewId)).perform(click());
    }

    public static void checkForText(final String expectedText, final TextView resultTextView) {
        String actualText = resultTextView.getText().toString().trim();
        assertEquals(expectedText, actualText);
    }
}

package com.mobsandgeeks.saripaar;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import java.util.List;

public class ValidatorTest extends ActivityUnitTestCase<BaseTestActivity> {

    private Intent intent;

    public ValidatorTest() {
        super(BaseTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        intent = new Intent(getInstrumentation().getTargetContext(), BaseTestActivity.class);
    }

    public void testRequiredAnnotationPositive() {
        startActivity(intent, null, null);
        final BaseTestActivity activity = getActivity();
        activity.textViewRequired.setText("Text required");
        activity.validator.setValidationListener(new SuccessValidationListener() {
            @Override
            public void onFormPrepared() {
                activity.validator.validate();
            }
        });
    }

    public void testRequiredAnnotationNegative() {
        startActivity(intent, null, null);
        final BaseTestActivity activity = getActivity();
        activity.textViewRequired.setText(null);
        activity.validator.setValidationListener(new FailureValidationListener() {
            @Override
            public void onFormPrepared() {
                activity.validator.validate();
            }
        });
    }

    /**
     * Created by maxchursin on 8/3/14.
     */
    static abstract class SuccessValidationListener implements Validator.ValidationListener {

        @Override
        public void onValidationSucceeded() {
            assertTrue(true);
        }

        @Override
        public void onValidationFailed(List<ViewErrorPair> failedResults) {
            assertTrue(false);
        }

        @Override
        public void onServerMappingFinish(List<ViewErrorPair> mappingResults) {
            assertTrue(false);
        }
    }

    /**
     * Created by maxchursin on 8/3/14.
     */
    static abstract class FailureValidationListener implements Validator.ValidationListener {

        @Override
        public void onValidationSucceeded() {
            assertTrue(false);
        }

        @Override
        public void onValidationFailed(List<ViewErrorPair> failedResults) {
            assertTrue(true);
        }

        @Override
        public void onServerMappingFinish(List<ViewErrorPair> mappingResults) {
            assertTrue(false);
        }
    }
}
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
        getActivity().validator.setValidationListener(new SuccessValidationListener());
        waitAnnotationProcess();
        getActivity().textViewRequired.setText("Text required");
        getActivity().validator.validate();
    }

    public void testRequiredAnnotationNegative() {
        startActivity(intent, null, null);
        getActivity().validator.setValidationListener(new FailureValidationListener());
        waitAnnotationProcess();
        getActivity().textViewRequired.setText(null);
        getActivity().validator.validate();
    }

    private void waitAnnotationProcess() {
        try {
            //Giving time for annotations to process
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
    * Created by maxchursin on 8/3/14.
    */
    static class SuccessValidationListener implements Validator.ValidationListener {
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
    static class FailureValidationListener implements Validator.ValidationListener {
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
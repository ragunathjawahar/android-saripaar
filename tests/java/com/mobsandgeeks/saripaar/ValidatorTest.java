package com.mobsandgeeks.saripaar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.List;

public class ValidatorTest extends ActivityUnitTestCase<ValidatorTest.TestActivity> {

    private Fragment fragment;
    final String errorMessage = "Error";
    private Intent intent;

    public ValidatorTest() {
        super(ValidatorTest.TestActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        intent = new Intent(getInstrumentation().getTargetContext(), TestActivity.class);
    }

    public void testRequiredAnnotation() {
        startActivity(intent, null, null);
        getActivity().validator.setValidationListener(new Validator.ValidationListener() {
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
        });
        try {
            //Giving time for annotations to process
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getActivity().validator.validate();
    }

    public static class TestActivity extends Activity {
        @Required
        TextView textView;
        Validator validator;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            textView = new TextView(this);
            textView.setText(null);
            validator = new Validator(this);
        }
    }

}
package com.mobsandgeeks.saripaar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.List;

public class ValidatorTest extends ActivityUnitTestCase<ValidatorTest.TestActivity> {

    private Intent intent;

    public ValidatorTest() {
        super(ValidatorTest.TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        intent = new Intent(getInstrumentation().getTargetContext(), TestActivity.class);
    }

    public void testRequiredAnnotationPositive() {
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
        waitAnnotationProcess();
        getActivity().textView.setText("test");
        getActivity().validator.validate();
    }

    public void testRequiredAnnotationNegative() {
        startActivity(intent, null, null);
        getActivity().validator.setValidationListener(new Validator.ValidationListener() {
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
        });
        waitAnnotationProcess();
        getActivity().textView.setText(null);
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

    public static class TestActivity extends Activity {
        @Required
        TextView textView;
        Validator validator;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
            );
            textView = new TextView(this);
            view.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            view.addView(textView);
            setContentView(view);
            validator = new Validator(this);
        }
    }

}
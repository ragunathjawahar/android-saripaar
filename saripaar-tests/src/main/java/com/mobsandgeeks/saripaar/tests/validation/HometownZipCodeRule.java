package com.mobsandgeeks.saripaar.tests.validation;

import com.mobsandgeeks.saripaar.AnnotationRule;

public class HometownZipCodeRule extends AnnotationRule<HometownZipCode, String> {

    protected HometownZipCodeRule(HometownZipCode hometownZipCode) {
        super(hometownZipCode);
    }

    @Override
    public boolean isValid(String data) {
        return "635001".equals(data);
    }

}

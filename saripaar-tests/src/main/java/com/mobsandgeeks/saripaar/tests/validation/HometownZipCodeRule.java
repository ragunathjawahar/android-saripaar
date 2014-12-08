package com.mobsandgeeks.saripaar.tests.validation;

import com.mobsandgeeks.saripaar.Rule;

public class HometownZipCodeRule extends Rule<HometownZipCode, String> {

    protected HometownZipCodeRule(HometownZipCode hometownZipCode) {
        super(hometownZipCode);
    }

    @Override
    public boolean isValid(String data) {
        return "635001".equals(data);
    }

}

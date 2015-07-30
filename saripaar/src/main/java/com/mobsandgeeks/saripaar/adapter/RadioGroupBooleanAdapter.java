package com.mobsandgeeks.saripaar.adapter;

import android.view.View;
import android.widget.RadioGroup;

import com.mobsandgeeks.saripaar.exception.ConversionException;


/**
 * Adapter that returns a {@link java.lang.Boolean} value from a {@link android.widget.RadioGroup}.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class RadioGroupBooleanAdapter implements ViewDataAdapter<RadioGroup, Boolean> {

    @Override
    public Boolean getData(RadioGroup radioGroup) throws ConversionException {
        return radioGroup.getCheckedRadioButtonId() != View.NO_ID;
    }
}

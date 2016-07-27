/*
 * Copyright (C) 2014 Mobs & Geeks
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobsandgeeks.saripaar.adapter;

import android.widget.TextView;

import com.mobsandgeeks.saripaar.exception.ConversionException;

/**
 * Adapter parses and returns a {@link java.lang.Double} from {@link android.widget.TextView}s or
 * its subclasses like {@link android.widget.EditText}s.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class TextViewDoubleAdapter extends TextViewBaseAdapter<Double> {
    private static final String REGEX_DECIMAL = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";

    @Override
    public Double getData(final TextView textView) throws ConversionException {
        String doubleString = textView.getText().toString().trim();
        if (!doubleString.matches(REGEX_DECIMAL)) {
            String message = String.format("Expected a floating point number, but was %s",
                doubleString);
            throw new ConversionException(message);
        }

        return Double.parseDouble(doubleString);
    }
}

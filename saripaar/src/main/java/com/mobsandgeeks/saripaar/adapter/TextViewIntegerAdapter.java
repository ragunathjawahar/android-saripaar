/*
 * Copyright (C) 2014 Mobs and Geeks
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
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class TextViewIntegerAdapter implements ViewDataAdapter<TextView, Integer> {
    private static final String REGEX_INTEGER = "\\d+";

    @Override
    public Integer getData(TextView editText) throws ConversionException {
        String integerString = editText.getText().toString().trim();
        if (!integerString.matches(REGEX_INTEGER)) {
            String message = String.format("Expected an integer, but was %s", integerString);
            throw new ConversionException(message);
        }

        return Integer.parseInt(integerString);
    }

}

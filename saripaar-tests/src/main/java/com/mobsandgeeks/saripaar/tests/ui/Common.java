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

package com.mobsandgeeks.saripaar.tests.ui;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public final class Common {

    public static String getFailedFieldNames(List<ValidationError> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ValidationError error : errors) {
            View view = error.getView();
            TextView textView = view instanceof FloatLabeledEditText
                ? ((FloatLabeledEditText) view).getEditText()
                : (TextView) view;
            List<Rule> failedRules = error.getFailedRules();
            String fieldName = textView.getHint().toString().toUpperCase().replaceAll(" ", "_");

            for (Rule failedRule : failedRules) {
                stringBuilder.append(fieldName).append(" ");
                Log.i(Rule.class.getSimpleName(), failedRule.toString());
            }
        }
        return stringBuilder.toString();
    }

}

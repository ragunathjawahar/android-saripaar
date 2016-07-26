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

package com.mobsandgeeks.saripaar.rule;

import android.content.Context;

import com.mobsandgeeks.saripaar.ContextualAnnotationRule;
import com.mobsandgeeks.saripaar.ValidationContext;
import com.mobsandgeeks.saripaar.annotation.Future;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class FutureRule extends ContextualAnnotationRule<Future, String> {

    protected FutureRule(final Future future, final ValidationContext validationContext) {
        super(future, validationContext);
    }

    @Override
    public boolean isValid(final String dateString) {
        DateFormat dateFormat = getDateFormat();
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException ignored) {}

        Date now = new Date();
        return parsedDate != null && parsedDate.after(now);
    }

    private DateFormat getDateFormat() {
        Context context = mValidationContext.getContext();
        int dateFormatResId = mRuleAnnotation.dateFormatResId();
        String dateFormatString =  dateFormatResId != -1
                ? context.getString(dateFormatResId) : mRuleAnnotation.dateFormat();
        return new SimpleDateFormat(dateFormatString);
    }
}

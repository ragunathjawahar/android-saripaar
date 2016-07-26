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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class NotEmptyRule extends ContextualAnnotationRule<NotEmpty, String> {

    protected NotEmptyRule(final NotEmpty notEmpty, final ValidationContext validationContext) {
        super(notEmpty, validationContext);
    }

    @Override
    public boolean isValid(final String data) {
        boolean isEmpty = false;
        if (data != null) {
            String text = mRuleAnnotation.trim() ? data.trim() : data;

            Context context = mValidationContext.getContext();
            String emptyText = mRuleAnnotation.emptyTextResId() != -1
                    ? context.getString(mRuleAnnotation.emptyTextResId())
                    : mRuleAnnotation.emptyText();

            isEmpty = emptyText.equals(text) || "".equals(text);
        }

        return !isEmpty;
    }
}

/*
 * Copyright (C) 2015 Mobs & Geeks
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

import android.view.View;

import com.mobsandgeeks.saripaar.ContextualAnnotationRule;
import com.mobsandgeeks.saripaar.ValidationContext;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * A generic class for comparing values across two {@link android.view.View}s.
 *
 * @see com.mobsandgeeks.saripaar.annotation.ConfirmEmail
 * @see com.mobsandgeeks.saripaar.annotation.ConfirmPassword
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
class SameValueContextualRule<CONFIRM extends Annotation, SOURCE extends Annotation, DATA_TYPE>
        extends ContextualAnnotationRule<CONFIRM, DATA_TYPE> {
    private Class<SOURCE> mSourceClass;
    private Class<CONFIRM> mConfirmClass;

    protected SameValueContextualRule(final CONFIRM confirmAnnotation,
            final Class<SOURCE> sourceClass, final ValidationContext validationContext) {
        super(confirmAnnotation, validationContext);
        mConfirmClass = (Class<CONFIRM>) confirmAnnotation.annotationType();
        mSourceClass = sourceClass;
    }

    @Override
    public boolean isValid(final DATA_TYPE confirmValue) {
        List<View> sourceViews = mValidationContext.getAnnotatedViews(mSourceClass);
        int nSourceViews = sourceViews.size();

        if (nSourceViews == 0) {
            String message = String.format(
                    "You should have a view annotated with '%s' to use '%s'.",
                    mSourceClass.getName(), mConfirmClass.getName());
            throw new IllegalStateException(message);
        } else if (nSourceViews > 1) {
            String message = String.format(
                    "More than 1 field annotated with '%s'.", mSourceClass.getName());
            throw new IllegalStateException(message);
        }

        // There's only one, then we're good to go :)
        View view = sourceViews.get(0);
        Object sourceValue = mValidationContext.getData(view, mSourceClass);

        return confirmValue.equals(sourceValue);
    }
}

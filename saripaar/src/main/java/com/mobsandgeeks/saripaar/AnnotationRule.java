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

package com.mobsandgeeks.saripaar;

import android.content.Context;

import java.lang.annotation.Annotation;

/**
 * Saripaar {@link java.lang.annotation.Annotation}s have an
 * {@link com.mobsandgeeks.saripaar.AnnotationRule} paired to them, via the
 * {@link com.mobsandgeeks.saripaar.annotation.ValidateUsing} annotation. Like stock annotations,
 * custom annotations must also have a corresponding
 * {@link com.mobsandgeeks.saripaar.AnnotationRule}.
 *
 * @param <RULE_ANNOTATION>  The associated rule {@link java.lang.annotation.Annotation}.
 * @param <DATA_TYPE>  The data type this rule operates on.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public abstract class AnnotationRule<RULE_ANNOTATION extends Annotation, DATA_TYPE>
        extends Rule<DATA_TYPE> {

    protected final RULE_ANNOTATION mRuleAnnotation;

    /**
     * Constructor. It is mandatory that all subclasses MUST have a constructor with the same
     * signature.
     *
     * @param ruleAnnotation  The rule {@link java.lang.annotation.Annotation} instance to which
     *      this rule is paired.
     */
    protected AnnotationRule(final RULE_ANNOTATION ruleAnnotation) {
        super(ruleAnnotation != null
                ? Reflector.getAttributeValue(ruleAnnotation, "sequence", Integer.TYPE) : -1);
        if (ruleAnnotation == null) {
            throw new IllegalArgumentException("'ruleAnnotation' cannot be null.");
        }
        mRuleAnnotation = ruleAnnotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(final Context context) {
        final int messageResId = Reflector.getAttributeValue(mRuleAnnotation, "messageResId",
                Integer.class);

        return messageResId != -1
                ? context.getString(messageResId)
                : Reflector.getAttributeValue(mRuleAnnotation, "message", String.class);
    }
}

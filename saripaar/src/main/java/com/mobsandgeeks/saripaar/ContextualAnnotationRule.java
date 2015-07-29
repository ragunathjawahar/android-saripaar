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

package com.mobsandgeeks.saripaar;

import java.lang.annotation.Annotation;

/**
 * An {@link com.mobsandgeeks.saripaar.AnnotationRule} that has access to a
 * {@link com.mobsandgeeks.saripaar.ValidationContext}.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public abstract class ContextualAnnotationRule<RULE_ANNOTATION extends Annotation, DATA_TYPE>
        extends AnnotationRule<RULE_ANNOTATION, DATA_TYPE> {

    protected ValidationContext mValidationContext;

    /**
     * Constructor. All subclasses MUST have a constructor with the same signature.
     *
     * @param validationContext  A {@link com.mobsandgeeks.saripaar.ValidationContext}.
     * @param ruleAnnotation  The rule {@link java.lang.annotation.Annotation} instance to which
     *      this rule is paired.
     */
    protected ContextualAnnotationRule(ValidationContext validationContext,
            RULE_ANNOTATION ruleAnnotation) {
        super(ruleAnnotation);
        if (validationContext == null) {
            throw new IllegalArgumentException("'validationContext' cannot be null.");
        }
        mValidationContext = validationContext;
    }
}

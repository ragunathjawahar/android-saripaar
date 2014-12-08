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

package com.mobsandgeeks.saripaar;

import android.content.Context;

import java.lang.annotation.Annotation;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public abstract class Rule<RULE_ANNOTATION extends Annotation, DATA_TYPE> {

    protected final RULE_ANNOTATION mRuleAnnotation;

    protected Rule(final RULE_ANNOTATION ruleAnnotation) {
        if (ruleAnnotation == null) {
            throw new IllegalArgumentException("'ruleAnnotation' cannot be null.");
        }
        mRuleAnnotation = ruleAnnotation;
    }

    public String getMessage(final Context context) {
        final int messageResId = Reflector.getAttributeValue(mRuleAnnotation, "messageResId",
            Integer.class);

        return messageResId != -1
            ? context.getString(messageResId)
            : Reflector.getAttributeValue(mRuleAnnotation, "message", String.class);
    }

    public abstract boolean isValid(DATA_TYPE data);

}

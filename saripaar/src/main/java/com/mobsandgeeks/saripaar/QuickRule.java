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

import android.view.View;

/**
 * Unlike {@link com.mobsandgeeks.saripaar.AnnotationRule}s,
 * {@link com.mobsandgeeks.saripaar.QuickRule}s can exist without Annotations. These rules are
 * directly applied to {@link android.view.View}s. They are added to the
 * {@link com.mobsandgeeks.saripaar.Validator} using the
 * {@link com.mobsandgeeks.saripaar.Validator#put(android.view.View, QuickRule[])} method.
 *
 * @param <VIEW>  The {@link android.view.View} on which this rule is to be applied.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public abstract class QuickRule<VIEW extends View> extends Rule<VIEW> {

    /**
     * Default constructor.
     */
    protected QuickRule() {
        super(-1);
    }

    /**
     * Constructor.
     *
     * @param sequence  A non-negative integer value.
     */
    protected QuickRule(final int sequence) {
        super(sequence);
        if (sequence < 0) {
            String message = "'sequence' should be a non-negative integer.";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks if the rule is valid.
     *
     * @param view  The {@link android.view.View} on which the rule has to be applied.
     *
     * @return true if valid, false otherwise.
     */
    public abstract boolean isValid(VIEW view);
}

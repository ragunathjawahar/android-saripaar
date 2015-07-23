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

import com.mobsandgeeks.saripaar.AnnotationRule;
import com.mobsandgeeks.saripaar.annotation.Length;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class LengthRule extends AnnotationRule<Length, String> {

    protected LengthRule(final Length length) {
        super(length);
    }

    @Override
    public boolean isValid(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("'text' cannot be null.");
        }
        int ruleMin = mRuleAnnotation.min();
        int ruleMax = mRuleAnnotation.max();

        // Assert min is <= max
        assertMinMax(ruleMin, ruleMax);

        // Trim?
        int length = mRuleAnnotation.trim() ? text.trim().length() : text.length();

        // Check for min length
        boolean minIsValid = true;
        if (ruleMin != Integer.MIN_VALUE) { // Min is set
            minIsValid = length >= ruleMin;
        }

        // Check for max length
        boolean maxIsValid = true;
        if (ruleMax != Integer.MAX_VALUE) { // Max is set
            maxIsValid = length <= ruleMax;
        }

        return minIsValid && maxIsValid;
    }

    private void assertMinMax(int min, int max) {
        if (min > max) {
            String message = String.format(
                    "'min' (%d) should be less than or equal to 'max' (%d).", min, max);
            throw new IllegalStateException(message);
        }
    }
}

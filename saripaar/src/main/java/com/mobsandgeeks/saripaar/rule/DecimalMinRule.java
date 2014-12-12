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

package com.mobsandgeeks.saripaar.rule;

import com.mobsandgeeks.saripaar.AnnotationRule;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;

import commons.validator.routines.DoubleValidator;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class DecimalMinRule extends AnnotationRule<DecimalMin, Double> {

    protected DecimalMinRule(DecimalMin decimalMin) {
        super(decimalMin);
    }

    @Override
    public boolean isValid(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("'Double' cannot be null.");
        }
        double minValue = mRuleAnnotation.value();
        return DoubleValidator.getInstance().minValue(value, minValue);
    }
}

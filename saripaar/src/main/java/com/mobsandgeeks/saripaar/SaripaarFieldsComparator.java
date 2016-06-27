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

import com.mobsandgeeks.saripaar.annotation.Order;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Sorts the {@link android.view.View} {@link java.lang.reflect.Field} objects based on the
 * {@link com.mobsandgeeks.saripaar.annotation.Order} annotation.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
final class SaripaarFieldsComparator implements Comparator<Field> {
    private boolean mOrderedFields = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Field lhsField, final Field rhsField) {
        final Order lhsOrderAnnotation = lhsField.getAnnotation(Order.class);
        final Order rhsOrderAnnotation = rhsField.getAnnotation(Order.class);

        int comparison;
        if (lhsOrderAnnotation == null || rhsOrderAnnotation == null) {
            mOrderedFields = false;
            comparison = 0;
        } else {
            int lhsOrder = lhsOrderAnnotation.value();
            int rhsOrder = rhsOrderAnnotation.value();

            comparison = lhsOrder == rhsOrder
                    ? 0 : lhsOrder > rhsOrder ? 1 : -1;
        }

        return comparison;
    }

    /**
     * Tells if the fields are ordered. Useful only after the
     * {@link com.mobsandgeeks.saripaar.SaripaarFieldsComparator} is used to sort collection. Will
     * return true, if this method is called on an unused instance.
     *
     * @return true if all the fields are ordered, false otherwise.
     */
    boolean areOrderedFields() {
        return mOrderedFields;
    }
}

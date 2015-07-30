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

import android.util.Pair;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;

import java.util.Comparator;

/**
 * Sorts the {@link com.mobsandgeeks.saripaar.Rule} and
 * {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter} pairs based on the
 * 'sequence' attribute.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
final class SequenceComparator implements Comparator<Pair<Rule, ViewDataAdapter>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Pair<Rule, ViewDataAdapter> lhsPair,
            final Pair<Rule, ViewDataAdapter> rhsPair) {

        final int lhsSequence = lhsPair.first.getSequence();
        final int rhsSequence = rhsPair.first.getSequence();

        return lhsSequence == rhsSequence
                ? 0 : lhsSequence > rhsSequence ? 1 : -1;
    }
}

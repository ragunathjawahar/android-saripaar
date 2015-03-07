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

import java.util.Comparator;

/**
 * Sorts the {@link com.mobsandgeeks.saripaar.Validator.RuleAdapterPair}s based on the
 * 'sequence' attribute.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
final class SequenceComparator implements Comparator<Validator.RuleAdapterPair> {

    @Override
    public int compare(final Validator.RuleAdapterPair lhsPair,
            final Validator.RuleAdapterPair rhsPair) {

        final int lhsSequence = lhsPair.rule.getSequence();
        final int rhsSequence = rhsPair.rule.getSequence();

        return lhsSequence > rhsSequence
                ? 1
                : lhsSequence < rhsSequence ? -1 : 0;
    }
}

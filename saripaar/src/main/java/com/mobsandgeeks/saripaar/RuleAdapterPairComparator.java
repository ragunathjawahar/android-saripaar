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

import com.mobsandgeeks.saripaar.Validator.RuleAdapterPair;
import java.util.Comparator;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
class RuleAdapterPairComparator implements Comparator<RuleAdapterPair> {

    @Override
    public int compare(RuleAdapterPair lhs, RuleAdapterPair rhs) {
        int lhsOrder = lhs.rule.getOrder();
        int rhsOrder = rhs.rule.getOrder();

        return lhsOrder > rhsOrder
            ? 1
            : lhsOrder < rhsOrder
                ? -1 : 0;
    }

}

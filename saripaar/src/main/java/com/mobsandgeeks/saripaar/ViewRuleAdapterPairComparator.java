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

import android.view.View;
import com.mobsandgeeks.saripaar.Validator.RuleAdapterPair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
class ViewRuleAdapterPairComparator implements Comparator<View> {
    final Map<View, ArrayList<RuleAdapterPair>> mViewRulesMap;

    public ViewRuleAdapterPairComparator(Map<View, ArrayList<RuleAdapterPair>> viewRulesMap) {
        this.mViewRulesMap = viewRulesMap;
    }

    @Override
    public int compare(View lhs, View rhs) {
        int minOrderLhs = getMinOrder(mViewRulesMap.get(lhs));
        int minOrderRhs = getMinOrder(mViewRulesMap.get(rhs));

        return minOrderLhs > minOrderRhs
            ? 1
            : minOrderLhs < minOrderRhs
                ? -1 : 0;
    }

    private int getMinOrder(List<RuleAdapterPair> ruleAdapterPairs) {
        int minOrder = ruleAdapterPairs.get(0).rule.getOrder();
        for (RuleAdapterPair ruleAdapterPair : ruleAdapterPairs) {
            int order = ruleAdapterPair.rule.getOrder();
            if (order < minOrder) {
                minOrder = order;
            }
        }
        return minOrder;
    }
}

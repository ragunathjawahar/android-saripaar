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

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class ValidationError {
    private final View view;
    private final Rule failedRule;

    public ValidationError(View view, Rule failedRule) {
        this.view = view;
        this.failedRule = failedRule;
    }

    public View getView() {
        return view;
    }

    public Rule getFailedRule() {
        return failedRule;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
            "view=" + view +
            ", failedRule=" + failedRule +
            '}';
    }
}

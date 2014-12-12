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

/**
 * This is a base interface for {@link com.mobsandgeeks.saripaar.AnnotationRule} and
 * {@link com.mobsandgeeks.saripaar.QuickRule}. Used internally as a parent for grouping.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
interface Rule {

    /**
     * Returns a failure message associated with the rule.
     *
     * @param context  Any {@link android.content.Context} instance, usually an
     *      {@link android.app.Activity}.
     *
     * @return A failure message.
     */
    String getMessage(final Context context);
}

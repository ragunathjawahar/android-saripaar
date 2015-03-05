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

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

/**
 * A default implementation of the {@link com.mobsandgeeks.saripaar.Validator.ViewValidatedAction}
 * that clears error messages on {@link android.widget.EditText} views by calling the
 * {@link android.widget.TextView#setError(CharSequence)} with a {@code null} parameter.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class DefaultViewValidatedAction implements Validator.ViewValidatedAction {

    private Handler handler;

    @Override
    public void onAllRulesPassed(final View view) {
        boolean isTextView = view instanceof TextView;
        if (!isTextView) return;

        boolean onMainThread = Looper.myLooper() == Looper.getMainLooper();
        if (onMainThread) {
            ((TextView) view).setError(null);
        } else {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) view).setError(null);
                }
            });
        }
    }

    private void runOnMainThread(Runnable runnable) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(runnable);
    }
}

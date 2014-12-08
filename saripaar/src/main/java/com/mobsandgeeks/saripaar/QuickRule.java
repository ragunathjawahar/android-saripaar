package com.mobsandgeeks.saripaar;

import android.view.View;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 * @since 2.0
 */
public abstract class QuickRule<T extends View> implements Rule {
    public abstract boolean isValid(T view);
}

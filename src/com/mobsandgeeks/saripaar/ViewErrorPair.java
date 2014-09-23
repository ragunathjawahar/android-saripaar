package com.mobsandgeeks.saripaar;

import android.view.View;

/**
 * Created by maxchursin on 7/31/14.
 */
public class ViewErrorPair {
    private View view;
    private String errors;

    public ViewErrorPair(View view, String errors) {
        this.view = view;
        this.errors = errors;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}

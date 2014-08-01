package com.mobsandgeeks.saripaar;

import android.view.View;

/**
 * Created by maxchursin on 7/31/14.
 */
public class ViewRulePair {
    private View view;
    private Rule rule;

    public ViewRulePair(View view, Rule<?> rule) {
        this.setView(view);
        this.setRule(rule);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}

package com.mobsandgeeks.saripaar;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxchursin on 7/31/14.
 */
public class ViewRulePair {
    private View view;
    private List<Rule> rules = new ArrayList<Rule>();

    public ViewRulePair(View view, List<Rule> rules) {
        this.setView(view);
        this.setRules(rules);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}

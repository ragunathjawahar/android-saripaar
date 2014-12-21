package com.mobsandgeeks.saripaar.showcase;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI References
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
    }

}

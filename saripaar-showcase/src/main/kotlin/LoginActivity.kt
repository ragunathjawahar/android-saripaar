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

package com.mobsandgeeks.saripaar.showcase

import android.app.Activity
import android.widget.EditText
import android.os.Bundle
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Password
import com.mobsandgeeks.saripaar.annotation.Password.Scheme
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.Validator.ValidationListener
import com.mobsandgeeks.saripaar.ValidationError
import android.widget.Toast
import android.widget.Button
import com.mobsandgeeks.saripaar.annotation.Order
import com.mobsandgeeks.saripaar.Validator.Mode

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class LoginActivity : Activity(), ValidationListener {

    // UI References
    NotEmpty
    Order(1)
    private var emailEditText: EditText? = null

    Password(scheme = Scheme.ALPHA_NUMERIC)
    Order(2)
    private var passwordEditText: EditText? = null

    private var loginButton: Button? = null

    // Validation
    val validator = Validator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI References
        emailEditText = findViewById(R.id.emailEditText) as EditText
        passwordEditText = findViewById(R.id.passwordEditText) as EditText
        loginButton = findViewById(R.id.loginButton) as Button

        // Validator
        validator.setValidationMode(Mode.IMMEDIATE)

        // Listeners
        validator.setValidationListener(this)
        loginButton?.setOnClickListener { v -> validator.validate() }
    }

    override fun onValidationSucceeded() {
        showToast("Yay")
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        // Since, we're using Mode.IMMEDIATE, we'll be notified as soon
        // as a view fails validation. So, the collection will always have
        // only one item.
        val error = errors.get(0)
        val message = error.getCollatedErrorMessage(this)
        val editText = error.getView() as EditText
        editText.setError(message)
        editText.requestFocus()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

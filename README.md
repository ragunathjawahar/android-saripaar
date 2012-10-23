Android Saripaar
----------------

**சரிபார்** - sari-paar (Tamil for "to check", "verify" or "validate")

Android Saripaar is a simple, yet powerful rule-based UI validation libarary for Android™.
It is the **SIMPLEST** validation library available for Android yet!

Why Android Saripaar?
---------------------

 - Declarative style validation powered by **Annotations**.
 - Does both **Synchronous** and **Asynchronous** validations, you don't have to worry about threading.
 - Works with **Stock Android Widgets**, no custom view dependencies.
 - Quick to setup, just download the [jar] and include it in your `libs` project folder.
 - Takes most of your validation logic out of your code.
 - Compatible with other annotation frameworks such as **AndroidAnnotations**, **RoboGuice**, etc.,

Quick Start
-----------
**Step 1 - Annotate your widgets using [Saripaar Annotations]**
```java
@Email(order = 1)
private EditText emailEditText;

@Password(order = 2)
@TextRule(order = 3, minLength = 6, message = "Enter at least 6 characters.")
private EditText passwordEditText;

@ConfirmPassword(order = 4)
private EditText confirmPasswordEditText;

@Checked(order = 5, message = "You must agree to the terms.")
private CheckBox iAgreeCheckBox;
```

The annotations are self-explanatory. The `order` attribute is mandatory and specified the order in which the validations will be performed by the library.

**Step 2 - Instantiate a new [Validator]**
```java
public void onCreate() {
    super.onCreate();
    // Code…

    validator = new Validator(this);
    validator.setValidationListener(this);

    // More code…
}
```
You will need a `Validator` for the current `Activity` and also a `ValidationListener` for callbacks on validation events.

**Step 3 - Implement your [ValidationListener]**
```java
public class RegistrationActivity implements ValidationListener {

    public void onSuccess() {
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();

        // Create a new account…
    }

    public void onFailure(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            failedView.setError(message);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void preValidation() {
        // Do nothing…
    }

    public void onValidationCancelled() {
        // Do nothing
    }
}
```

  [jar]: https://github.com/ragunathjawahar/android-saripaar/downloads
  [Saripaar Annotations]: https://github.com/ragunathjawahar/android-saripaar/tree/master/src/com/mobsandgeeks/saripaar/annotation
  [Validator]: https://github.com/ragunathjawahar/android-saripaar/blob/master/src/com/mobsandgeeks/saripaar/Validator.java
  [ValidationListener]: https://github.com/ragunathjawahar/android-saripaar/blob/master/src/com/mobsandgeeks/saripaar/Validator.java
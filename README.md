[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=ragunathjawahar&url=https://github.com/ragunathjawahar/android-saripaar/&title=Android Saripaar&language=&tags=github&category=software) 

Android Saripaar
----------------

**சரிபார்** - sari-paar (Tamil for "to check", "verify" or "validate")

Android Saripaar is a simple, yet powerful rule-based UI validation library for Android.
It is the **SIMPLEST** validation library available for Android.

Why Android Saripaar?
---------------------

 - Declarative style validation powered by **Annotations**.
 - Does both **Synchronous** and **Asynchronous** validations, you don't have to worry about threading.
 - Works with **Stock Android Widgets**, no custom view dependencies.
 - Quick to setup, just download the [jar] and include it in your `libs` project folder.
 - Takes most of your validation logic out of your code.
 - Compatible with other annotation frameworks such as **[AndroidAnnotations]**, **[RoboGuice]**, etc.,

Quick Start
-----------
**Step 1 - Annotate your widgets using [Saripaar Annotations]**
```java
@Required(order = 1)
@Email(order = 2)
private EditText emailEditText;

@Password(order = 3)
@TextRule(order = 4, minLength = 6, message = "Enter at least 6 characters.")
private EditText passwordEditText;

@ConfirmPassword(order = 5)
private EditText confirmPasswordEditText;

@Checked(order = 6, message = "You must agree to the terms.")
private CheckBox iAgreeCheckBox;
```

The annotations are self-explanatory. The `order` attribute is mandatory and specifies the order in which the validations will be performed by the library.

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

**Step 3 - Implement a [ValidationListener]**
```java
public class RegistrationActivity implements ValidationListener {

    public void onSuccess() {
        // Create a new account…
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
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
 - `onSuccess()` - Called when all your views pass all validations.
 - `onFailure(View, Rule<?>)` - Called when a `Rule` fails, you receive the `View` along with the `Rule` that failed.
 - `preValidation()` - Called before the validation starts, useful during asynchronous validations.
 - `onValidationCancelled()` - Called when an asynchronous validation is cancelled, never called during synchronous validations.

**Step 4 - Perform validation**
```java
registerButton.setOnClickListener(new OnClickListener() {
    public void onClick(View v) {
        validator.validate();
    }
});
```
The `Validator.validate()` call runs the validations and returns the result via appropriate callbacks on the `ValidationListener`. You can run validations on a background `AsyncTask` by calling the `Validator.validateAsync()` method. You can call both the methods from any event listener such as the `OnClickListener`, `TextWatcher`, `OnFocusChangedListener`, `OnTouchListener`, etc.,

Dependencies
---------------------

 - [Android Support Library]

**Please visit the [wiki] for a complete guide on Android Saripaar.**

  [jar]: https://github.com/ragunathjawahar/android-saripaar/downloads
  [Saripaar Annotations]: https://github.com/ragunathjawahar/android-saripaar/tree/master/src/com/mobsandgeeks/saripaar/annotation
  [AndroidAnnotations]: https://github.com/excilys/androidannotations
  [RoboGuice]: http://code.google.com/p/roboguice/
  [Validator]: https://github.com/ragunathjawahar/android-saripaar/blob/master/src/com/mobsandgeeks/saripaar/Validator.java
  [ValidationListener]: https://github.com/ragunathjawahar/android-saripaar/blob/master/src/com/mobsandgeeks/saripaar/Validator.java
  [Android Support Library]: http://developer.android.com/tools/extras/support-library.html
  [wiki]: https://github.com/ragunathjawahar/android-saripaar/wiki

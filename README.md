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

Quick Start
-----------
**Step 1 - Annotate the fields you want to validate using [Saripaar Annotations]**
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

  [jar]: https://github.com/ragunathjawahar/android-saripaar/downloads
  [Saripaar Annotations]: https://github.com/ragunathjawahar/android-saripaar/tree/master/src/com/mobsandgeeks/saripaar/annotation

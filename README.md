Android Saripaar
================
![Logo](logo.png)

**சரிபார்** - sari-paar (Tamil for "to check", "verify" or "validate")

Android Saripaar is a simple, yet powerful rule-based UI validation library for Android.
It is the **SIMPLEST** validation library available for Android.

Why Android Saripaar?
---------------------

 - Declarative style validation powered by **Annotations**.
 - **Extensible**
 - **Synchronous** and **Asynchronous** validations, you don't have to worry about threading.
 - Works with **Stock Android Widgets**, no custom view dependencies.
 - Quick to setup, just download the [jar] and include it in your `libs` project folder.
 - Isolates validation logic using rules.
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
You will need a `Validator` and a `ValidationListener` for receiving callbacks on validation events.

**Step 3 - Implement a [ValidationListener]**
```java
public class RegistrationActivity implements ValidationListener {

    public void onValidationSucceeded() {
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
    }

    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

}
```
 - `onValidationSucceeded()` - Called when all your views pass all validations.
 - `onValidationFailed(View, Rule<?>)` - Called when a `Rule` fails, you receive the `View` along with the `Rule` that failed.

**Step 4 - Validate**
```java
registerButton.setOnClickListener(new OnClickListener() {

    public void onClick(View v) {
        validator.validate();
    }
});
```
The `Validator.validate()` call runs the validations and returns the result via appropriate callbacks on the `ValidationListener`. You can run validations on a background `AsyncTask` by calling the `Validator.validateAsync()` method.

Maven
---------------------
    <dependency>
        <groupId>com.mobsandgeeks</groupId>
        <artifactId>android-saripaar</artifactId>
        <version>1.0.2</version>
    </dependency>

Gradle
---------------------
    dependencies {
        compile 'com.mobsandgeeks:android-saripaar:1.0.2'
    }

Wiki
---------------------
Please visit the [wiki] for a complete guide on Android Saripaar.

License
---------------------

    Copyright 2012 Mobs and Geeks

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

<sub>Saripaar Logo © 2013, Mobs & Geeks.<sub>

  [jar]: http://search.maven.org/#search%7Cga%7C1%7Candroid%20saripaar
  [Saripaar Annotations]: https://github.com/ragunathjawahar/android-saripaar/tree/master/src/com/mobsandgeeks/saripaar/annotation
  [AndroidAnnotations]: https://github.com/excilys/androidannotations
  [RoboGuice]: http://code.google.com/p/roboguice/
  [Validator]: https://github.com/ragunathjawahar/android-saripaar/blob/master/src/com/mobsandgeeks/saripaar/Validator.java
  [ValidationListener]: https://github.com/ragunathjawahar/android-saripaar/blob/master/src/com/mobsandgeeks/saripaar/Validator.java
  [wiki]: https://github.com/ragunathjawahar/android-saripaar/wiki


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/ragunathjawahar/android-saripaar/trend.png)](https://bitdeli.com/free "Bitdeli Badge")


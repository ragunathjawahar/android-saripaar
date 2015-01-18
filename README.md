Android Saripaar v2
===================
![Logo](logo.png)

**சரிபார்** - sari-paar (Tamil for "to check", "verify" or "validate")

Android Saripaar is a simple, yet powerful rule-based UI form validation library for Android.
It is the **SIMPLEST** and **FEATURE-RICH** validation library available for Android.

*Note: v2 is still under development and is available as snapshots for PREVIEW. For a feature complete version of the library, please use v1 (available from Maven Central). The following annotations are yet to be implemented in Saripaar v2 `@Domain`, `@Future`, `@Past` and `@Digits`*.

Why Android Saripaar?
---------------------

 - Built on top of [Apache Commons Validator], a validation framework with proven track record on the web, desktop and mobile platforms.
 - Declarative style validation using **Annotations**.
 - **Extensible**, now allows Custom Annotations.
 - **Synchronous** and **Asynchronous** validations, you don't have to worry about threading.
 - Supports both BURST and IMMEDIATE modes.
 - Works with **Stock Android Widgets**, no custom view dependencies.
 - Quick to setup, just download the [jar] and include it in your `libs` project folder.
 - Isolates validation logic using rules.
 - Compatible with other annotation frameworks such as [ButterKnife], [AndroidAnnotations], [RoboGuice], etc.,

Quick Start
-----------
**Step 1 - Annotate your widgets using [Saripaar Annotations]**
```java
@NotEmpty
@Email
private EditText emailEditText;

@Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS)
private EditText passwordEditText;

@ConfirmPassword
private EditText confirmPasswordEditText;

@Checked(message = "You must agree to the terms.")
private CheckBox iAgreeCheckBox;
```

The annotations are self-explanatory. The `@Order` annotation is required ONLY when performing ordered validations using
`Validator.validateTill(View)` and `Validator.validateBefore(View)` or in `IMMEDIATE` mode.

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

    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            // Do anything you want :)
        }
    }

}
```
 - `onValidationSucceeded()` - Called when all your views pass all validations.
 - `onValidationFailed(List<ValidationError> errors)` - Called when there are validation error(s).

**Step 4 - Validate**
```java
registerButton.setOnClickListener(new OnClickListener() {

    public void onClick(View v) {
        validator.validate();
    }
});
```
The `Validator.validate()` call runs the validations and returns the result via appropriate callbacks on the `ValidationListener`. You can run validations on a background `AsyncTask` by calling the `Validator.validate(true)` method.

Maven
---------------------
    <dependency>
        <groupId>com.mobsandgeeks</groupId>
        <artifactId>android-saripaar</artifactId>
        <version>2.0-SNAPSHOT</version>
    </dependency>

Gradle
---------------------
    dependencies {
        compile 'com.mobsandgeeks:android-saripaar:2.0-SNAPSHOT'
    }

Snapshots
---------------------
In your `{project_base}/build.gradle` file, include the following.

    allprojects {
        repositories {
            mavenCentral()
            maven {
                url "https://oss.sonatype.org/content/repositories/snapshots/"
            }
        }
    }

ProGuard
---------------------
Exclude Saripaar classes from obfuscation and minification. Add the following rules to your `proguard-rules.pro` file.

    -keep class com.mobsandgeeks.saripaar.** {*;}
    -keep class commons.validator.routines.** {*;}

Wiki
---------------------
Please visit the [wiki] for a complete guide on Android Saripaar.

License
---------------------

    Copyright 2012 - 2015 Mobs & Geeks

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

<sub>Saripaar Logo © 2013 - 2015, Mobs &amp; Geeks.<sub>

  [jar]: http://search.maven.org/#search%7Cga%7C1%7Candroid%20saripaar
  [Apache Commons Validator]: http://commons.apache.org/proper/commons-validator/
  [ButterKnife]: https://github.com/JakeWharton/butterknife
  [AndroidAnnotations]: https://github.com/excilys/androidannotations
  [RoboGuice]: https://github.com/roboguice/roboguice/
  [Saripaar Annotations]: https://github.com/ragunathjawahar/android-saripaar/tree/master/saripaar/src/main/java/com/mobsandgeeks/saripaar/annotation
  [Validator]: https://github.com/ragunathjawahar/android-saripaar/blob/master/saripaar/src/main/java/com/mobsandgeeks/saripaar/Validator.java
  [ValidationListener]: https://github.com/ragunathjawahar/android-saripaar/blob/master/saripaar/src/main/java/com/mobsandgeeks/saripaar/Validator.java
  [wiki]: https://github.com/ragunathjawahar/android-saripaar/wiki

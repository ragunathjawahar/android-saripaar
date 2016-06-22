Android Saripaar v2 [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Saripaar-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/526)
===================
![Logo](logo.png)

**சரிபார்** - sari-paar (Tamil for "to check", "verify" or "validate")

Android Saripaar is a simple, feature-rich and powerful rule-based UI form validation library for Android.
It is the **SIMPLEST** UI validation library available for Android.

Why Android Saripaar?
---------------------

 - Built on top of [Apache Commons Validator], a validation framework with proven track record on the web, desktop and mobile platforms.
 - Declarative style validation using **Annotations**.
 - **Extensible**, now allows Custom Annotations.
 - **Synchronous** and **Asynchronous** validations, you don't have to worry about threading.
 - Supports both BURST and IMMEDIATE modes.
 - Works with **Stock Android Widgets**, no custom view dependencies.
 - Isolates validation logic using rules.
 - Compatible with other annotation-based libraries and frameworks such as [ButterKnife], [AndroidAnnotations], [RoboGuice], etc.,

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
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Code…

    validator = new Validator(this);
    validator.setValidationListener(this);

    // More code…
}
```
You will need a `Validator` and a `ValidationListener` for receiving callbacks on validation events.

**Step 3 - Implement a [ValidationListener]**
```java
public class RegistrationActivity extends Activity implements ValidationListener {

    // Code…

    @Override
    public void onValidationSucceeded() {
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
```
 - `onValidationSucceeded()` - Called when all your views pass all validations.
 - `onValidationFailed(List<ValidationError> errors)` - Called when there are validation error(s).

**Step 4 - Validate**
```java
registerButton.setOnClickListener(new OnClickListener() {

    @Override
    public void onClick(View v) {
        validator.validate();
    }
});
```
The `Validator.validate()` call runs the validations and returns the result via appropriate callbacks on the `ValidationListener`. You can run validations on a background `AsyncTask` by calling the `Validator.validate(true)` method.

Saripaar X
---------------------
If you are looking for country-specific annotations, checkout the [Saripaar X] project. The extensions project is in its early stages and needs contributors. Feel free to contribute.

Maven
---------------------
    <dependency>
        <groupId>com.mobsandgeeks</groupId>
        <artifactId>android-saripaar</artifactId>
        <version>(latest version)</version>
    </dependency>

Gradle
---------------------
    dependencies {
        compile 'com.mobsandgeeks:android-saripaar:(latest version)'
    }

Snapshots
---------------------
In your `{project_base}/build.gradle` file, include the following.

    allprojects {
        repositories {
            jcenter()
            maven {
                url "https://oss.sonatype.org/content/repositories/snapshots/"
            }
        }
    }

ProGuard
---------------------
Exclude Saripaar classes from obfuscation and minification. Add the following rules to your `proguard-rules.pro` file.

    -keep class com.mobsandgeeks.saripaar.** {*;}
    -keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}

Evolution
---------------------
For those interested in finding out how v2 evolved from v1, watch this (~20 second) [video].

Using Saripaar?
---------------------
[Tweet] me with your Google Play URL and I'll add your app to the list :)

Icon         | App           | Icon         | App           | Icon         | App
------------ | ------------- | ------------ | ------------- | ------------ | -------------
<img src="https://lh3.ggpht.com/qhpfFQFd5YuLzT5d9jUCI69dMeLlW6XewLsgZ0l06D92M0SmvsMKSMd_YY1Xc9K1GyU=w300-rw" width="48" height="48" /> | [Wikipedia] | <img src="https://lh6.ggpht.com/i_pxbaojay2K2xb2RDC2W7eOnNlpGRgILoACaEDhaKz87JSg3nEJHV3Vz3wmS3L3e4M=w300-rw" width="48" height="48" /> | [Wikipedia Beta] | <img src="https://lh3.ggpht.com/o2lhzbRnq6U1oPxyqY6LDJIc2PO_tm1_sIbX-fMLpG2Sxk94QW2gQaDw8ewam1dPQrdz=w300-rw" width="48" height="48" /> | [Mizuno Baton]
<img src="https://lh6.ggpht.com/t-WYlpXlwhLL0unTDChiVi24b4LP0kNsJQnRwFaMHd0NGqxgQ2LupQ1Dl7M1ztj8Vg8=w300-rw" width="48" height="48" /> | [Fetch] | <img src="https://lh3.ggpht.com/J3bMDphmzsPFQeMfWR-LH70g5vSGrTVggPzXQdUafKM2KmpWS3THIcSHQaTVbCQ_hjw=w300-rw" width="48" height="48" /> | [HealtheMinder] | <img src="https://lh3.ggpht.com/EhidzByoyUY1OPVcsjOmtOcRwoxphRCy1-a_qKLYKHwsS0DuHIC9cHIDEPLVKO-oTw=w300-rw" width="48" height="48" /> | [MomMe]
<img src="https://lh5.ggpht.com/h6T-az0ip_OqNtSh__Kc5-0ZPpT7sYxSn4kFPOjrNI7o-LN9bPbovoiYDfswL-B5XQ=w300-rw" width="48" height="48" /> | [Feelknit] | <img src="https://lh4.ggpht.com/k5zFS5VheJKt1yBHKC-wBgJTwOQ4Q_Aa2XbG6Ea_-HvP8nXjYeSnO_227j1wVno1JZY=w300-rw" width="48" height="48" /> | [StreetBarz] | <img src="https://lh3.googleusercontent.com/4fO6lwPVZ7ncBsvfVpfME9xm9Nn6ggtEffCrNDFbqZ9LXBkYxSbdALWmNxQLwkhv3p6G=w300-rw" width="48" height="48" /> | [Roast Me]
<img src="https://lh3.googleusercontent.com/Mtr7AqEN4WGJVih1rAjvvzl4rN5gYT2EwWxpVwczZXfRmixaQJBFnm-WBr8TkhGzpOA=w300-rw" width="48" height="48" /> | [Pipe] | <img src="https://lh3.googleusercontent.com/0fEpTqHqbVJ_0z7rdsG9wks5KiiaBjyA4MScLf19HSMhy8BR3vSX6agZ0NucyPMyow=w300-rw" width="48" height="48" /> | [Snagajob] | <img src="https://lh3.googleusercontent.com/SrowFwOEDERCrVqk7SaBwifZslMoqTk5iZ9gMKOafvD1KvlyYYLxvbNJXiiUjBSsLjk=w300-rw" width="48" height="48" /> | [Tatva Moksh Lakshya]

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
  [Saripaar X]: https://github.com/ragunathjawahar/saripaar-x
  [video]: https://youtu.be/6Os9RxVK76A?t=23
  [Tweet]: https://twitter.com/ragunathjawahar
  [Wikipedia]: https://play.google.com/store/apps/details?id=org.wikipedia
  [Wikipedia Beta]: https://play.google.com/store/apps/details?id=org.wikipedia.beta
  [Fetch]: https://play.google.com/store/apps/details?id=com.buywithfetch.android
  [Mizuno Baton]: https://play.google.com/store/apps/details?id=com.mizuno.baton
  [MomMe]: https://play.google.com/store/apps/details?id=org.harthosp.momme
  [HealtheMinder]: https://play.google.com/store/apps/details?id=org.hartfordhealthcare.healtheminder
  [Feelknit]: https://play.google.com/store/apps/details?id=com.qubittech.feelknit.app
  [StreetBarz]: https://play.google.com/store/apps/details?id=com.diofeher.StreetBarz
  [Roast Me]: https://play.google.com/store/apps/details?id=com.marsvard.roastcam
  [Pipe]: https://play.google.com/store/apps/details?id=com.pipeapp.pipe
  [Snagajob]: https://play.google.com/store/apps/details?id=com.snagajob.jobseeker
  [Tatva Moksh Lakshya]: https://play.google.com/store/apps/details?id=siesgst.edu.in.tml16
  [wiki]: https://github.com/ragunathjawahar/android-saripaar/wiki

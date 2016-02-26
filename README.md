# FilePickerLibrary

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FilePickerLibrary-blue.svg?style=flat-square)](http://android-arsenal.com/details/1/785)

[![Release](https://jitpack.io/v/DeveloperPaul123/FilePickerLibrary.svg)](https://jitpack.io/#DeveloperPaul123/FilePickerLibrary)

Simple library that allows for picking of files and directories. This is a clean and simple way to allow your user to easily select a file. This library is inspired by Android L and the new Material Design guidelines adding to its sleekness and beauty.

![image] (images/main_framed.png)

## A Quick Overview What's In
* Compatible down to API Level 16

## Include to Project
### Provide the Gradle Dependency
#### Step 1
Add the JitPack in your root `build.gradle` at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
#### Step 2
Add the dependency
```gradle
dependencies {
    compile 'com.github.alirezaaa:FilePickerLibrary:3.5.1'
}
```
### Clone or Download `.zip` file
Clone this repository or download the compressed file, then extract to your computer. Simply import the `library` module to your project.

## Usages
Use one of the following samples or simply compile and test the sample `app` provided:
### Material Theme Used (with Modern Builder)
```java
new FilePickerBuilder(this)
    .withColor(android.R.color.holo_blue_bright)
    .withRequest(Request.FILE)
    .withScope(Scope.ALL)
    .withMimeType(MimeType.JPEG)
    .useMaterialActivity(true)
    .launch(REQUEST_FILE);
```
### `image/png` Mime Type
```java
Intent filePicker = new Intent(this, FilePickerActivity.class);
filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_green_dark);
filePicker.putExtra(FilePickerActivity.MIME_TYPE, MimeType.PNG);
startActivityForResult(filePicker, REQUEST_FILE);
```
### Show as a Dialog
```java
Intent filePickerDialogIntent = new Intent(this, FilePickerActivity.class);
filePickerDialogIntent.putExtra(FilePickerActivity.THEME_TYPE, ThemeType.DIALOG);
filePickerDialogIntent.putExtra(FilePickerActivity.REQUEST, Request.FILE);
startActivityForResult(filePickerDialogIntent, REQUEST_FILE);
```
### Select a Directory
```java
Intent filePickerActivity = new Intent(this, FilePickerActivity.class);
filePickerActivity.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
filePickerActivity.putExtra(FilePickerActivity.REQUEST, Request.DIRECTORY);
filePickerActivity.putExtra(FilePickerActivity.INTENT_EXTRA_FAB_COLOR_ID, android.R.color.holo_green_dark);
startActivityForResult(filePickerActivity, REQUEST_DIRECTORY);
```
To have the result, you must override `onActivityResult(int, int, Intent)` method as I did below:

```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if ((requestCode == REQUEST_DIRECTORY) && (resultCode == RESULT_OK)) {
        Toast.makeText(this, "Directory Selected: " + data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH), Toast.LENGTH_LONG).show();
    } else if ((requestCode == REQUEST_FILE) && (resultCode == RESULT_OK)) {
        Toast.makeText(this, "File Selected: " + data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH), Toast.LENGTH_LONG).show();
    }
}
```
## Contributors
- [Paul T](mailto:developer.paul.123@gmail.com) (developer)
- [Alireza Eskandarpour Shoferi](https://twitter.com/enormoustheory) (contributor)

## License
    Copyright 2016 Paul T
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

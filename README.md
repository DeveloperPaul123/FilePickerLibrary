FilePickerLibrary
=================

Simple library that allows for picking of files and directories. This is a clean and simple way to allow your user to easily select a file. This library is inspired by Android L and the new Material Design guidelines adding to its sleekness and beauty.

<h2>Requirements</h2>
Min SDK Level is 14 or Android IceCreamSandwich

<h2>Dependency</h2>

Clone this repository or download the zip. Then extract to your computer. Simply import the library to your project but only include the FPlib module (that's where the library is). If you're using Android Studio then import a module and only include the FPlib module. 

<h2>Usage</h2>
Simply do the following.
```java
Intent filePickerIntent = new Intent(this, FilePickerActivity.class);
filePickerIntent.putIntExtra(FilePicker.REQUEST_CODE, FilePickerActivity.REQUEST_DIRECTORY);
startActivityForResult(filePickerIntent, FilePickerActivity.REQUEST_DIRECTORY); 
```
Make sure to add the int extra for your request code. You can also request for a file path instead of a directory. See the javadocs for more info. To get the file path do the following.

```java
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if(requestCode == FilePickerActivity.REQUEST_DIRECTORY 
    && resultCode == RESULT_OK) {
    
        String filePath = data.
                getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
                if(filePath != null) {
                //do something with filePath...
                }
  }
    super.onActivityResult(requestCode, resultCode, data);
  }
```

<h2>Customization</h2>
You can change the header background in the activity by adding resorce ids to your calling intent as int extras. Below is an example.

```java
Intent filePickerIntent = new Intent(this, FilePickerActivity.class);
filePickerIntent.putIntExtra(FilePicker.REQUEST_CODE, FilePickerActivity.REQUEST_DIRECTORY);
filePickerIntent.putIntExtra(FilePicker.INTENT_EXTRA_COLOR_ID, R.color.myColor);
startActivityForResult(filePickerIntent, FilePickerActivity.REQUEST_DIRECTORY); 
```

<h2>Todo</h2>
Push to Maven Central

<h2>Developed By</h2>
**Paul Tsouchlos**

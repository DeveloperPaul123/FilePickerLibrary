/*
 * Copyright 2014 Paul Tsouchlos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.developerpaul123.filepickerlibrary.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.devpaul.filepicker.R;
import com.devpaul.materiallibrary.views.MaterialFlatButton;
import com.github.developerpaul123.filepickerlibrary.FilePickerActivity;
import com.github.developerpaul123.filepickerlibrary.FilePickerBuilder;
import com.github.developerpaul123.filepickerlibrary.enums.MimeType;
import com.github.developerpaul123.filepickerlibrary.enums.Request;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;
import com.github.developerpaul123.filepickerlibrary.enums.ThemeType;


public class MainActivity extends AppCompatActivity {

    static int REQUEST_FILE = 10;
    static int REQUEST_DIRECTORY = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialFlatButton filePickerActivity = (MaterialFlatButton) findViewById(R.id.file_picker_activity);

        filePickerActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filePickerActivity = new Intent(MainActivity.this, FilePickerActivity.class);
                filePickerActivity.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
                filePickerActivity.putExtra(FilePickerActivity.REQUEST, Request.DIRECTORY);
                filePickerActivity.putExtra(FilePickerActivity.INTENT_EXTRA_FAB_COLOR_ID, android.R.color.holo_green_dark);
                startActivityForResult(filePickerActivity, REQUEST_DIRECTORY);
            }
        });

        MaterialFlatButton filePickerForFile = (MaterialFlatButton) findViewById(R.id.file_picker_return_file_path);

        filePickerForFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filePicker = new Intent(MainActivity.this, FilePickerActivity.class);
                filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
                filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
                filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_orange_dark);
                startActivityForResult(filePicker, REQUEST_FILE);
            }
        });

        MaterialFlatButton filePickerDialog = (MaterialFlatButton) findViewById(R.id.file_picker_dialog);
        filePickerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filePickerDialogIntent = new Intent(MainActivity.this, FilePickerActivity.class);
                filePickerDialogIntent.putExtra(FilePickerActivity.THEME_TYPE, ThemeType.DIALOG);
                filePickerDialogIntent.putExtra(FilePickerActivity.REQUEST, Request.FILE);
                startActivityForResult(filePickerDialogIntent, REQUEST_FILE);
            }
        });

        MaterialFlatButton filePickerMimePng = (MaterialFlatButton) findViewById(R.id.file_picker_mime_png);
        filePickerMimePng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filePicker = new Intent(MainActivity.this, FilePickerActivity.class);
                filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
                filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
                filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_green_dark);
                filePicker.putExtra(FilePickerActivity.MIME_TYPE, MimeType.PNG);
                startActivityForResult(filePicker, REQUEST_FILE);
            }
        });

        MaterialFlatButton newFilePicker = (MaterialFlatButton) findViewById(R.id.new_file_picker_activity);
        newFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new FilePickerBuilder(MainActivity.this).withColor(android.R.color.holo_blue_bright)
                        .withRequest(Request.FILE)
                        .withScope(Scope.ALL)
                        .withMimeType(MimeType.JPEG)
                        .useMaterialActivity(true)
                        .launch(REQUEST_FILE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_DIRECTORY) && (resultCode == RESULT_OK)) {
            Toast.makeText(this, "Directory Selected: " + data
                            .getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH),
                    Toast.LENGTH_LONG).show();
        } else if ((requestCode == REQUEST_FILE) && (resultCode == RESULT_OK)) {
            Toast.makeText(this, "File Selected: " + data
                            .getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH),
                    Toast.LENGTH_LONG).show();
        }
    }
}

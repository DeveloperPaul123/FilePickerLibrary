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
import android.widget.Button;
import android.widget.Toast;

import com.devpaul.filepicker.R;
import com.github.developerpaul123.filepickerlibrary.FilePickerActivity;
import com.github.developerpaul123.filepickerlibrary.FilePickerBuilder;
import com.github.developerpaul123.filepickerlibrary.enums.MimeType;
import com.github.developerpaul123.filepickerlibrary.enums.Request;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;
import com.github.developerpaul123.filepickerlibrary.enums.ThemeType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    static int REQUEST_FILE = 10;
    static int REQUEST_DIRECTORY = 11;
    @BindView(R.id.file_picker_activity)
    Button activitySample;
    @BindView(R.id.file_picker_return_file_path)
    Button returnFilePathSample;
    @BindView(R.id.file_picker_dialog)
    Button dialogSample;
    @BindView(R.id.file_picker_mime_png)
    Button mimePngSample;
    @BindView(R.id.new_file_picker_activity)
    Button materialSample;

    @OnClick(R.id.file_picker_mime_png)
    void mimePngSample() {
        Intent filePicker = new Intent(this, FilePickerActivity.class);
        filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
        filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
        filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_green_dark);
        filePicker.putExtra(FilePickerActivity.MIME_TYPE, MimeType.PNG);
        startActivityForResult(filePicker, REQUEST_FILE);
    }

    @OnClick(R.id.new_file_picker_activity)
    void materialSample() {
        new FilePickerBuilder(this).withColor(android.R.color.holo_blue_bright)
                .withRequest(Request.FILE)
                .withScope(Scope.ALL)
                .withMimeType(MimeType.JPEG)
                .useMaterialActivity(true)
                .launch(REQUEST_FILE);
    }

    @OnClick(R.id.file_picker_dialog)
    void dialogSample() {
        Intent filePickerDialogIntent = new Intent(this, FilePickerActivity.class);
        filePickerDialogIntent.putExtra(FilePickerActivity.THEME_TYPE, ThemeType.DIALOG);
        filePickerDialogIntent.putExtra(FilePickerActivity.REQUEST, Request.FILE);
        startActivityForResult(filePickerDialogIntent, REQUEST_FILE);
    }

    @OnClick(R.id.file_picker_activity)
    void activitySample() {
        Intent filePickerActivity = new Intent(this, FilePickerActivity.class);
        filePickerActivity.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
        filePickerActivity.putExtra(FilePickerActivity.REQUEST, Request.DIRECTORY);
        filePickerActivity.putExtra(FilePickerActivity.INTENT_EXTRA_FAB_COLOR_ID, android.R.color.holo_green_dark);
        startActivityForResult(filePickerActivity, REQUEST_DIRECTORY);
    }

    @OnClick(R.id.file_picker_return_file_path)
    void returnFilePathSample() {
        Intent filePicker = new Intent(this, FilePickerActivity.class);
        filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
        filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
        filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_orange_dark);
        startActivityForResult(filePicker, REQUEST_FILE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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

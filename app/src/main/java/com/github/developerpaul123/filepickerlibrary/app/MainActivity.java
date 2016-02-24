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

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devpaul.filepicker.R;
import com.devpaul.materiallibrary.views.MaterialFlatButton;
import com.github.developerpaul123.filepickerlibrary.FilePickerActivity;
import com.github.developerpaul123.filepickerlibrary.FilePickerBuilder;
import com.github.developerpaul123.filepickerlibrary.enums.FileType;
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
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainActivity.PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == REQUEST_DIRECTORY && resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "File Selected: " + data
                                .getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH),
                        Toast.LENGTH_LONG).show();
            } else if (requestCode == REQUEST_FILE && resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "File Selected: " + data
                                .getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH),
                        Toast.LENGTH_LONG).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            MaterialFlatButton filePickerActivity = (MaterialFlatButton) rootView.findViewById(R.id.file_picker_activity);

            filePickerActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent filePickerActivity = new Intent(getActivity(), FilePickerActivity.class);
                    filePickerActivity.putExtra(FilePickerActivity.SCOPE_TYPE, Scope.ALL);
                    filePickerActivity.putExtra(FilePickerActivity.REQUEST_CODE, Request.DIRECTORY);
                    filePickerActivity.putExtra(FilePickerActivity.INTENT_EXTRA_FAB_COLOR_ID, android.R.color.holo_green_dark);
                    startActivityForResult(filePickerActivity, REQUEST_DIRECTORY);
                }
            });

            MaterialFlatButton filePickerForFile = (MaterialFlatButton) rootView.findViewById(R.id.file_picker_return_file_path);

            filePickerForFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent filePicker = new Intent(getActivity(), FilePickerActivity.class);
                    filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, Scope.ALL);
                    filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
                    filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_orange_dark);
                    startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);
                }
            });

            MaterialFlatButton filePickerDialog = (MaterialFlatButton) rootView.findViewById(R.id.file_picker_dialog);
            filePickerDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent filePickerDialogIntent = new Intent(getActivity(), FilePickerActivity.class);
                    filePickerDialogIntent.putExtra(FilePickerActivity.THEME_TYPE, ThemeType.DIALOG);
                    filePickerDialogIntent.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
                    startActivityForResult(filePickerDialogIntent, FilePickerActivity.REQUEST_FILE);
                }
            });

            MaterialFlatButton filePickerMimePng = (MaterialFlatButton) rootView.findViewById(R.id.file_picker_mime_png);
            filePickerMimePng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent filePicker = new Intent(getActivity(), FilePickerActivity.class);
                    filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, Scope.ALL);
                    filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
                    filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_green_dark);
                    filePicker.putExtra(FilePickerActivity.MIME_TYPE, FileType.PNG);
                    startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);
                }
            });

            MaterialFlatButton newFilePicker = (MaterialFlatButton) rootView.findViewById(R.id.new_file_picker_activity);
            newFilePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new FilePickerBuilder(getActivity()).withColor(android.R.color.holo_blue_bright)
                            .withRequest(Request.FILE)
                            .withScopeType(Scope.ALL)
                            .withMimeType(FileType.JPEG)
                            .useMaterialActivity(true)
                            .launch(REQUEST_FILE);
                }
            });
            return rootView;
        }
    }
}

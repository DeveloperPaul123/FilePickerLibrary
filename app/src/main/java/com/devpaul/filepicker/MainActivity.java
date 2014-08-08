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

package com.devpaul.filepicker;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.FileType;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button filePickerActivity = (Button) rootView.findViewById(R.id.file_picker_activity);

            filePickerActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent filePickerActivity = new Intent(getActivity(), FilePickerActivity.class);
                    filePickerActivity.putExtra(FilePickerActivity.SCOPE_TYPE, FileType.ALL);
                    filePickerActivity.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_DIRECTORY);
                    startActivityForResult(filePickerActivity, FilePickerActivity.REQUEST_DIRECTORY);
                }
            });

            Button filePickerForFile = (Button) rootView.findViewById(R.id.file_picker_return_file_path);

            filePickerForFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent filePicker = new Intent(getActivity(), FilePickerActivity.class);
                    filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, FileType.ALL);
                    filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
                    filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, android.R.color.holo_orange_dark);
                    startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);
                }
            });
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(requestCode == FilePickerActivity.REQUEST_DIRECTORY && resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "File Selected: " + data
                        .getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH),
                        Toast.LENGTH_LONG).show();
            } else if (requestCode == FilePickerActivity.REQUEST_FILE && resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "File Selected: " + data
                        .getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH),
                        Toast.LENGTH_LONG).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

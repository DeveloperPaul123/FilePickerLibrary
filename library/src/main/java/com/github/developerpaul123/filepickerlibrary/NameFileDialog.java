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

package com.github.developerpaul123.filepickerlibrary;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Paul Tsouchlos
 */
public class NameFileDialog extends DialogFragment {

    private NameFileDialogInterface listener;
    private EditText fileName;

    public static NameFileDialog newInstance() {

        NameFileDialog nfd = new NameFileDialog();
        nfd.setCancelable(false);
        return nfd;

    }

    @Override
    public void onAttach(Activity activity) {
        try {
            listener = (NameFileDialogInterface) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog.title("New File");
        fileName = new EditText(getActivity());
        dialog.customView(fileName, false);
        dialog.positiveText("Done");
        dialog.negativeText("Cancel");
        dialog.autoDismiss(false);
        dialog.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                if (listener != null) {
                    listener.onReturnFileName(fileName.getText().toString());
                }
                dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                dialog.dismiss();
            }
        });

        return dialog.build();
    }
}

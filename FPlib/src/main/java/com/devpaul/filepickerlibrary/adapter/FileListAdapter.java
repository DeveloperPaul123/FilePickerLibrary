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

package com.devpaul.filepickerlibrary.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devpaul.filepickerlibrary.enums.FileScopeType;
import com.devpaul.filepickerlibrary.R;

import java.io.File;

/**
 * Created by Paul Tsouchlos
 */
public class FileListAdapter extends BaseAdapter {

    private Context mContext;
    private File[] files;
    private LayoutInflater inflater;
    private int selectedPos;

    private Drawable folderDrawable;

    private FileScopeType mFileType;

    public FileListAdapter(Context context, File[] fileArray, FileScopeType type) {
        this.mContext = context;
        this.files = fileArray;
        this.inflater = LayoutInflater.from(mContext);
        this.mFileType = type;
        selectedPos = -1;
        folderDrawable = mContext.getResources().getDrawable(R.drawable.ic_folder);
    }
    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int i) {
        return files[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setSelectedPosition(int pos){
        selectedPos = pos;
        // inform the view of this change
        notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPos;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null) {
            view = inflater.inflate(R.layout.file_list_item, null);
        }
        if(selectedPos == i) {
            view.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.card_detailing));
        } else {
            view.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.card));
        }

        TextView fileTitle = (TextView) view.findViewById(R.id.file_item_file_name);
        TextView fileInfo = (TextView) view.findViewById(R.id.file_item_file_info);
        ImageView fileImage = (ImageView) view.findViewById(R.id.file_item_image_view);

        if (mFileType == FileScopeType.ALL) {
            fileTitle.setText(files[i].getName());
            fileInfo.setText("" + files[i].length() + " bytes");
            if(files[i].isDirectory()) {
                fileImage.setBackgroundDrawable(folderDrawable);
            }

        } else if(mFileType == FileScopeType.DIRECTORIES) {
            if(files[i].isDirectory()) {
                fileImage.setBackgroundDrawable(folderDrawable);
                fileTitle.setText(files[i].getName());
                fileInfo.setText("" + files[i].length() + " bytes");
            }
        }



        return view;
    }
}

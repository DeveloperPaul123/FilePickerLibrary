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

package com.github.developerpaul123.filepickerlibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.developerpaul123.filepickerlibrary.R;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Paul Tsouchlos
 */
public class FileListAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<File> fileList;
    private final LayoutInflater inflater;
    private int selectedPos;
    private Bitmap bitmap;
    private final float iconPadding;
    private final Drawable folderDrawable;

    private final Scope mFileType;

    public FileListAdapter(Context context, File[] fileArray, Scope type) {
        mContext = context;
        fileList = new ArrayList<File>(Arrays.asList(fileArray));
        inflater = LayoutInflater.from(mContext);
        mFileType = type;
        selectedPos = -1;
        folderDrawable = mContext.getResources().getDrawable(R.drawable.fplib_ic_folder);
        iconPadding = mContext.getResources().getDimension(R.dimen.file_picker_lib_default_icon_padding);
        if (mFileType == Scope.DIRECTORIES) {
            for (int i = 0; i < fileList.size(); i++) {
                String extension = fileExt(fileList.get(i).getPath());
                if (extension != null) {
                    fileList.remove(i);
                }
            }
        }
    }

    /**
     * Returns the file extension of a file.
     *
     * @param url the file path
     * @return
     */
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    /**
     * From the google examples, decodes a bitmap as a byte array and then resizes it for the required
     * width and hieght.
     *
     * @param picture   the picture byte array
     * @param reqWidth  the required width
     * @param reqHeight the required height
     * @return a Bitmap
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] picture,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(picture, 0, picture.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Encodes a bitmap to a byte array.
     *
     * @param bitmap the bitmap to compress
     * @param format the compression format for the Bitmap
     * @return {@code byte[]} object
     */
    public static byte[] encodeBitmapToArray(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(format, 0, outputStream);
        return outputStream.toByteArray();

    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FileListAdapter.ViewHolder viewHolder;
        final int position = i;
        if (view == null) {
            view = inflater.inflate(R.layout.file_list_item, null);
            viewHolder = new FileListAdapter.ViewHolder();
            viewHolder.fileInfo = (TextView) view.findViewById(R.id.file_item_file_info);
            viewHolder.fileTitle = (TextView) view.findViewById(R.id.file_item_file_name);
            viewHolder.fileImage = (ImageView) view.findViewById(R.id.file_item_image_view);
            viewHolder.fileInfoButton = (ImageView) view.findViewById(R.id.file_item_file_info_button);
            view.setTag(viewHolder);
        } else {
            viewHolder = (FileListAdapter.ViewHolder) view.getTag();
        }

        if (selectedPos == i) {
            view.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.card_detailing));
        } else {
            view.setBackgroundColor(mContext.getResources()
                    .getColor(android.R.color.background_light));
        }

        viewHolder.fileInfoButton.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_action_info));
        viewHolder.fileInfoButton.setClickable(true);
        viewHolder.fileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View customView = LayoutInflater.from(v.getContext()).inflate(R.layout.file_info_layout, null);
                TextView fileSize = (TextView) customView.findViewById(R.id.file_info_size);
                TextView fileDate = (TextView) customView.findViewById(R.id.file_info_date_created);
                TextView filePath = (TextView) customView.findViewById(R.id.file_info_path);
                File file = fileList.get(position);
                if (!file.isDirectory()) {
                    fileSize.setText(String.format(
                            mContext.getString(R.string.file_picker_adapter_size_string), file.length()));
                }
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(file.lastModified());
                DateFormat df = DateFormat.getDateInstance();
                fileDate.setText(String.format(mContext.getString(R.string.file_picker_adapter_last_modified_string), df.format(cal.getTime())));
                filePath.setText(String.format(mContext.getString(R.string.file_picker_adapter_file_path_string), file.getAbsolutePath()));
                new MaterialDialog.Builder(v.getContext())
                        .title(String.format(mContext.getString(R.string.file_picker_file_info_dialog_file_path), fileList.get(position).getName()))
                        .customView(customView, true)
                        .show();
            }
        });

        if (mFileType == Scope.ALL) {
            viewHolder.fileTitle.setText(fileList.get(i).getName());
            if (!fileList.get(i).isDirectory()) {
                viewHolder.fileInfo.setText(String.format(mContext.getString(R.string.file_picker_adapter_file_size_only_string), fileList.get(i).length()));
            }
            String fileExt = fileExt(fileList.get(i).toString());
            if (fileList.get(i).isDirectory()) {
                viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_action_file_folder));
            } else {
                if (fileExt != null) {
                    if (fileExt.equalsIgnoreCase(".doc")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_doc_file));
                    } else if (fileExt.equalsIgnoreCase(".docx")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_doc_file));
                    } else if (fileExt.equalsIgnoreCase(".xls")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_xls_file));
                    } else if (fileExt.equalsIgnoreCase(".xlsx")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_xlsx_file));
                    } else if (fileExt.equalsIgnoreCase(".xml")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_xml_file));
                    } else if (fileExt.equalsIgnoreCase(".html")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_html_file));
                    } else if (fileExt.equalsIgnoreCase(".pdf")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_pdf_file));
                    } else if (fileExt.equalsIgnoreCase(".txt")) {
                        viewHolder.fileImage.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_txt_file));
                    } else if (fileExt.equalsIgnoreCase(".jpeg")) {
                        viewHolder.fileImage.setBackgroundDrawable(mContext.getResources()
                                .getDrawable(R.drawable.fplib_rectangle));
                        new BitmapWorkerTask(viewHolder.fileImage, Bitmap.CompressFormat.JPEG).execute(fileList.get(i));
                    } else if (fileExt.equalsIgnoreCase(".jpg")) {
                        viewHolder.fileImage.setBackgroundDrawable(mContext.getResources()
                                .getDrawable(R.drawable.fplib_rectangle));
                        new BitmapWorkerTask(viewHolder.fileImage, Bitmap.CompressFormat.JPEG).execute(fileList.get(i));
                    } else if (fileExt.equalsIgnoreCase(".png")) {
                        viewHolder.fileImage.setBackgroundDrawable(mContext.getResources()
                                .getDrawable(R.drawable.fplib_rectangle));
                        new BitmapWorkerTask(viewHolder.fileImage, Bitmap.CompressFormat.PNG).execute(fileList.get(i));
                    } else {
                        viewHolder.fileImage.setBackgroundDrawable(mContext.getResources()
                                .getDrawable(R.drawable.fplib_ic_default_file));
                    }
                }
            }
        } else if (mFileType == Scope.DIRECTORIES) {
            if (fileList.get(i).isDirectory()) {
                viewHolder.fileImage.setBackgroundDrawable(folderDrawable);
                viewHolder.fileTitle.setText(fileList.get(i).getName());
            }
        }
        return view;
    }

    private Drawable getFileDrawable(int fileResource) {
        Drawable firstLayer = mContext.getResources().getDrawable(fileResource);
        LayerDrawable drawable = new LayerDrawable(new Drawable[]{
                mContext.getResources().getDrawable(R.drawable.fplib_circle),
                firstLayer
        });

        drawable.setLayerInset(1, (int) iconPadding, (int) iconPadding,
                (int) iconPadding, (int) iconPadding);

        return drawable;
    }

    public int getSelectedPosition() {
        return selectedPos;
    }

    public void setSelectedPosition(int pos) {
        selectedPos = pos;
        // inform the view of this change
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView fileTitle;
        TextView fileInfo;
        ImageView fileImage;
        ImageView fileInfoButton;
    }

    /**
     * Class that handles the loading of a bitmap.
     */
    private class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte[] data;
        private final Bitmap.CompressFormat mFormat;

        public BitmapWorkerTask(ImageView imageView, Bitmap.CompressFormat format) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageView.setBackgroundDrawable(imageView.getContext()
                    .getResources().getDrawable(R.drawable.fplib_rectangle));
            imageViewReference = new WeakReference<ImageView>(imageView);
            mFormat = format;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            Bitmap mBitmap = BitmapFactory.decodeFile(params[0].getAbsolutePath());
            //check if bitmap is null here.
            if (mBitmap != null) {
                data = encodeBitmapToArray(mBitmap, mFormat);
                return decodeSampledBitmapFromByteArray(data, 54, 54);
            } else {
                return null;
            }
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference.get() != null && bitmap != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
    }

    private class GetFileSizeTask extends AsyncTask<Void, Void, Long> {

        private final WeakReference<TextView> textViewWeakReference;
        private final File file;
        private final String formatString;

        private GetFileSizeTask(TextView textView, File f, String string) {
            file = f;
            textViewWeakReference = new WeakReference<TextView>(textView);
            formatString = string;
        }

        private long getDirectorySize(File directory) {
            File[] files = directory.listFiles();
            int size = 0;
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        size += getDirectorySize(file);
                    } else {
                        size += file.length();
                    }
                }
            }
            return size;
        }        @Override
        protected Long doInBackground(Void... params) {
            return getDirectorySize(file);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            TextView textView = textViewWeakReference.get();
            if (textView != null) {
                textView.setText(String.format(formatString, aLong));
            }
        }


    }

}

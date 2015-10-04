package com.devpaul.filepickerlibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devpaul.filepickerlibrary.R;
import com.devpaul.filepickerlibrary.enums.FileScopeType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Paul on 10/3/2015.
 */
public class FileRecyclerViewAdapter extends RecyclerView.Adapter {

    private final Drawable folderDrawable;
    private int selectedPosition;
    private float iconPadding;
    private FileScopeType mFileType;
    private View.OnClickListener viewClickListener;

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileTitle;
        TextView fileInfo;
        ImageView fileImage;
        ImageView fileInfoButton;

        public FileViewHolder(View view) {
            super(view);
            fileInfo = (TextView) view.findViewById(R.id.file_item_file_info);
            fileTitle = (TextView) view.findViewById(R.id.file_item_file_name);
            fileImage = (ImageView) view.findViewById(R.id.file_item_image_view);
            fileInfoButton = (ImageView) view.findViewById(R.id.file_item_file_info_button);
        }
    }

    private List<File> fileList;
    private Context context;

    public FileRecyclerViewAdapter(Context context, File[] files, FileScopeType scopeType) {
        this.context = context;
        iconPadding = context.getResources().getDimension(R.dimen.file_picker_lib_default_icon_padding);
        this.mFileType = scopeType;
        selectedPosition = -1;
        folderDrawable = context.getResources().getDrawable(R.drawable.fplib_ic_folder);
        if(mFileType == FileScopeType.DIRECTORIES) {
            for(int i = 0; i < fileList.size(); i++) {
                String extension = fileExt(fileList.get(i).getPath());
                if(extension != null) {
                    Log.d("FILELISTADAPTER", "Ext: " + extension);
                    fileList.remove(i);
                }
            }
        }
    }

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        this.viewClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.file_list_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = getItem(position);
        FileViewHolder viewHolder = (FileViewHolder) holder;
        viewHolder.fileTitle.setText(file.getName());

        if(selectedPosition == position) {
            viewHolder.itemView.setBackgroundColor(context.getResources()
                    .getColor(R.color.card_detailing));
        } else {
            viewHolder.itemView.setBackgroundColor(context.getResources()
                    .getColor(android.R.color.background_light));
        }
        final int i = position;
        viewHolder.fileInfoButton.setBackgroundDrawable(getFileDrawable(R.drawable.fplib_ic_action_info));
        viewHolder.fileInfoButton.setClickable(true);
        viewHolder.fileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View customView = LayoutInflater.from(v.getContext()).inflate(R.layout.file_info_layout, null);
                TextView fileSize = (TextView) customView.findViewById(R.id.file_info_size);
                TextView fileDate = (TextView) customView.findViewById(R.id.file_info_date_created);
                TextView filePath = (TextView) customView.findViewById(R.id.file_info_path);
                File file = fileList.get(i);
                if (file.isDirectory()) {
                    new GetFileSizeTask(fileSize, file, "Size: %d bytes").execute();
                } else {
                    fileSize.setText("Size: " + file.length() + " bytes");
                }
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(file.lastModified());
                DateFormat df = SimpleDateFormat.getDateInstance();
                fileDate.setText("Last Modified: " + df.format(cal.getTime()));
                filePath.setText("Path: " + file.getAbsolutePath());
                new MaterialDialog.Builder(v.getContext())
                        .title("File: " + fileList.get(i).getName())
                        .customView(customView, true)
                        .show();
            }
        });

        if (mFileType == FileScopeType.ALL) {
            viewHolder.fileTitle.setText(fileList.get(i).getName());
            if(!fileList.get(i).isDirectory()) {
                viewHolder.fileInfo.setText("" + fileList.get(i).length() + " bytes");
            } else {
                new GetFileSizeTask(viewHolder.fileInfo, fileList.get(i), "%d bytes").execute();
            }
            String fileExt = fileExt(fileList.get(i).toString());
            if(fileList.get(i).isDirectory()) {
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
                        viewHolder.fileImage.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.fplib_rectangle));
                        new BitmapWorkerTask(viewHolder.fileImage, Bitmap.CompressFormat.JPEG).execute(fileList.get(i));
                    } else if (fileExt.equalsIgnoreCase(".jpg")) {
                        viewHolder.fileImage.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.fplib_rectangle));
                        new BitmapWorkerTask(viewHolder.fileImage, Bitmap.CompressFormat.JPEG).execute(fileList.get(i));
                    } else if (fileExt.equalsIgnoreCase(".png")) {
                        viewHolder.fileImage.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.fplib_rectangle));
                        new BitmapWorkerTask(viewHolder.fileImage, Bitmap.CompressFormat.PNG).execute(fileList.get(i));
                    } else {
                        viewHolder.fileImage.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.fplib_ic_default_file));
                    }
                }
            }
        } else if(mFileType == FileScopeType.DIRECTORIES) {
            if(fileList.get(i).isDirectory()) {
                viewHolder.fileImage.setBackgroundDrawable(folderDrawable);
                viewHolder.fileTitle.setText(fileList.get(i).getName());
                new GetFileSizeTask(viewHolder.fileInfo, fileList.get(i), "%d bytes").execute();
            }
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public File getItem(int i) {
        return fileList.get(i);
    }

    public void setSelectedPosition(int i) {
        this.selectedPosition = i;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return this.selectedPosition;
    }

    private Drawable getFileDrawable(int fileResource) {
        Drawable firstLayer = context.getResources().getDrawable(fileResource);
        LayerDrawable drawable = new LayerDrawable(new Drawable[] {
                context.getResources().getDrawable(R.drawable.fplib_circle),
                firstLayer
        });

        drawable.setLayerInset(1, (int) iconPadding, (int) iconPadding,
                (int)iconPadding, (int)iconPadding);

        return drawable;
    }

    /**
     * Returns the file extension of a file.
     * @param url the file path
     * @return
     */
    private String fileExt(String url) {
        if (url.indexOf("?")>-1) {
            url = url.substring(0,url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") );
            if (ext.indexOf("%")>-1) {
                ext = ext.substring(0,ext.indexOf("%"));
            }
            if (ext.indexOf("/")>-1) {
                ext = ext.substring(0,ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    /**
     * Class that handles the loading of a bitmap.
     */
    private class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte[] data;
        private Bitmap.CompressFormat mFormat;

        public BitmapWorkerTask(ImageView imageView, Bitmap.CompressFormat format) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageView.setBackgroundDrawable(imageView.getContext()
                    .getResources().getDrawable(R.drawable.fplib_rectangle));
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.mFormat = format;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            Bitmap mBitmap = BitmapFactory.decodeFile(params[0].getAbsolutePath());
            //check if bitmap is null here.
            if(mBitmap != null) {
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
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
    }

    /**
     * From the google examples, decodes a bitmap as a byte array and then resizes it for the required
     * width and hieght.
     * @param picture the picture byte array
     * @param reqWidth the required width
     * @param reqHeight the required height
     * @return a Bitmap
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] picture,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
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
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Encodes a bitmap to a byte array.
     * @param bitmap the bitmap to compress
     * @param format the compression format for the Bitmap
     * @return {@code byte[]} object
     */
    public static byte[] encodeBitmapToArray(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(format, 0, outputStream);
        return outputStream.toByteArray();

    }

    private class GetFileSizeTask extends AsyncTask<Void, Void, Long> {

        private WeakReference<TextView> textViewWeakReference;
        private File file;
        private String formatString;

        private GetFileSizeTask(TextView textView, File f, String string) {
            this.file = f;
            this.textViewWeakReference = new WeakReference<TextView>(textView);
            this.formatString = string;
        }

        @Override
        protected Long doInBackground(Void... params) {
            return getDirectorySize(file);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            TextView textView = textViewWeakReference.get();
            if(textView != null) {
                textView.setText(String.format(formatString, aLong));
            }
        }

        private long getDirectorySize(File directory) {
            File[] files = directory.listFiles();
            int size = 0;
            for(File file: files) {
                if(file.isDirectory()) {
                    size += getDirectorySize(file);
                } else {
                    size += file.length();
                }
            }
            return size;
        }
    }
}

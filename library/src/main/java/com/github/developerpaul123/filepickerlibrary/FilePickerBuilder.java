package com.github.developerpaul123.filepickerlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorRes;

import com.github.developerpaul123.filepickerlibrary.enums.FileType;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;

/**
 * Created by Paul on 11/23/2015.
 */
public class FilePickerBuilder {

    private final Context mContext;
    private boolean useMaterial;
    private Scope mScope = Scope.ALL;
    private int requestCode = FilePicker.REQUEST_FILE;
    private int color = android.R.color.holo_blue_bright;
    private FileType mimeType = FileType.NONE;

    /**
     * Builder class to build a filepicker activity.
     *
     * @param context the calling activity.
     */
    public FilePickerBuilder(Context context) {
        mContext = context;
    }

    /**
     * Set the scopetype of the file picker.
     *
     * @param type scope type. Can be DIRECTORIES or ALL.
     * @return the current builder instance.
     */
    public FilePickerBuilder withScopeType(Scope type) {
        mScope = type;
        return this;
    }

    /**
     * Set the request code of this. You can request a path to a file or
     * a directory.
     *
     * @param requestCode the request code can be FilePicker.DIRECTORY or FilePicker.FILE.
     * @return current instance of the builder.
     */
    public FilePickerBuilder withRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * Set the header color with a resource integer.
     *
     * @param color the color resource.
     * @return current instance of tbe builder.
     */
    public FilePickerBuilder withColor(@ColorRes int color) {
        this.color = color;
        return this;
    }

    /**
     * Set the file mime type. The will require the returned file type to match the
     * mime type.
     *
     * @param type the mime type.
     * @return current instance of the builder.
     */
    public FilePickerBuilder withMimeType(FileType type) {
        mimeType = type;
        return this;
    }

    /**
     * Set if you want to use the new material designed file picker.
     *
     * @param use true to use the material version. False otherwise, default is false.
     * @return current builder instance.
     */
    public FilePickerBuilder useMaterialActivity(boolean use) {
        useMaterial = use;
        return this;
    }

    @Deprecated
    public void launch() {
        Intent intent = build();
        ((Activity) mContext).startActivityForResult(intent, requestCode);
    }

    /**
     * Build the current intent.
     *
     * @return a filepicker intent.
     */
    public Intent build() {
        Intent filePicker = new Intent(mContext, useMaterial ? FilePicker.class : FilePickerActivity.class);
        filePicker.putExtra(FilePicker.SCOPE_TYPE, mScope);
        filePicker.putExtra(FilePicker.REQUEST_CODE, requestCode);
        filePicker.putExtra(FilePicker.INTENT_EXTRA_COLOR_ID, color);
        filePicker.putExtra(FilePicker.MIME_TYPE, mimeType);
        return filePicker;
    }

    /**
     * Builds and starts the intent with startActivityForResult() uses the request code you said as the request code for the activity result.
     *
     * @param requestCode
     */
    public void launch(int requestCode) {
        Intent intent = build();
        ((Activity) mContext).startActivityForResult(intent, requestCode);
    }
}

package com.github.developerpaul123.filepickerlibrary;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.developerpaul123.filepickerlibrary.adapter.FileRecyclerViewAdapter;
import com.github.developerpaul123.filepickerlibrary.enums.MimeType;
import com.github.developerpaul123.filepickerlibrary.enums.Request;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;
import com.github.developerpaul123.filepickerlibrary.enums.ThemeType;

import java.io.File;

/**
 * Created by Paul on 10/8/2015.
 */
public class FilePicker extends AppCompatActivity implements NameFileDialogInterface {

    /**
     * Constant value for adding the REQUEST int as an extra to the {@code FilePickerActivity}
     * {@code Intent}
     */
    public static final String REQUEST = "request";

    /**
     * Constant value for adding the SCOPE enum as an extra to the {@code FilePickerActivity}
     * {@code Intent} The default is {@code FileType.ALL} see
     * {@link Scope} for other types.
     */
    public static final String SCOPE = "scope";

    /**
     * Constant label value for sending a color id extra in the calling intent for this
     * {@code FilePickerActivity}
     */
    public static final String INTENT_EXTRA_COLOR_ID = "intentExtraColorId";

    /**
     * Constant label value for sending a drawable image id in the calling intent for this
     * {@code FilePickerActivity}
     */
    public static final String INTENT_EXTRA_DRAWABLE_ID = "intentExtraDrawableId";

    /**
     * Constant label value for sending a color id to be used for the floating action button.
     */
    public static final String INTENT_EXTRA_FAB_COLOR_ID = "intentExtraFabColorId";

    /**
     * Constant for retrieving the return file path in {@link #onActivityResult(int, int, Intent)}
     * If the result code is RESULT_OK then the file path will not be null. This should always be
     * checked though.
     * <p/>
     * Example:
     * <p/>
     * {@code
     * <p/>
     * protected void onActivityResult(int resultCode, int requestCode, Intent data) {
     * <p/>
     * if(resultCode == RESULT_OK && requestCode == FILEPICKER) {
     * String filePath = data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
     * <p/>
     * if(filePath != null) {
     * //do something with the string.
     * }
     * }
     * }
     * }
     */
    public static final String FILE_EXTRA_DATA_PATH = "fileExtraPath";
    /**
     * Constant used for passing a {@link ThemeType} enum
     * to this activity from the calling activity.
     */
    public static final String THEME_TYPE = "themeType";
    /**
     * Constant used for setting the mime type of the files that the user is supposed to choose.
     */
    public static final String MIME_TYPE = "mimeType";
    /**
     * Request code for app permissions.
     */
    private static final int REQUEST_FOR_READ_EXTERNAL_STORAGE = 101;
    private static final OvershootInterpolator interpolator = new OvershootInterpolator();
    /**
     * Current toolbar
     */
    Toolbar toolbar;

    /**
     * Floating action button.
     */
    FloatingActionButton fab;

    boolean isFabShowing;
    /**
     * Array of files
     */
    File[] files;
    /**
     * Recycler view for list of files.
     */
    private RecyclerView recyclerView;
    /**
     * Button that allows user to selet the file or directory.
     */
    private Button selectButton;
    /**
     * Allows user to enter a directory tree.
     */
    private Button openButton;
    /**
     * Container that encloses the two buttons above.
     */
    private LinearLayout buttonContainer;
    /**
     * Relative layout that holds the header.
     */
    private RelativeLayout header;
    /**
     * {@code Animation} for showing the buttonContainer
     */
    private Animation slideUp;
    /**
     * {@code Animation} for hiding the buttonContainer
     */
    private Animation slideDown;
    private Animation scaleIn;
    private Animation scaleOut;
    /**
     * {@code File} current directory
     */
    private File curDirectory;
    /**
     * {@code File} the directory one level up from the current one
     */
    private File lastDirectory;
    /**
     * {@code FileListAdapter} object
     */
    private FileRecyclerViewAdapter adapter;
    /**
     * The currently selected file
     */
    private File currentFile;
    private boolean areButtonsShowing;
    private final FileRecyclerViewAdapter.Callback callback = new FileRecyclerViewAdapter.Callback() {
        @Override
        public void onItemClicked(View item, int position) {

            if (position > 0 && position <= files.length - 1) {
                currentFile = files[position];
            }

            if (adapter.getSelectedPosition() == position) {
                hideButtons();
                adapter.setSelectedPosition(-1);
            } else {
                adapter.setSelectedPosition(position);
                showButtons();
            }
        }
    };
    /**
     * {@link Scope} enum
     */
    private Scope scopeType;
    /**
     * {@link ThemeType} enum for the type of them for this
     * activity.
     */
    private ThemeType themeType;
    /**
     * Actual mime type to be used for file browsing
     */
    private String mimeType;
    /**
     * Request code for this activity
     */
    private Request requestCode;
    /**
     * {@code Intent} used to send back the data to the calling activity
     */
    private Intent data;
    /**
     * {@code int} used to store the color resource id sent as an extra to this activity.
     */
    private int colorId;
    /**
     * {@code int} used to store the color for the floating action button.
     */
    private int fabColorId;
    /**
     * {@code int} used to store the drawable resource id sent as an extra to this activity.
     */
    private int drawableId;
    /**
     * (@code int) saves the previous first visible item when scrolling, used to make the buttons
     * disappear
     */
    private int mLastFirstVisibleItem;
    /**
     * (@code Context) saves the context of activity so that you can use it in onClick calls, etc.
     */
    private Context mContext;
    /**
     * Layout manager for the Recycler View.
     */
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.material_file_picker_activity_layout);
        recyclerView = (RecyclerView) findViewById(R.id.file_picker_recycler_view);
        toolbar = (Toolbar) findViewById(R.id.file_picker_base_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.file_picker_floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NameFileDialog nfd = NameFileDialog.newInstance();
                nfd.show(getFragmentManager(), "NameDialog");
            }
        });
        isFabShowing = true;
//        //get the theme type for this activity
//        themeType = (ThemeType) getIntent().getSerializableExtra(THEME_TYPE);
//        if (themeType == null) {
//            themeType = ThemeType.ACTIVITY;
//        }
//
//        setThemeType(themeType);

        areButtonsShowing = false;

        //set up the mime type for the file.
        Object rawMimeTypeParameter = getIntent().getExtras().get(MIME_TYPE);
        if (rawMimeTypeParameter instanceof String) {
            mimeType = (String) rawMimeTypeParameter;
        } else if (rawMimeTypeParameter instanceof MimeType) {
            mimeType = ((MimeType) rawMimeTypeParameter).getMimeType();
        } else {
            mimeType = null;
        }

        //set up the animations
        setUpAnimations();

        Intent givenIntent = getIntent();

        //get the scope type and request code. Defaults are all files and request of a directory
        //path.
        scopeType = (Scope) givenIntent.getSerializableExtra(SCOPE);
        if (scopeType == null) {
            //set default if it is null
            scopeType = Scope.ALL;
        }
        requestCode = (Request) givenIntent.getSerializableExtra(REQUEST);

        colorId = givenIntent.getIntExtra(INTENT_EXTRA_COLOR_ID, android.R.color.holo_blue_light);
        drawableId = givenIntent.getIntExtra(INTENT_EXTRA_DRAWABLE_ID, -1);
        fabColorId = givenIntent.getIntExtra(INTENT_EXTRA_FAB_COLOR_ID, -1);

        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new FileRecyclerViewAdapter(this, new File[0], scopeType, callback);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (Math.abs(dy) >= 5) {
                    if (dy > 0) {
                        toggleButton(false);
                    } else if (dy < 0) {
                        toggleButton(true);
                    }
                    if (areButtonsShowing) {
                        hideButtons();
                        adapter.setSelectedPosition(-1);
                        mLastFirstVisibleItem = firstVisibleItem;
                    } else if (firstVisibleItem > adapter.getSelectedPosition()) {
                        hideButtons();
                        adapter.setSelectedPosition(-1);
                    }
                } else {
                    mLastFirstVisibleItem = firstVisibleItem;
                }
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        initializeViews();

        //drawable has not been set so set the color.
        setHeaderBackground(colorId, drawableId);

        //check for proper permissions.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //Show permission rationale.
                    new MaterialDialog.Builder(this)
                            .title(R.string.file_picker_permission_rationale_dialog_title)
                            .content(R.string.file_picker_permission_rationale_dialog_content)
                            .positiveText(R.string.file_picker_ok)
                            .negativeText(R.string.file_picker_cancel)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    ActivityCompat.requestPermissions(FilePicker.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_FOR_READ_EXTERNAL_STORAGE);
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_FOR_READ_EXTERNAL_STORAGE);
                }
            }
        } else {
            init();
        }
    }

    /**
     * Toggles the material floating action button.
     *
     * @param visible
     */
    public void toggleButton(final boolean visible) {
        if (isFabShowing != visible) {
            isFabShowing = visible;
            int height = fab.getHeight();
            if (height == 0) {
                ViewTreeObserver vto = fab.getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = fab.getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggleButton(visible);
                            return true;
                        }
                    });
                    return;
                }
            }
            int translationY = visible ? 0 : height;
            fab.animate().setInterpolator(interpolator)
                    .setDuration(350)
                    .translationY(translationY);

            // On pre-Honeycomb a translated view is still clickable, so we need to disable clicks manually
            fab.setClickable(visible);
        }
    }

    @Override
    public void onBackPressed() {
        if (lastDirectory != null && !curDirectory.getPath()
                .equals(Environment.getExternalStorageDirectory().getPath())) {
            new UpdateFilesTask(this).execute(lastDirectory);
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //see if we got the permission.
            case REQUEST_FOR_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                return;
        }
    }

    /**
     * Initialize the current directory.
     */
    private void init() {
        curDirectory = new File(Environment.getExternalStorageDirectory().getPath());
        currentFile = new File(curDirectory.getPath());
        lastDirectory = curDirectory.getParentFile();

        if (curDirectory.isDirectory()) {
            new UpdateFilesTask(this).execute(curDirectory);
        } else {
            try {
                throw new Exception(getString(R.string.file_picker_directory_error));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes all the views in the layout of the activity.
     */
    private void initializeViews() {
        buttonContainer = (LinearLayout) findViewById(R.id.button_container);

        selectButton = (Button) findViewById(R.id.select_button);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestCode == Request.DIRECTORY) {
                    if (currentFile.isDirectory()) {
                        curDirectory = currentFile;
                        data = new Intent();
                        data.putExtra(FILE_EXTRA_DATA_PATH, currentFile.getAbsolutePath());
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        Snackbar.make(getWindow().getDecorView(), R.string.file_picker_snackbar_select_directory_message, Snackbar.LENGTH_SHORT).show();
                    }
                } else { //request code is for a file
                    if (currentFile.isDirectory()) {
                        curDirectory = currentFile;
                        new UpdateFilesTask(FilePicker.this).execute(curDirectory);
                    } else {
                        if (!TextUtils.isEmpty(mimeType)) {
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String requiredExtension = "." + mimeTypeMap.getExtensionFromMimeType(mimeType);
                            if (requiredExtension.equalsIgnoreCase(fileExt(currentFile.toString()))) {
                                data = new Intent();
                                data.putExtra(FILE_EXTRA_DATA_PATH, currentFile.getAbsolutePath());
                                setResult(RESULT_OK, data);
                                finish();
                            } else {
                                Snackbar.make(getWindow().getDecorView(), String.format(getString(R.string.file_picker_snackbar_select_file_ext_message), requiredExtension), Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            data = new Intent();
                            data.putExtra(FILE_EXTRA_DATA_PATH, currentFile.getAbsolutePath());
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                }
            }
        });

        openButton = (Button) findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFile.isDirectory()) {
                    curDirectory = currentFile;
                    toolbar.setTitle(curDirectory.getName());
                    new UpdateFilesTask(FilePicker.this).execute(curDirectory);
                } else {
                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    String file = currentFile.toString();
                    if (file != null) {
                        newIntent.setDataAndType(Uri.fromFile(currentFile), mimeType);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(newIntent);
                        } catch (ActivityNotFoundException e) {
                            Snackbar.make(getWindow().getDecorView(), R.string.file_picker_snackbar_no_file_type_handler, Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(getWindow().getDecorView(), R.string.file_picker_snackbar_no_read_type, Snackbar.LENGTH_SHORT).show();
                    }

                }
            }
        });

        buttonContainer.setVisibility(View.INVISIBLE);
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
     * Initializes the animations used in this activity.
     */
    private void setUpAnimations() {
        slideUp = AnimationUtils.loadAnimation(this,
                R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this,
                R.anim.slide_down);
        scaleIn = AnimationUtils.loadAnimation(this,
                R.anim.scale_in);
        scaleOut = AnimationUtils.loadAnimation(this,
                R.anim.scale_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that shows the sliding panel
     */
    private void showButtons() {
        if (!areButtonsShowing) {
            buttonContainer.clearAnimation();
            buttonContainer.startAnimation(slideUp);
            buttonContainer.setVisibility(View.VISIBLE);
            areButtonsShowing = true;
        }
    }

    /**
     * Method that hides the sliding panel
     */
    private void hideButtons() {
        if (areButtonsShowing) {
            buttonContainer.clearAnimation();
            buttonContainer.startAnimation(slideDown);
            buttonContainer.setVisibility(View.INVISIBLE);
            areButtonsShowing = false;
        }
    }

    @Override
    public void onReturnFileName(String fileName) {
        if (fileName.equalsIgnoreCase("") || fileName.isEmpty()) {
            fileName = null;
        }
        if (fileName != null && curDirectory != null) {
            File file = new File(curDirectory.getPath() + "//" + fileName);
            boolean created = false;
            if (!file.exists()) {
                created = file.mkdirs();
            }
            if (created) {
                new UpdateFilesTask(this).execute(curDirectory);
            }
        }
    }

    /**
     * Set the background color of the header
     *
     * @param colorResId    Resource Id of the color
     * @param drawableResId Resource Id of the drawable
     */
    private void setHeaderBackground(int colorResId, int drawableResId) {
        //TODO
    }

    /**
     * Class that updates the list view with a new array of files. Resets the adapter and the
     * directory title.
     */
    private class UpdateFilesTask extends AsyncTask<File, Void, File[]> {

        private final Context mContext;
        private File[] fileArray;
        private ProgressDialog dialog;
        private File directory;

        private UpdateFilesTask(Context context) {
            mContext = context;
        }

        @Override
        protected File[] doInBackground(File... files) {
            directory = files[0];
            fileArray = files[0].listFiles();
            return fileArray;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(getString(R.string.file_picker_progress_dialog_loading));
            dialog.setCancelable(false);
            dialog.show();
            hideButtons();
            recyclerView.setAdapter(null);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(File[] localFiles) {
            files = localFiles;
            if (directory.getPath().equalsIgnoreCase(Environment
                    .getExternalStorageDirectory().getPath())) {
                toolbar.setTitle(getString(R.string.file_picker_default_directory_title));

            } else {
                toolbar.setTitle(directory.getName());

            }
            lastDirectory = directory.getParentFile();
            curDirectory = directory;
//            adapter.notifyDataSetChanged();
//            for(int i = 0; i < files.length; i++) {
//                adapter.addFile(files[i]);
//            }
            if (files != null) {
                adapter = new FileRecyclerViewAdapter(FilePicker.this, files, scopeType, callback);
                //TODO: Fix this, figure out how to add and remove the header.
                recyclerView.setAdapter(adapter);
            }
            //make sure the button is showing.
            if (!isFabShowing) {
                toggleButton(true);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(files);
        }

        /**
         * Checks if the files contain a directory.
         *
         * @param files the files.
         * @return a boolean, true if there is a file that is a directory.
         */
        public boolean directoryExists(File[] files) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    return true;
                }
            }
            return false;
        }
    }


}

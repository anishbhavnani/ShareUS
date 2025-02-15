package com.share.in.main.model;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
* @Author Tom Farrell.   License: Whatever...
 *
 * The model for a simple File Manager.
*/
public class Model {
    private File mCurrentDir; //Our current location.
    private File mPreviousDir; //Our previous location.
    private Stack<File> mHistory; //Our navigation History.
    public static final String TAG = "Current dir"; //for debugging purposes.

    public Model() {
        init();
    }

    private void init() {
        mHistory = new Stack<>();
    
    /* The first thing I need to do is check to see if the device's storage is read/write accessible.  If it is not,
    then why bother continuing?  I guess I could do everything in read only mode, but I'd rather not.
    */
    
        //if the storage device is writable and readable, set the current directory to the external storage location.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mCurrentDir = Environment.getExternalStorageDirectory();
            
            Log.i(TAG, String.valueOf(mCurrentDir));
        } else {
            Log.i(TAG, "External storage unavailable");
        }
    }

    /* Now for the getters, setters, and utlity methods.*/
    
    //get the current directory.
    public File getmCurrentDir() {
        return mCurrentDir;
    }

    //set the current directory.
    public void setmCurrentDir(File mCurrentDir) {
        this.mCurrentDir = mCurrentDir;
    }

    //Returns whether or not we have a previous dir in our history.  If the stack is not empty, we have one.
    public boolean hasmPreviousDir() {
        return !mHistory.isEmpty();
    }

    //return the previous dir and remove it from the stack.
    public File getmPreviousDir() {
        return mHistory.pop();
    }

    //set the previous dir for navigation.
    public void setmPreviousDir(File mPreviousDir) {
        this.mPreviousDir = mPreviousDir;
        mHistory.add(mPreviousDir);

    }

    //Returns a sorted list of all dirs and files in a given directory.
    public List<File> getAllFiles(File f) {
        File[] allFiles = f.listFiles();

        /* I want all directories to appear before files do, so I have separate lists for both that are merged into one later.*/
        List<File> dirs = new ArrayList<>();
        List<File> files = new ArrayList<>();

        for (File file : allFiles) {
            if (file.isDirectory()) {
                dirs.add(file);
            } else {
                files.add(file);
            }
        }

        Collections.sort(dirs);
        Collections.sort(files);

        /*Both lists are sorted, so I can just add the files to the dirs list.
        This will give me a list of dirs on top and files on bottom. */
        dirs.addAll(files);

        return dirs;
    }
    
    //Try to determine the mime type of a file based on extension.
    public String getMimeType(Uri uri) {
        String mimeType = null;

        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());

        if (MimeTypeMap.getSingleton().hasExtension(extension)) {

            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }
}
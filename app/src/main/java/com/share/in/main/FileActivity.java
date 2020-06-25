package com.share.in.main;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import 	androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.share.in.R;

import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class FileActivity extends Fragment implements View.OnClickListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGES = 2;
    public static final int STORAGE_PERMISSION = 100;
    private static final int PERMISSION_REQUEST_CODE = 100;
    Button read;
    ArrayList<String> myList;
    ListView listview;
    ArrayList<FileModel> imageList;
    ArrayList<String> selectedImageList;
    RecyclerView imageRecyclerView, selectedImageRecyclerView;
    int[] resImg = {R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp};
    String[] title = {"Camera", "Folder"};
    String mCurrentPhotoPath;

    FileAdapter FileAdapter;
    String[] projection = {MediaStore.MediaColumns.DATA};
    File image;
    Button done;
String fpath;
    public FileActivity(){

    }
    public void setPath(String fpath){

        Log.d("FileActivity", "FileActivity path");
        this.fpath=fpath;
        init();
        getAllImages();
        setImageList();
        FileAdapter.notifyDataSetChanged();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_images, container, false);

        if (isStoragePermissionGranted()) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Log.e("FileActivity", "key123");
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        Log.e("FileActivity", "key456");
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            Log.e("FileActivity", "key789");
                            if(imageList.size()>0) {
                                fpath = imageList.get(0).getParentDir();
                                File f=new File(fpath);
                                setPath(f.getAbsoluteFile().getParent());
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
            imageRecyclerView = view.findViewById(R.id.recycler_view);
            // selectedImageRecyclerView = view.findViewById(R.id.selected_recycler_view);
            Bundle b = getArguments();
            if(b!=null)
                fpath=b.getString("position");

            selectedImageList = new ArrayList<>();
            imageList = new ArrayList<>();
            init();
            getAllImages();
            setImageList();

            //setSelectedImageList();
        }
        return view;
    }

    public void init(){
        selectedImageList = new ArrayList<>();
        imageList = new ArrayList<>();

    }

    public void setImageList(){
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FileAdapter = new  FileAdapter(getActivity(), imageList, FileActivity.this);
        imageRecyclerView.setAdapter(FileAdapter);

        FileAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                try {
                    setPath(imageList.get(position).getPath());
                } catch (ArrayIndexOutOfBoundsException ed) {
                    ed.printStackTrace();
                }
            }

        });
        //setImagePickerList();
    }

    public void setSelectedImageList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        selectedImageRecyclerView.setLayoutManager(layoutManager);
    }

    // Add Camera and Folder in ArrayList
    public void setImagePickerList(){
        for (int i = 0; i < resImg.length; i++) {
            FileModel FileModel = new FileModel();
            //FileModel.setResImg(resImg[i]);
            //FileModel.setTitle(title[i]);
            imageList.add(i, FileModel);
        }
        FileAdapter.notifyDataSetChanged();
    }

    // get all images from external storage
    public void getAllImages(){
        imageList.clear();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (true) {
                    String envPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

                    if(fpath!=null && fpath.length()>0)
                        envPath=fpath+"/";
                    Log.e("envpath", envPath);
                    File dir = new File(envPath);

                    if (dir.getAbsoluteFile().exists()) {
                        Log.e("path", dir.toString());
                        File list[] = dir.listFiles();
                        for (int i = 0; i < list.length; i++) {
                            FileModel FileModel = new FileModel();
                            FileModel.setPath(list[i].getAbsolutePath());
                            FileModel.setTitle(list[i].getName());
                            FileModel.setDirectory(list[i].isDirectory());
                            FileModel.setParentDir(envPath);
                            imageList.add(FileModel);
                        }

                    }
                    else
                        Log.e("error path", envPath +" : "+dir.exists());
                } else {
                    requestPermission(); // Code for permission
                }
            } else {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                if (dir.exists()) {
                    Log.d("path", dir.toString());
                    File list[] = dir.listFiles();
                    for (int i = 0; i < list.length; i++) {
                        FileModel FileModel = new FileModel();
                        FileModel.setPath(list[i].getAbsolutePath());
                        FileModel.setTitle(list[i].getName());
                        FileModel.setDirectory(list[i].isDirectory());
                        imageList.add(FileModel);
                    }
                }
            }
        }

    }

    // start the image capture Intent
    public void takePicture(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Continue only if the File was successfully created;
        File photoFile = createImageFile();
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void getPickImageIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGES);
    }

    // Add image in SelectedArrayList
    public void selectImage(int position) {
        // Check before add new item in ArrayList;
        if (!selectedImageList.contains(imageList.get(position).getTitle())) {
            imageList.get(position).setSelected(true);
            selectedImageList.add(0, imageList.get(position).getTitle());
            //selectedFileAdapter.notifyDataSetChanged();
            FileAdapter.notifyDataSetChanged();
        }
    }

    // Remove image from selectedImageList
    public void unSelectImage(int position) {
        for (int i = 0; i < selectedImageList.size(); i++) {
            if (imageList.get(position).getTitle() != null) {
                if (selectedImageList.get(i).equals(imageList.get(position).getTitle())) {
                    imageList.get(position).setSelected(false);
                    selectedImageList.remove(i);
                    //      selectedFileAdapter.notifyDataSetChanged();
                    FileAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public File createImageFile() {
        // Create an image file name
        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + dateTime + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    addImage(mCurrentPhotoPath);
                }
            } else if (requestCode == PICK_IMAGES) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        getImageFilePath(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    getImageFilePath(uri);
                }
            }
        }
    }

    // Get image file path
    public void getImageFilePath(Uri uri) {
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Log.e("getImageFilePath", projection[0]);
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null,    null, null);
        if (cursor != null) {
            while  (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (absolutePathOfImage != null) {
                    checkImage(absolutePathOfImage);
                } else {
                    checkImage(String.valueOf(uri));
                }
            }
        }
    }

    public void checkImage(String filePath) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList.contains(filePath)) {
            for (int pos = 0; pos < imageList.size(); pos++) {
                if (imageList.get(pos).getTitle() != null) {
                    if (imageList.get(pos).getTitle().equalsIgnoreCase(filePath)) {
                        imageList.remove(pos);
                    }
                }
            }
            addImage(filePath);
        }
    }

    // add image in selectedImageList and imageList
    public void addImage(String filePath) {
        FileModel FileModel = new FileModel();
        FileModel.setPath(filePath);
        FileModel.setSelected(true);
        imageList.add(2, FileModel);
        selectedImageList.add(0, filePath);
        //selectedFileAdapter.notifyDataSetChanged();
        FileAdapter.notifyDataSetChanged();
    }

    public boolean isStoragePermissionGranted() {
        int ACCESS_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((ACCESS_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
            getAllImages();
            setImageList();
            //setSelectedImageList();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),  android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to read  files. Please allow this Manifest.permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onClick(View v) {
        //int adapterPosition = getActivity().getAdapterPosition();
        Log.e("onBindViewHolder", "45678");
            /*if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }*/
        //new FileActivity(imageList.get(position).getPath());
    }
}
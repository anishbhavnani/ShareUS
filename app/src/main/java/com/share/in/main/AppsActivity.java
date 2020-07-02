package com.share.in.main;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.share.in.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AppsActivity extends Fragment {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGES = 2;
    public static final int STORAGE_PERMISSION = 100;
    PackageManager packageManager;
    ArrayList<AppModel> imageList;
    ArrayList<String> selectedImageList;
    RecyclerView imageRecyclerView, selectedImageRecyclerView;
    int[] resImg = {R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp};
    String[] title = {"Camera", "Folder"};
    String mCurrentPhotoPath;
    SelectedAppAdapter selectedAppAdapter;
    AppAdapter AppAdapter;
    String[] projection = {MediaStore.MediaColumns.DATA};
    File image;
    Button done;


    public AppsActivity(){
        AppModel.filePath=new HashSet<String>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_apps, container, false);

        if (isStoragePermissionGranted()) {
            imageRecyclerView = view.findViewById(R.id.recycler_view);
           // selectedImageRecyclerView = view.findViewById(R.id.selected_recycler_view);
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
        AppAdapter = new  AppAdapter(getActivity(), imageList);
        imageRecyclerView.setAdapter(AppAdapter);

        AppAdapter.setOnItemClickListener(new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                try {
                    if (!imageList.get(position).isSelected) {
                        selectImage(position);
                    } else {
                        unSelectImage(position);
                    }
                } catch (ArrayIndexOutOfBoundsException ed) {
                    ed.printStackTrace();
                }
            }

        });
        //setImagePickerList();
    }

    public void setSelectedImageList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        selectedImageRecyclerView.setLayoutManager(layoutManager);
        selectedAppAdapter = new SelectedAppAdapter(getActivity(), selectedImageList);
        selectedImageRecyclerView.setAdapter(selectedAppAdapter);
    }

    // Add Camera and Folder in ArrayList
    public void setImagePickerList(){
        for (int i = 0; i < resImg.length; i++) {
            AppModel AppModel = new AppModel();
            //AppModel.setResImg(resImg[i]);
            //AppModel.setTitle(title[i]);
            imageList.add(i, AppModel);
        }
        AppAdapter.notifyDataSetChanged();
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }
    // get all images from external storage
    public void getAllImages(){
        imageList.clear();

        packageManager = getActivity().getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);

        List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();

        /*To filter out System apps*/
        for(PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
            try {
                Drawable appIcon = packageManager
                        .getApplicationIcon(pi.applicationInfo);
                appIcon.setBounds(0, 0, 40, 40);

                String appName = packageManager.getApplicationLabel(
                        pi.applicationInfo).toString();
                Context ctx = getActivity().getApplicationContext();
                PackageManager pm = ctx.getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(ctx.getPackageName(), 0);
                String sourceApk = ai.publicSourceDir;
                File file = new File(sourceApk);
                long size = file.length();
                AppModel appModel=new AppModel();
                appModel.setTitle(appName);
                appModel.setImage(appIcon);
                appModel.setPath(pi.applicationInfo.publicSourceDir);
                appModel.setAppSize(android.text.format.Formatter.formatFileSize(getActivity().getApplicationContext(), size)+"");
                if (!b) {
                    packageList1.add(pi);
                }
                imageList.add(appModel);
            }
            catch(Exception e){
                Log.e("getAPkFilePath", e.toString());
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
            //selectedAppAdapter.notifyDataSetChanged();
            AppAdapter.notifyDataSetChanged();
        }
    }

    // Remove image from selectedImageList
    public void unSelectImage(int position) {
        for (int i = 0; i < selectedImageList.size(); i++) {
            if (imageList.get(position).getImage() != null) {
                if (selectedImageList.get(i).equals(imageList.get(position).getTitle())) {
                    imageList.get(position).setSelected(false);
                    selectedImageList.remove(i);
              //      selectedAppAdapter.notifyDataSetChanged();
                    AppAdapter.notifyDataSetChanged();
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
}
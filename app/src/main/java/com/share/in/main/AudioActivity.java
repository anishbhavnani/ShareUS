package com.share.in.main;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.share.in.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AudioActivity extends Fragment {

  public static final int REQUEST_IMAGE_CAPTURE = 1;
  public static final int PICK_IMAGES = 2;
  public static final int STORAGE_PERMISSION = 100;

  ArrayList<AudioModel> imageList;
  ArrayList<String> selectedImageList;
  RecyclerView imageRecyclerView, selectedImageRecyclerView;
  int[] resImg = {R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp};
  String[] title = {"Camera", "Folder"};
  String mCurrentPhotoPath;


  AudioAdapter imageAdapter;
  String[] projection = {MediaStore.MediaColumns.DATA};
  File image;
  Button done;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_audio, container, false);
//        selectedImageRecyclerView = view.findViewById(R.id.selected_recycler_view);
    selectedImageList = new ArrayList<>();
    imageList = new ArrayList<>();
    if (isStoragePermissionGranted()) {
      imageRecyclerView = view.findViewById(R.id.recycler_view);
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
    imageAdapter = new  AudioAdapter(getActivity(), imageList);
    imageRecyclerView.setAdapter(imageAdapter);

    imageAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
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
    //selectedImageAdapter = new SelectedAudioAdapter(getActivity(), selectedImageList);
    //selectedImageRecyclerView.setAdapter(selectedImageAdapter);
  }

  /* Add Camera and Folder in ArrayList
  public void setImagePickerList(){
      for (int i = 0; i < resImg.length; i++) {
          AudioModel imageModel = new AudioModel();
          //imageModel.setResImg(resImg[i]);
          //imageModel.setTitle(title[i]);
          imageList.add(i, imageModel);
      }
      imageAdapter.notifyDataSetChanged();
  }
*/
  // get all images from external storage
  public void getAllImages(){
    imageList.clear();
    String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,    // filepath of the audio file
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.AudioColumns.SIZE};
    Log.e("getAllAudio", projection[0]);
    final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
    Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null,null, null);

    while (cursor.moveToNext()) {
      //String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
      String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
      String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
      long timeInMillisec = Long.parseLong(duration);
      duration=convertMillieToHMmSs(timeInMillisec);
      // int thum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Thumbnails.DATA);
      AudioModel AudioModel = new AudioModel();
      // AudioModel.setImage(absolutePathOfImage);
      //AudioModel.setThum(cursor.getString(thum));
      String sizeColInd = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
      long fileSize = Long.parseLong(sizeColInd);
      sizeColInd=android.text.format.Formatter.formatFileSize(getActivity().getApplicationContext(), fileSize);
      AudioModel.setTitle(title);
      AudioModel.setSize(sizeColInd);
      AudioModel.setDuration(duration);
      imageList.add(AudioModel);
    }
    cursor.close();
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
    if (!selectedImageList.contains(imageList.get(position).getImage())) {
      imageList.get(position).setSelected(true);
      selectedImageList.add(0, imageList.get(position).getImage());
      //selectedImageAdapter.notifyDataSetChanged();
      imageAdapter.notifyDataSetChanged();
    }
  }

  // Remove image from selectedImageList
  public void unSelectImage(int position) {
    for (int i = 0; i < selectedImageList.size(); i++) {
      if (imageList.get(position).getImage() != null) {
        if (selectedImageList.get(i).equals(imageList.get(position).getImage())) {
          imageList.get(position).setSelected(false);
          selectedImageList.remove(i);
          //   selectedImageAdapter.notifyDataSetChanged();
          imageAdapter.notifyDataSetChanged();
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
        if (imageList.get(pos).getImage() != null) {
          if (imageList.get(pos).getImage().equalsIgnoreCase(filePath)) {
            imageList.remove(pos);
          }
        }
      }
      addImage(filePath);
    }
  }

  // add image in selectedImageList and imageList
  public void addImage(String filePath) {
    AudioModel imageModel = new AudioModel();
    imageModel.setImage(filePath);
    imageModel.setSelected(true);
    imageList.add(2, imageModel);
    selectedImageList.add(0, filePath);
    // selectedImageAdapter.notifyDataSetChanged();
    imageAdapter.notifyDataSetChanged();
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

  public static String convertMillieToHMmSs(long millie) {
    long seconds = (millie / 1000);
    long second = seconds % 60;
    long minute = (seconds / 60) % 60;
    long hour = (seconds / (60 * 60)) % 24;

    String result = "";
    if (hour > 0) {
      return String.format("%02d:%02d:%02d", hour, minute, second);
    }
    else {
      return String.format("%02d:%02d" , minute, second);
    }

  }
}
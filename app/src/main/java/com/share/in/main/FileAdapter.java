package com.share.in.main;

import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.share.in.R;
import com.share.in.main.utils.DownloadImageTask;
import com.share.in.main.utils.ImageDownloaderTask;
import com.share.in.main.utils.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.SparseBooleanArray;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private FileActivity fa;
    private ArrayList<FileModel> imageList;
    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private static OnItemClickListener onItemClickListener;
    public ImageLoader imageLoader;
    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;

    public FileAdapter(Context context, ArrayList<FileModel> imageList,FileActivity fa) {
        this.context = context;
        this.imageList = imageList;
        //imageLoader=new ImageLoader(context);
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == IMAGE_LIST) {;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
            return new ImageListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_picker_list, parent, false);
            return new ImagePickerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_LIST;
    }

    private void loadImagesAsync(final Map<Integer, String> bindings,ImageView view) {
        for (final Map.Entry<Integer, String> binding :
                bindings.entrySet()) {
            new DownloadImageTask(new DownloadImageTask.Listener() {
                @Override
                public void onImageDownloaded(final Bitmap bitmap) {
                    view.setImageBitmap(bitmap);
                }
                @Override
                public void onImageDownloadError() {
                    Log.e(TAG, "Failed to download image from "
                            + binding.getKey());
                }
            }).execute(binding.getValue());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public  void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == IMAGE_LIST) {
            ;
            final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;

            viewHolder.title.setText(imageList.get(position).getTitle());
            viewHolder.position = position;
            viewHolder.size.setText(imageList.get(position).getSize());
            if(FileModel.filePath.contains(imageList.get(position).getPath()+"/")) {
                    viewHolder.checkBox.setChecked(true);
            }

            if (imageList.get(position).getFileType().equals("image") || imageList.get(position).getFileType().equals("video")){
                 Glide.with(context)
                         .load(new File(imageList.get(position).getPath()))
                        .placeholder(R.color.codeGray)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.image);
                 if(imageList.get(position).getFileType().equals("video"))
                    viewHolder.play_button.setVisibility(View.VISIBLE);
                 else
                     viewHolder.play_button.setVisibility(View.INVISIBLE);
        }
            else {

                Glide.with(context)
                        .load(imageList.get(position).getImage())
                        .placeholder(R.color.codeGray)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.image);

                viewHolder.play_button.setVisibility(View.INVISIBLE);
            }
                if(imageList.get(position).getFileType().equals("folder"))
                    viewHolder.size.setVisibility(View.INVISIBLE);
            viewHolder.bind(position);
        }
    }

    private MultiTransformation getImageTransformation() {
        return new MultiTransformation<>(new FitCenter());
    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image,play_button;
        CheckBox checkBox;
        TextView title,duration,size;
        int position;
        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            play_button= itemView.findViewById(R.id.play1);
            checkBox = itemView.findViewById(R.id.circle);
            title=itemView.findViewById(R.id.vidname);
            size=itemView.findViewById(R.id.vidsize);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
                itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageList.get(getAdapterPosition()).isDirectory())
                        onItemClickListener.onItemClick(getAdapterPosition(), v);
                    else
                        checkBox.setChecked(!checkBox.isChecked());
                }
            });


            /*
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if(isChecked){
                        FileModel.filePath.add(imageList.get(position).getPath());
                        Log.e("FileModel Path : ",FileModel.filePath.size()+ "");
                        if(imageList.get(position).isDirectory())
                            getFilesRecursive(new File(imageList.get(position).getPath()));
                        for(String str:FileModel.filePath)
                            Log.e("Selected Path : ",str);

                    } else {
                        if(imageList!=null) {
                            Log.e("FilePath : ", (FileModel.filePath!=null) + "");
                            FileModel.filePath.removeIf((String fpath) -> fpath.startsWith(imageList.get(position).getPath()));
                            FileModel.filePath.remove(imageList.get(position).getParentDir());
                            for (String str : FileModel.filePath)
                                Log.e("UnSelected Path : ", str);
                        }
                    }

                }
            }); */
            checkBox.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    for(String str:FileModel.filePath)
                        Log.e("FilePath ",str);
                    if(checkBox.isChecked()){
                        if(imageList.get(position).isDirectory())
                            FileModel.filePath.add(imageList.get(position).getPath()+"/");
                        else
                            FileModel.filePath.add(imageList.get(position).getPath()+"/");
                        if(imageList.get(position).isDirectory())
                            getFilesRecursive(new File(imageList.get(position).getPath()));

                    } else {
                        Log.e("onBindViewHolder", checkBox.isChecked() + "");
                        Log.e("FileModel Path : ",imageList.get(position).getPath()+"/");
                        Log.e("FilePath  Parent: ", imageList.get(position).getParentDir());
                        if(imageList!=null) {
                            FileModel.filePath.removeIf((String fpath) -> fpath.startsWith(imageList.get(position).getPath()));
                            FileModel.filePath.remove(imageList.get(position).getParentDir());
                        }
                    }
                }
            });
        }

        private void getFilesRecursive(File pFile)
        {
            for(File files : pFile.listFiles())
            {
                FileModel.filePath.add(files.getAbsolutePath()+"/");
                if(files.isDirectory())
                    getFilesRecursive(files);

            }
        }
        void bind(int position) {
            // use the sparse boolean array to check

this.position=position;
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.e("onBindViewHolder", "456");
        }
    }

    private void getFilesRecursive(File pFile)
    {
        for(File files : pFile.listFiles())
        {
            FileModel.filePath.add(files.getAbsolutePath()+"/");
            if(files.isDirectory())
                getFilesRecursive(files);

        }
    }
    public class ImagePickerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ImagePickerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

}

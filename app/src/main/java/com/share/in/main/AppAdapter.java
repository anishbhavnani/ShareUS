
package com.share.in.main;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.share.in.R;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<AppModel> imageList;
    private static OnItemClickListener onItemClickListener;
    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;

    public AppAdapter(Context context, ArrayList<AppModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list, parent, false);
            return new ImageListViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_LIST;
    }

    @Override
    public  void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == IMAGE_LIST) {;
            final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;
            viewHolder.appname.setText(imageList.get(position).getTitle());
            viewHolder.image.setImageDrawable(imageList.get(position).getImage());
            viewHolder.appsize.setText(imageList.get(position).getAppSize());
            if (imageList.get(position).isSelected()) {;
                viewHolder.checkBox.setChecked(true);
            } else {;
                viewHolder.checkBox.setChecked(false);
            }
        } else {;
            ImagePickerViewHolder viewHolder = (ImagePickerViewHolder) holder;
            viewHolder.image.setImageResource(imageList.get(position).getResImg());
            viewHolder.title.setText(imageList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView appname,appsize;
        ImageView image;
        CheckBox checkBox;

        public ImageListViewHolder(View itemView) {
            super(itemView);
            appname = itemView.findViewById(R.id.appname);
            image = itemView.findViewById(R.id.image);
            appsize = itemView.findViewById(R.id.appsize);
            checkBox = itemView.findViewById(R.id.circle);
            itemView.setOnClickListener(this);
        }
        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                checkBox.setChecked(false);}
            else {
                checkBox.setChecked(true);
            }

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }
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
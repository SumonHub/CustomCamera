package org.sumon.customcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SumOn on 15,June,2020
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    Context context;
    List<Bitmap> bitmapList = new ArrayList<>();
    TYPE type;

    public PhotoAdapter(Context applicationContext, List<Bitmap> bitmaps, TYPE type) {
        this.context = applicationContext;
        this.bitmapList = bitmaps;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (type == TYPE.PREVIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photo_preview, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photo, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.photo.setImageBitmap(bitmapList.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                FullScreenDialog dialog = new FullScreenDialog();
                dialog.setArguments(bundle);
                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                dialog.show(ft, "DialogFragmentLogin");
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public enum TYPE {
        LISTVIEW,
        PREVIEW
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
        }
    }

}

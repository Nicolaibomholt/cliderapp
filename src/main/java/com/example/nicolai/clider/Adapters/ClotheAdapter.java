package com.example.nicolai.clider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nicolai.clider.R;
import com.example.nicolai.clider.model.Clothe;

import java.util.ArrayList;

public class ClotheAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Clothe> clotheArrayList;
    private Clothe clothe;

    public ClotheAdapter(Context context, ArrayList<Clothe> clotheArrayList) {
        this.context = context;
        this.clotheArrayList = clotheArrayList;
    }

    @Override
    public int getCount() {
        if(clotheArrayList!=null){
            return clotheArrayList.size();
        } return 0;
    }

    @Override
    public Object getItem(int position) {
        if (clotheArrayList!=null){
            return clotheArrayList.get(position);
        }return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Custom adapter for our listview, setting up image, text + location sold.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater clotheInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = clotheInflater.inflate(R.layout.clothe_adapter_item, null);
        }
        clothe = clotheArrayList.get(position);
        if (clothe!=null){
            TextView clotheType = (TextView)convertView.findViewById(R.id.clothesListType);
            clotheType.setText(clothe.getName());

            TextView clotheDescription = (TextView)convertView.findViewById(R.id.clothesListDescription);
            clotheDescription.setText(clothe.getLocation());

            ImageView clotheListImage = (ImageView)convertView.findViewById(R.id.clotheListImage);
            Glide.with(context).load(clothe.getImageUrl()).override(200,200).fitCenter().into(clotheListImage);
        }
        return convertView;
    }
}

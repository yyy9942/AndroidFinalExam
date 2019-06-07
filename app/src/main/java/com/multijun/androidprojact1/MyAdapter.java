package com.multijun.androidprojact1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    ArrayList<Mail> arSrc;
    Context maincon;
    LayoutInflater inflater;
    int layout;

    public MyAdapter(Context context, int alayout, ArrayList<Mail> aarSrc){
        maincon = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }
    public int getCount(){
        return arSrc.size();
    }
    public Mail getItem(int position){
        return arSrc.get(position);
    }
    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);
        }


        TextView title = (TextView)convertView.findViewById(R.id.mailTitle);
        TextView content = (TextView)convertView.findViewById(R.id.mailSender);
        TextView time = (TextView)convertView.findViewById(R.id.mailTime);
        ImageView imageViewMail = (ImageView)convertView.findViewById(R.id.imageViewMail);
        title.setText(arSrc.get(position).getTitle());
        content.setText(arSrc.get(position).getSender());
        time.setText(arSrc.get(position).getSendTime());
        if(arSrc.get(position).getOpen() == 0){
            imageViewMail.setImageResource(R.drawable.mail);
        }else if(arSrc.get(position).getOpen() == 1){
            imageViewMail.setImageResource(R.drawable.mail2);
        }else if(arSrc.get(position).getOpen() == 2){
            imageViewMail.setImageResource(R.drawable.ic_send);
        }
        return convertView;
    }
}

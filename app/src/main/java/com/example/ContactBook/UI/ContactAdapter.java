package com.example.ContactBook.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ContactBook.Model.Contactitem;
import com.example.ContactBook.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context context;
    private List<Contactitem> dataList;


    public ContactAdapter(Context context) {
        this.context = context;
        dataList = new ArrayList<>();
    }

    public void addItem(Contactitem item) {
        dataList.add(item);
    }


    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void remove(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size());
    }

    public int getId(int position) {
        return dataList.get(position).getId();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView phoneTextView, nameTextView;
        ImageView itemImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_tv);
            phoneTextView = (TextView) itemView.findViewById(R.id.phone_tv);
            itemImageView = (ImageView) itemView.findViewById(R.id.user_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Contactitem item= dataList.get(getAdapterPosition());
                    Intent send = new Intent(context, DetailViewActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("serialzable", (Serializable) item);
                    send.putExtras(b);
                    send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(send);
                }
            });

        }

        public void setData(Contactitem item) {
            nameTextView.setText(item.getName());
            phoneTextView.setText(item.getPhone());

        }


    }

}
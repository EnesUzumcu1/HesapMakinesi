package com.example.hesapmakinesi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hesapmakinesi.Object.Hesap;
import com.example.hesapmakinesi.databinding.ListItemBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<Hesap> hesapArrayList;
    Context context;
    private ClickListener listener;

    public CustomAdapter (ArrayList<Hesap> hesapArrayList,Context context, ClickListener listener){
        this.hesapArrayList = hesapArrayList;
        this.context = context;
        this.listener = listener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ListItemBinding binding;

        MyViewHolder (ListItemBinding b){
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        return new MyViewHolder(ListItemBinding.inflate(layoutInflater));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        double adet =  hesapArrayList.get(position).getAdet();
        double fiyat =  hesapArrayList.get(position).getFiyat();


        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(340);

        holder.binding.textView.setText(position+1+")");

        holder.binding.textViewAdetListItem.setText(df.format(adet));
        holder.binding.textViewFiyatListItem.setText(df.format(fiyat));

        holder.binding.buttonSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClicked(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return hesapArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}

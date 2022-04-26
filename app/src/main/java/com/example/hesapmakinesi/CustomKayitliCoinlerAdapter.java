package com.example.hesapmakinesi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hesapmakinesi.Object.KayitliCoinler;
import com.example.hesapmakinesi.databinding.ListItemKayitliCoinlerBinding;

import java.util.ArrayList;

public class CustomKayitliCoinlerAdapter extends RecyclerView.Adapter<CustomKayitliCoinlerAdapter.MyViewHolder>{

    ArrayList<KayitliCoinler> kayitliCoinlerArrayList;
    Context context;
    String id;
    ClickListenerKayitliCoinler listener;

    public CustomKayitliCoinlerAdapter (ArrayList<KayitliCoinler> kayitliCoinlerArrayList,Context context, ClickListenerKayitliCoinler listener){
        this.kayitliCoinlerArrayList = kayitliCoinlerArrayList;
        this.context = context;
        this.listener = listener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ListItemKayitliCoinlerBinding binding;

        MyViewHolder (ListItemKayitliCoinlerBinding b){
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull

    @Override
    public CustomKayitliCoinlerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        return new CustomKayitliCoinlerAdapter.MyViewHolder(ListItemKayitliCoinlerBinding.inflate(layoutInflater));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomKayitliCoinlerAdapter.MyViewHolder holder, int position) {

        holder.binding.textViewKayitliAdet.setText(kayitliCoinlerArrayList.get(position).getAdet()+" adet alım");
        holder.binding.textViewKayitliCoinAdi.setText(kayitliCoinlerArrayList.get(position).getIsim());

        holder.binding.textView.setText(position+1+")");

        holder.binding.buttonSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClickedKayitliCoinler(kayitliCoinlerArrayList.get(position),position,1);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClickedKayitliCoinler(kayitliCoinlerArrayList.get(position),position,2);
            }
        });

    }


    @Override
    public int getItemCount() {
        return kayitliCoinlerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

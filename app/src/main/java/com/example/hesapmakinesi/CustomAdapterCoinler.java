package com.example.hesapmakinesi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hesapmakinesi.Object.Coinler;
import com.example.hesapmakinesi.databinding.ListItemCoinBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdapterCoinler extends RecyclerView.Adapter<CustomAdapterCoinler.MyViewHolder> implements Filterable {

    ArrayList<Coinler> coinlerArrayList;
    ArrayList<Coinler> coinlerArrayListfull;
    Context context;
    private ClickListener listener;

    public CustomAdapterCoinler(ArrayList<Coinler> coinlerArrayList, Context context, ClickListener listener) {
        this.coinlerArrayList = coinlerArrayList;
        this.context = context;
        this.listener = listener;
        coinlerArrayListfull = new ArrayList<>(coinlerArrayList);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ListItemCoinBinding binding;

        MyViewHolder(ListItemCoinBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull

    @Override
    public CustomAdapterCoinler.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new CustomAdapterCoinler.MyViewHolder(ListItemCoinBinding.inflate(layoutInflater));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterCoinler.MyViewHolder holder, int position) {
        String isim = coinlerArrayList.get(position).getIsim();
        double fiyat = coinlerArrayList.get(position).getFiyat();


        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(340);

        holder.binding.textView.setText(position + 1 + ")");

        holder.binding.textViewCoinAdi.setText(isim);
        holder.binding.textViewCoinFiyati.setText(String.format("%s $", df.format(fiyat)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClickedCoinlerList(coinlerArrayList.get(position));
            }
        });

    }

    @Override
    public Filter getFilter() {
        return filtre;
    }

    private Filter filtre = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Coinler> filtereliListe = new ArrayList<>();
            if(charSequence ==null || charSequence.length() == 0){
                filtereliListe.addAll(coinlerArrayListfull);
            }
            else{
                String filterPattern = charSequence.toString().toUpperCase().trim();
                for(Coinler coin : coinlerArrayListfull){
                    if(coin.getIsim().toUpperCase().contains(filterPattern)){
                        filtereliListe.add(coin);
                    }
                }
            }
            FilterResults results =new FilterResults();
            results.values = filtereliListe;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            coinlerArrayList.clear();
            coinlerArrayList.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };


    @Override
    public int getItemCount() {
        return coinlerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
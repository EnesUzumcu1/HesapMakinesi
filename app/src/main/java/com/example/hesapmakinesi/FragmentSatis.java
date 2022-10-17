package com.example.hesapmakinesi;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hesapmakinesi.Object.Coinler;
import com.example.hesapmakinesi.Object.Hesap;
import com.example.hesapmakinesi.databinding.FragmentSatisSayfasiBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSatis extends Fragment implements ClickListener {
    private FragmentSatisSayfasiBinding binding;
    RecyclerView.LayoutManager manager;
    ArrayList<Hesap> hesapArrayList;
    DecimalFormat decimalFormat;
    DecimalFormat decimalFormatYuzde;
    CustomAdapter adapter;
    String coinYukle;

    static ArrayList<Coinler> coinerArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSatisSayfasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        atamalar();
        loadData(coinYukle);
        buildrecyclerview();
        ortalamaHesapla();

        binding.buttonEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bosMuKontrol1 = !binding.editTextNumberDecimalAdet.getText().toString().equals("");
                boolean bosMuKontrol2 = !binding.editTextNumberDecimalFiyat.getText().toString().equals("");
                boolean noktaKontrol1 = !binding.editTextNumberDecimalAdet.getText().toString().equals(".");
                boolean noktaKontrol2 = !binding.editTextNumberDecimalFiyat.getText().toString().equals(".");
                if (bosMuKontrol1 && bosMuKontrol2 && noktaKontrol1 && noktaKontrol2) {
                    veriEkle();
                    adapter.notifyDataSetChanged();
                    binding.recyclerview.smoothScrollToPosition(adapter.getItemCount());

                    binding.editTextNumberDecimalAdet.setText("");
                    binding.editTextNumberDecimalFiyat.setText("");

                    ortalamaHesapla();
                    saveData(coinYukle);

                }

            }
        });
    }

    public void ortalamaHesapla() {
        double adet = 0;
        double fiyatOrt = 0;
        double gerceklesirsePara = 0;
        for (int i = 0; i < hesapArrayList.size(); i++) {
            adet = (hesapArrayList.get(i).getAdet() + adet);
            gerceklesirsePara = hesapArrayList.get(i).getAdet() * hesapArrayList.get(i).getFiyat() + gerceklesirsePara;
            fiyatOrt = gerceklesirsePara / adet;
        }

        binding.textViewAdet.setText(decimalFormat.format(adet));
        binding.textViewOrtalama.setText(decimalFormat.format(fiyatOrt));
        binding.textViewGerceklesirsePara.setText(decimalFormat.format(gerceklesirsePara));
        double hamPara = Double.parseDouble(decimalFormat.format(FragmentAlis.toplamHamPara));
        binding.textViewBaslangictakiPara.setText(String.valueOf(hamPara));

        if(gerceklesirsePara>0) {
            float artisOrani = (float) (((gerceklesirsePara - hamPara) / gerceklesirsePara) * 100);
            float netKar = (float) (gerceklesirsePara - hamPara);

            binding.textViewKarOrani.setText(String.format("%% %s (%s)", decimalFormatYuzde.format(artisOrani), decimalFormatYuzde.format(netKar)));
            if (artisOrani > 0.0) {
                binding.textViewKarOrani.setTextColor(Color.GREEN);
            } else {
                binding.textViewKarOrani.setTextColor(Color.RED);
            }
        }
        else {
            binding.textViewKarOrani.setText("0");
        }
    }


    public void atamalar() {

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("coinAdi");
            //The key argument here must match that used in the other activity
            coinYukle = value;
        }

        manager = new LinearLayoutManager(getContext());
        binding.recyclerview.setLayoutManager(manager);

        hesapArrayList = new ArrayList<>();
        coinerArray = new ArrayList<Coinler>();

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        Locale currentLocale = Locale.getDefault();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        //end

        decimalFormat = new DecimalFormat("#.#####", otherSymbols);
        decimalFormatYuzde = new DecimalFormat("#.##", otherSymbols);
    }

    public void veriEkle() {
        double adet = Double.parseDouble(binding.editTextNumberDecimalAdet.getText().toString());
        double fiyat = Double.parseDouble(binding.editTextNumberDecimalFiyat.getText().toString());
        if (adet != 0 && fiyat != 0) {
            hesapArrayList.add(new Hesap(adet, fiyat));
        } else {
            Toast.makeText(getContext(), "Kabul edilmeyen değer", Toast.LENGTH_SHORT).show();
        }
    }

    public void buildrecyclerview() {
        adapter = new CustomAdapter(hesapArrayList, getContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(manager);
        binding.recyclerview.setAdapter(adapter);

        binding.recyclerview.smoothScrollToPosition(adapter.getItemCount());
    }

    private void loadData(String coinYukle) {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(coinYukle, MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("hesaplarSatis", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<Hesap>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        hesapArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (hesapArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            hesapArrayList = new ArrayList<>();
        }

    }

    private void saveData(String coinYukle) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(coinYukle, MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(hesapArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("hesaplarSatis", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();
    }

    @Override
    public void OnItemClicked(int position) {
        adapter.notifyItemRemoved(position);
        adapter.hesapArrayList.remove(position);
        ortalamaHesapla();
        saveData(coinYukle);
    }

    @Override
    public void OnItemClickedCoinlerList(Coinler coinler) {

    }


}

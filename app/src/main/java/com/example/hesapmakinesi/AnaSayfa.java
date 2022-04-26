package com.example.hesapmakinesi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.hesapmakinesi.Object.Hesap;
import com.example.hesapmakinesi.Object.KayitliCoinler;
import com.example.hesapmakinesi.Object.ListeLimitKontrol;
import com.example.hesapmakinesi.databinding.ActivityAnaSayfaBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class AnaSayfa extends AppCompatActivity implements ClickListenerKayitliCoinler {

    private ActivityAnaSayfaBinding binding;
    RecyclerView.LayoutManager manager;
    ArrayList<KayitliCoinler> kayitliCoinlers;
    ArrayList<ListeLimitKontrol> stringsID;

    CustomKayitliCoinlerAdapter adapter;

    int tiklananPosition;
    //resume ilke defa çalışınca istenenleri yapmaması icin kullanıldı
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atamalar();
        buildrecyclerview();

        binding.yeniCoinEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                veriKontrol();
                adapter.notifyDataSetChanged();
                binding.listeRecyclerview.smoothScrollToPosition(adapter.getItemCount());
                saveData();
                binding.toolbar.notSayisi.setText(kayitliCoinlers.size()+"/20");
                gorunurlukDegistir();
            }
        });

    }

    public void coinEkle(String isim, int adet, String id, int idPosition) {
        kayitliCoinlers.add(new KayitliCoinler(isim, adet, id, idPosition));
    }

    public void veriKontrol() {
        String id = "";
        int i = 0;
        try {
            while (i < 21) {

                if (stringsID.get(i).isDurum()) {
                    id = stringsID.get(i).getId();
                    stringsID.get(i).setDurum(false);
                    break;
                }
                i++;
            }

            SharedPreferences preferences = getSharedPreferences(id, MODE_PRIVATE);

            String isim = preferences.getString("coinAdi", "Eklemek için tıkla");


            ArrayList<Hesap> hesapArrayList;
            Gson gson = new Gson();
            String json = preferences.getString("hesaplar", null);
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

            int adet = hesapArrayList.size();

            if (!id.equals("")) {
                coinEkle(isim, adet, id, i);
            } else {
                Toast.makeText(getApplicationContext(), "Liste Dolu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }


    public void atamalar() {
        binding = ActivityAnaSayfaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = new LinearLayoutManager(this);
        binding.listeRecyclerview.setLayoutManager(manager);

        kayitliCoinlers = new ArrayList<>();
        stringsID = new ArrayList<>();


        loadData();

        if(stringsID.size()==0){
            //20 adet not eklemek için sınır koyulacak
            for (int i = 1; i < 21; i++) {
                stringsID.add(new ListeLimitKontrol("Note" + i, true));
                //durum true ise kullanılmamış bir id, eğer false ise id kullanılıyor
            }
        }

        gorunurlukDegistir();
        binding.toolbar.notSayisi.setText(kayitliCoinlers.size()+"/20");

    }

    public void gorunurlukDegistir(){
        if(kayitliCoinlers.size()==0){
            binding.listeRecyclerview.setVisibility(View.GONE);
            binding.textViewBosUyarisi.setVisibility(View.VISIBLE);
        }else{
            binding.listeRecyclerview.setVisibility(View.VISIBLE);
            binding.textViewBosUyarisi.setVisibility(View.GONE);
        }
    }

    public void buildrecyclerview() {
        adapter = new CustomKayitliCoinlerAdapter(kayitliCoinlers, getApplicationContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.listeRecyclerview.setHasFixedSize(true);
        binding.listeRecyclerview.setLayoutManager(manager);
        binding.listeRecyclerview.setAdapter(adapter);

        binding.listeRecyclerview.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void OnItemClickedKayitliCoinler(KayitliCoinler kayitliCoinler, int position, int islemID) {
        if (islemID == 1) {
            stringsID.set(kayitliCoinler.getIdPosition(), new ListeLimitKontrol(kayitliCoinler.getId(), true));
            adapter.kayitliCoinlerArrayList.remove(position);
            adapter.notifyItemRemoved(position);

            //Verileri hafızan kalıcı olarak siliyor
            SharedPreferences sharedPreferences = getSharedPreferences(kayitliCoinler.getId(), MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();

            saveData();
            gorunurlukDegistir();
        } else if (islemID == 2) {
            tiklananPosition = position;
            String value = kayitliCoinler.getId();
            Intent i = new Intent(AnaSayfa.this, HesapSayfasi.class);
            i.putExtra("coinAdi", value);
            startActivity(i);
        }

        binding.toolbar.notSayisi.setText(kayitliCoinlers.size()+"/20");
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("Liste", MODE_PRIVATE);


        // creating a variable for gson.
        Gson gson = new Gson();
        Gson gson2 = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("listeler", null);
        String json2 = sharedPreferences.getString("IDler", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<KayitliCoinler>>() {
        }.getType();
        Type type2 = new TypeToken<ArrayList<ListeLimitKontrol>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        kayitliCoinlers = gson.fromJson(json, type);
        stringsID = gson2.fromJson(json2, type2);

        // checking below if the array list is empty or not
        if (kayitliCoinlers == null) {
            // if the array list is empty
            // creating a new array list.
            kayitliCoinlers = new ArrayList<>();
        }
        if (stringsID == null) {
            // if the array list is empty
            // creating a new array list.
            stringsID = new ArrayList<>();
        }

    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("Liste", MODE_PRIVATE);


        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();
        Gson gson2 = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(kayitliCoinlers);
        String json2 = gson2.toJson(stringsID);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("listeler", json);
        editor.putString("IDler", json2);



        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();
    }

    @Override
    protected void onResume() {
        if (i != 0) {
            SharedPreferences preferences = getSharedPreferences(kayitliCoinlers.get(tiklananPosition).getId(), MODE_PRIVATE);

            String isim = preferences.getString("coinAdi", "Eklemek için tıkla");


            ArrayList<Hesap> hesapArrayList;
            Gson gson = new Gson();
            String json = preferences.getString("hesaplar", null);
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

            int adet = hesapArrayList.size();
            kayitliCoinlers.get(tiklananPosition).setAdet(adet);
            kayitliCoinlers.get(tiklananPosition).setIsim(isim);
            adapter.notifyDataSetChanged();
            saveData();
        }
        i++;

        super.onResume();
    }

}
package com.example.hesapmakinesi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hesapmakinesi.Object.Coinler;
import com.example.hesapmakinesi.Object.Hesap;
import com.example.hesapmakinesi.databinding.CustomDialogBoxBinding;
import com.example.hesapmakinesi.databinding.CustomListBinding;
import com.example.hesapmakinesi.databinding.FragmentAlisSayfasiBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class FragmentAlis extends Fragment implements ClickListener {
    private FragmentAlisSayfasiBinding binding;
    RecyclerView.LayoutManager manager;
    ArrayList<Hesap> hesapArrayList;
    Dialog dialog;
    Dialog dialogCoin;
    DecimalFormat decimalFormat;
    DecimalFormat decimalFormatYuzde;
    CustomAdapter adapter;
    CustomAdapterCoinler adapterCoinler;
    String mevcutCoinAdi;
    String coinYukle;
    static double toplamHamPara;

    ArrayList<Coinler> coinlerArray;

    Handler handler;
    Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlisSayfasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        atamalar();
        loadData(coinYukle);
        buildrecyclerview();
        ortalamaHesapla();
        getData();

        if (!binding.textViewYeniAdet.getText().toString().equals("") && !binding.textViewYeniAdet.getText().toString().equals("Yeni adet için tıkla")) {
            String str = binding.textViewYeniAdet.getText().toString();
            String[] strings = str.split(" ");
            yeniOrtalamaHesapla(Double.parseDouble(strings[0].trim()));
        }

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
                    yuzdeHesapla();
                    saveData(coinYukle);

                }

            }
        });

        binding.textViewYeniAdet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAlert();
                saveData(coinYukle);
            }
        });

        binding.toolbar.coinAdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coinlerArray.size() > 0) {
                    showDialogAlertCoin();
                } else {
                    //eger liste yüklenmezse tekrar denenmesi icin fonksiyon cagiriliyor.
                    Toast.makeText(getContext(), "Tekrar deneyin!", Toast.LENGTH_SHORT).show();
                    getData();
                }
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //yenile butonu yerine belirtilen sürede bir sürekli güncelleme yapacak
                getDataKaydedilenCoin(mevcutCoinAdi);
                yuzdeHesapla();
                handler.postDelayed(this, 6000);

            }
        };
    }

    public void ortalamaHesapla() {
        double adet = 0;
        double fiyatOrt = 0;
        double toplamPara = 0;
        for (int i = 0; i < hesapArrayList.size(); i++) {
            adet = (hesapArrayList.get(i).getAdet() + adet);
            toplamPara = hesapArrayList.get(i).getAdet() * hesapArrayList.get(i).getFiyat() + toplamPara;
            fiyatOrt = toplamPara / adet;
        }

        binding.textViewAdet.setText(decimalFormat.format(adet));
        binding.textViewOrtalama.setText(decimalFormat.format(fiyatOrt));
        binding.textViewToplamPara.setText(decimalFormat.format(toplamPara));
        toplamHamPara = toplamPara;
    }

    public void yeniOrtalamaHesapla(Double yeniAdet) {
        double fark = yeniAdet - Double.parseDouble(binding.textViewAdet.getText().toString());
        if (yeniAdet == 0) {
            fark = 0;
        }
        binding.textViewYeniAdet.setText(String.format("%s (%s)", decimalFormat.format(yeniAdet), decimalFormat.format(fark)));
        if (yeniAdet > 0) {
            double yeniOrt = Double.parseDouble(binding.textViewToplamPara.getText().toString()) / yeniAdet;
            binding.textViewYeniOrtalama.setText(decimalFormat.format(yeniOrt));
            if (yeniOrt < Double.parseDouble(binding.textViewOrtalama.getText().toString())) {
                binding.textViewYeniOrtalama.setTextColor(Color.GREEN);
            } else if (yeniOrt > Double.parseDouble(binding.textViewOrtalama.getText().toString())) {
                binding.textViewYeniOrtalama.setTextColor(Color.RED);
            }
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
        coinlerArray = new ArrayList<Coinler>();

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        Locale currentLocale = Locale.getDefault();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        //end

        decimalFormat = new DecimalFormat("#.#####", otherSymbols);
        decimalFormatYuzde = new DecimalFormat("#.##", otherSymbols);

        dialog = new Dialog(getContext());
        dialogCoin = new Dialog(getContext());

        if (!isConnected()) {
            Toast.makeText(getContext(), "İnternet bağlantısı bulunamadı!", Toast.LENGTH_SHORT).show();
        }

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

    public void showDialogAlert() {
        CustomDialogBoxBinding bindingDialogBox = CustomDialogBoxBinding.inflate(getLayoutInflater());
        dialog.setContentView(bindingDialogBox.getRoot());

        bindingDialogBox.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bindingDialogBox.textViewYeniAdetDialogBox.getText().toString().equals("")) {
                    double yeniadetsayisi = Double.parseDouble(bindingDialogBox.textViewYeniAdetDialogBox.getText().toString());
                    if (yeniadetsayisi != 0) {
                        double yeniAdet = Double.parseDouble(bindingDialogBox.textViewYeniAdetDialogBox.getText().toString());
                        yeniOrtalamaHesapla(yeniAdet);
                        yuzdeHesapla();
                    } else {
                        yeniOrtalamaSifirlayici();
                    }
                    saveData(coinYukle);
                }
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void yeniOrtalamaSifirlayici() {
        yeniOrtalamaHesapla(0.0);
        binding.textViewKarYuzdeKarDahil.setText("% 0");
        binding.textViewYeniOrtalama.setText("0");
        binding.textViewKarYuzdeKarDahil.setTextColor(Color.BLACK);
        binding.textViewYeniOrtalama.setTextColor(Color.BLACK);
        binding.textViewKarliBakiye.setText("0");
    }

    public void showDialogAlertCoin() {
        CustomListBinding listBinding = CustomListBinding.inflate(getLayoutInflater());
        dialogCoin.setContentView(listBinding.getRoot());

        Collections.sort(coinlerArray, Coinler.presidentComparatorAZ);
        adapterCoinler = new CustomAdapterCoinler(coinlerArray, getContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listBinding.recyclerViewCoinler.setHasFixedSize(true);
        listBinding.recyclerViewCoinler.setLayoutManager(manager);
        listBinding.recyclerViewCoinler.setAdapter(adapterCoinler);

        listBinding.editTextTextPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapterCoinler.getFilter().filter(editable);
            }
        });

        dialogCoin.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //Listede arama yapılıp seçilmediği zaman liste filteli bir şekilde kalması engellendi
                adapterCoinler.getFilter().filter("");
            }
        });
        dialogCoin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCoin.show();
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
        String json = sharedPreferences.getString("hesaplar", null);

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
        String yeniOrt = sharedPreferences.getString("yeniAdet", "Yeni adet için tıkla");
        binding.textViewYeniAdet.setText(yeniOrt);

        String coinAdi = sharedPreferences.getString("coinAdi", "BTCUSDT");
        getDataKaydedilenCoin(coinAdi);
        mevcutCoinAdi = coinAdi;

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
        editor.putString("hesaplar", json);

        editor.putString("yeniAdet", binding.textViewYeniAdet.getText().toString());
        if (!binding.toolbar.coinAdi.getText().toString().equals("")) {
            editor.putString("coinAdi", binding.toolbar.coinAdi.getText().toString());
        }

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();
    }

    @Override
    public void OnItemClicked(int position) {
        adapter.notifyItemRemoved(position);
        adapter.hesapArrayList.remove(position);
        ortalamaHesapla();
        yuzdeHesapla();
        saveData(coinYukle);
        if (adapter.getItemCount() == 0) {
            yeniOrtalamaSifirlayici();
        }
    }

    @Override
    public void OnItemClickedCoinlerList(Coinler coinler) {
        binding.toolbar.coinAdi.setText(coinler.getIsim());
        binding.toolbar.coinFiyati.setText(String.format("%s $", decimalFormat.format(coinler.getFiyat())));
        mevcutCoinAdi = coinler.getIsim();

        saveData(coinYukle);

        adapterCoinler.getFilter().filter("");
        yuzdeHesapla();
        dialogCoin.dismiss();
    }


    public void getDataKaydedilenCoin(String coinAdi) {
        RequestQueue requestQueue1;
        requestQueue1 = Volley.newRequestQueue(getContext());
        String url = "https://api1.binance.com/api/v3/ticker/price?symbol=" + coinAdi;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject json = new JSONObject(response.toString());
                    binding.toolbar.coinAdi.setText(json.getString("symbol"));
                    binding.toolbar.coinFiyati.setText(decimalFormat.format(Double.parseDouble(json.getString("price"))) + " $");
                    yuzdeHesapla();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue1.add(jsonObjectRequest);
    }

    public void getData() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://api1.binance.com/api/v3/ticker/price", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    coinlerArray.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);

                        if (jsonObject.getString("symbol").contains("USDT")) {
                            Coinler coin = new Coinler(jsonObject.getString("symbol"), Double.parseDouble(jsonObject.getString("price")));
                            coinlerArray.add(coin);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void yuzdeHesapla() {
        String anlikDegerstr = binding.toolbar.coinFiyati.getText().toString();
        if (!anlikDegerstr.equals("")) {
            String[] str1 = anlikDegerstr.split(" ");
            double anlikDeger = Double.parseDouble(str1[0]);
            double karliOrtalama = 0;
            double karsizOrtalama = 0;
            double netHamKar = 0;
            double netKarDahilKar = 0;
            float karliArtisOrani = 0;
            float karsizArtisOrani = 0;
            double toplamPara = Double.parseDouble(binding.textViewToplamPara.getText().toString());
            if (binding.textViewYeniOrtalama.getText().toString().equals("") || Double.parseDouble(binding.textViewYeniOrtalama.getText().toString()) == 0) {

            } else {
                karliOrtalama = Double.parseDouble(binding.textViewYeniOrtalama.getText().toString());
                karliArtisOrani = (float) (((anlikDeger - karliOrtalama) / karliOrtalama) * 100);
                netKarDahilKar = (toplamPara * karliArtisOrani) / 100;
            }
            if (Double.parseDouble(binding.textViewOrtalama.getText().toString()) == 0) {

            } else {
                karsizOrtalama = Double.parseDouble(binding.textViewOrtalama.getText().toString());
                karsizArtisOrani = (float) (((anlikDeger - karsizOrtalama) / karsizOrtalama) * 100);
                netHamKar = (toplamPara * karsizArtisOrani) / 100;
            }

            binding.textViewKarYuzde.setText(String.format("%% %s (%s)", decimalFormatYuzde.format(karsizArtisOrani), decimalFormatYuzde.format(netHamKar)));
            binding.textViewKarYuzdeKarDahil.setText(String.format("%% %s (%s)", decimalFormatYuzde.format(karliArtisOrani), decimalFormatYuzde.format(netKarDahilKar)));

            if (karsizArtisOrani > 0.0) {
                binding.textViewKarYuzde.setTextColor(Color.GREEN);
            } else {
                binding.textViewKarYuzde.setTextColor(Color.RED);
            }
            if (karliArtisOrani > 0.0) {
                binding.textViewKarYuzdeKarDahil.setTextColor(Color.GREEN);
            } else {
                binding.textViewKarYuzdeKarDahil.setTextColor(Color.RED);
            }
            anlikBakiyeHesaplama(str1);
        } else {
            Toast.makeText(getContext(), "Kâr oranı hesaplanması için internet bağlantısı gerekir", Toast.LENGTH_SHORT).show();
        }
    }

    public void anlikBakiyeHesaplama(String[] guncelFiyatstr) {
        double karsizAdet = Double.parseDouble(binding.textViewAdet.getText().toString());
        double karliAdet = 0;

        if (!binding.textViewYeniAdet.getText().toString().equals("") && !binding.textViewYeniAdet.getText().toString().equals("Yeni adet için tıkla")) {
            String str = binding.textViewYeniAdet.getText().toString();
            String[] strings = str.split(" ");
            karliAdet = Double.parseDouble(strings[0].trim());
        }

        double guncelFiyat = Double.parseDouble(guncelFiyatstr[0]);

        binding.textViewKarliBakiye.setText(decimalFormat.format(karliAdet * guncelFiyat));
        binding.textViewKarsizBakiye.setText(decimalFormat.format(karsizAdet * guncelFiyat));
    }

    boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.isConnected())
                return true;
            else
                return false;
        } else
            return false;

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 6000);
    }
}

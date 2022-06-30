package com.example.hesapmakinesi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hesapmakinesi.Object.Coinler;
import com.example.hesapmakinesi.Object.Hesap;
import com.example.hesapmakinesi.databinding.ActivityHesapSayfasiBinding;
import com.example.hesapmakinesi.databinding.CustomDialogBoxBinding;
import com.example.hesapmakinesi.databinding.CustomListBinding;
import com.google.android.material.tabs.TabLayout;
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

public class HesapSayfasi extends AppCompatActivity{
    private ActivityHesapSayfasiBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        atamalar();
        viewPagerTabLayout();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //ekran yan kaydırılarak değiştirildiğinde tabLayout itemleride kendini günceller.
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
    }
    private void atamalar() {
        binding = ActivityHesapSayfasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void viewPagerTabLayout(){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(viewPagerAdapter);
    }

}
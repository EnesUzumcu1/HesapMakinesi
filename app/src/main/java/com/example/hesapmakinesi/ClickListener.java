package com.example.hesapmakinesi;

import com.example.hesapmakinesi.Object.Coinler;
import com.example.hesapmakinesi.Object.Hesap;

public interface ClickListener {
    void OnItemClicked(Hesap hesap, int position);
    void OnItemClickedCoinlerList(Coinler coinler, int position);
}

package com.example.hesapmakinesi.Object;

import java.util.Comparator;

public class Coinler {
    String isim;
    double fiyat;

    public Coinler(String isim, double fiyat) {
        this.isim = isim;
        this.fiyat = fiyat;
    }

    public static Comparator<Coinler> presidentComparatorAZ = new Comparator<Coinler>() {
        @Override
        public int compare(Coinler p1, Coinler p2) {
            return p1.getIsim().compareTo(p2.getIsim());
        }
    };

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public double getFiyat() {
        return fiyat;
    }

    public void setFiyat(double fiyat) {
        this.fiyat = fiyat;
    }




}

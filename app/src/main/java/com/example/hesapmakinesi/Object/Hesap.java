package com.example.hesapmakinesi.Object;

public class Hesap {

    double adet,fiyat;

    public Hesap(double adet, double fiyat) {
        this.adet = adet;
        this.fiyat = fiyat;
    }

    public double getAdet() {
        return adet;
    }

    public void setAdet(double adet) {
        this.adet = adet;
    }

    public double getFiyat() {
        return fiyat;
    }

    public void setFiyat(double fiyat) {
        this.fiyat = fiyat;
    }


}

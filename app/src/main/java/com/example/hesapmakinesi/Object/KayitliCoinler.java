package com.example.hesapmakinesi.Object;


public class KayitliCoinler {
    String isim;
    String id;
    int adet;
    int idPosition;

    public KayitliCoinler(String isim, int adet, String id, int idPosition) {
        this.isim = isim;
        this.adet = adet;
        this.id = id;
        this.idPosition = idPosition;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public int getAdet() {
        return adet;
    }

    public void setAdet(int adet) {
        this.adet = adet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }
}
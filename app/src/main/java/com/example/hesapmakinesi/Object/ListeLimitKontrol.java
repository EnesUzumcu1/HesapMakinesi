package com.example.hesapmakinesi.Object;

public class ListeLimitKontrol {

    String id;
    boolean durum;

    public ListeLimitKontrol(String id, boolean durum) {
        this.id = id;
        this.durum = durum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDurum() {
        return durum;
    }

    public void setDurum(boolean durum) {
        this.durum = durum;
    }



}

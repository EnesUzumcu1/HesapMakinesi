package com.example.hesapmakinesi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentAlis();
        } else if (position == 1) {
            return new FragmentSatis();
        } else {
            return new FragmentAlis();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}

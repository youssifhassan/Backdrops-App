package com.example.android.backdropsapp.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.backdropsapp.Fragments.ActionFragment;
import com.example.android.backdropsapp.Fragments.DramaFragment;
import com.example.android.backdropsapp.Fragments.RomanticFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ActionFragment();
            case 1:
                return new DramaFragment();
            case 2:
                return new RomanticFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Action";
            case 1:
                return "Drama";
            case 2:
                return "Romantic";
        }

        return null;
    }
}

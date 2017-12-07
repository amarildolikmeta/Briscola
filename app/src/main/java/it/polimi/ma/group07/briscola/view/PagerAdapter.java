package it.polimi.ma.group07.briscola.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.StatisticsActivity;

/**
 * Created by amari on 03-Dec-17.
 */

public  class PagerAdapter  extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;
    public PagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments=new ArrayList<>();
        StatisticsFragment localFragment=new StatisticsFragment();
        localFragment.setGameType("Local");
        fragments.add(localFragment);
        StatisticsFragment onlineFragment=new StatisticsFragment();
        onlineFragment.setGameType("Online");
        fragments.add(onlineFragment);
    }
    @Override
    public Fragment getItem(int position) {
        //replace fragment
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
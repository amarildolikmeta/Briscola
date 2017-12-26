package it.polimi.ma.group07.briscola;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import it.polimi.ma.group07.briscola.view.PagerAdapter;

/**
 * Displays the statistics of the game in two tabs
 * Local Games
 * Online Games
 */
public class StatisticsActivity extends AppCompatActivity {
    /**
     * To manage the different tabs
     */
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        /**
         * Set the tabs labels and set the listener to switch between them
         */
        final ViewPager viewPager=(ViewPager) findViewById(R.id.pager);
        tabLayout=(TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.local)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.online)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pagerAdapter= new PagerAdapter(getSupportFragmentManager());
        /**
         * Set the adapter for the viewPager
         */
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}

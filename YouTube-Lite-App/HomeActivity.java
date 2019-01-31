package jillpena.c323finalproj.com.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class HomeActivity extends AppCompatActivity implements Tab3FriendsFragment.FriendsInterface {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    FloatingActionButton fab;
    TabLayout tabLayout;
    FriendProfileFragment friendProfileFragment;
    Tab3FriendsFragment tab3FriendsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter with sections
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new Tab1TrendingFragment(), "Trending", 0);
        mSectionsPagerAdapter.addFragment(new Tab2VideoFeedFragment(), "Video Feed", 1);
        tab3FriendsFragment = new Tab3FriendsFragment();
        mSectionsPagerAdapter.addFragment(tab3FriendsFragment, "Friends", 2);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Intent myIntent = getIntent();
        String username = myIntent.getStringExtra("USER_NAME");
        String phoneNumber = myIntent.getStringExtra("PHONE_NUMBER");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickFab(ArrayList<Person> plist) {
        Toast.makeText(this, "Clicked ME!", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(HomeActivity.this, FriendsActivity.class);
        ArrayList<String> peopleNames= new ArrayList<>();
        ArrayList<Integer> peoplePhotos = new ArrayList<>();
        for (int i = 0; i < plist.size(); i++){
            peopleNames.add(i, plist.get(i).getName());
            peoplePhotos.add(i, plist.get(i).getImage());
        }
        myIntent.putExtra("NAMES_LIST", peopleNames);
        myIntent.putExtra("PHOTO_LIST", peoplePhotos);
        startActivity(myIntent);
    }

    @Override
    public void onClickPersonProfile(Person p) {
        String name = p.getName();
        int photo = p.getImage();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("image", photo);

        FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
        friendProfileFragment.setArguments(bundle);
        Toast.makeText(this, "CLICKED ME!", Toast.LENGTH_SHORT).show();
        //mSectionsPagerAdapter.removeFragment(tab3FriendsFragment, 2);
        mSectionsPagerAdapter.addFragment(friendProfileFragment, "Friend Profile", 3);
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(3);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title, int position) {
            mFragmentList.add(position, fragment);
            mFragmentTitleList.add(title);
        }

        public void removeFragment(Fragment fragment, int position) {
            mFragmentList.remove(position);
            mFragmentTitleList.remove(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }


}

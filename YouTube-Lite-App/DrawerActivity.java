package jillpena.c323finalproj.com.finalproject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Tab3FriendsFragment.FriendsInterface {

    DrawerLayout drawer;
    private SectionsPageAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    FloatingActionButton fab;
    TabLayout tabLayout;
    FriendProfileFragment friendProfileFragment;
    Tab3FriendsFragment tab3FriendsFragment;
    String username;
    String phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
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
        username = myIntent.getStringExtra("USER_NAME");
        phonenumber = myIntent.getStringExtra("PHONE_NUMBER");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.textViewNameNav);
        navUsername.setText(username);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
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

        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_profile) {
             Intent myIntent = new Intent(DrawerActivity.this, UserProfileActivity.class);
             myIntent.putExtra("user", username);
             myIntent.putExtra("phonenumber", phonenumber);
             startActivity(myIntent);

        } else if (id == R.id.nav_settings) {
             Toast.makeText(this, "you clicked me", Toast.LENGTH_SHORT).show();

         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClickFab(ArrayList<Person> plist) {
        Toast.makeText(this, "Clicked ME!", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(DrawerActivity.this, FriendsActivity.class);
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

    int count=0;
    @Override
    public void onClickPersonProfile(Person p) {
        String name = p.getName();
        int photo = p.getImage();
        Log.i("FINAL_EXAM", "On click Person: " + name + " " + photo);
        try {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putInt("image", photo);

            FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
            friendProfileFragment.setArguments(bundle);
            //mSectionsPagerAdapter.removeFragment(tab3FriendsFragment, 2);
            if (count == 0) {
                mSectionsPagerAdapter.addFragment(friendProfileFragment, "Friend Profile", 3);
                mSectionsPagerAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(3);
                count++;
            } else{
                android.support.v4.app.Fragment frag = mSectionsPagerAdapter.getItem(3);
                mSectionsPagerAdapter.replaceFragment(frag, friendProfileFragment, 3, "New Friend Profile " + count);
                mSectionsPagerAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(3);
                friendProfileFragment.setName(p.getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

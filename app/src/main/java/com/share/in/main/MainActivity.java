package com.share.in.main;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.share.in.R;
import com.share.in.main.transfer.TransferActivity;
import com.share.in.main.view.UIFragment;

public class MainActivity extends AppCompatActivity implements ContainerFragment.TabLayoutSetupCallback,
        PageFragment.OnListItemClickListener {

  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mActionBarDrawerToggle;
  ViewPagerAdapter viewPagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
    mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
            toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

    //calling sync state is necessary or else your hamburger icon wont show up
    mActionBarDrawerToggle.syncState();

    if (savedInstanceState == null) {
      // update the main content by replacing fragments
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
      FragmentTransaction transaction = fragmentManager.beginTransaction();

      transaction.replace(R.id.container, new ContainerFragment());
      transaction.commit();
    }
  }

  @Override
  public void onBackPressed() {
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  public void sendFiles(View view)
  {
    startActivity( new Intent( getApplicationContext(), TransferActivity.class ) );
    // Do something in response to button click
  }
  @Override
  public void setupTabLayout(ViewPager viewPager) {
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    tabLayout.setupWithViewPager(viewPager);
    viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPagerAdapter.addFrag(new HistoryFragment(), "History");
    viewPagerAdapter.addFrag(new AppsActivity(), "App");
    viewPagerAdapter.addFrag(new ImagesActivity(), "Photo");
    viewPagerAdapter.addFrag(new AudioActivity(), "Music");
    viewPagerAdapter.addFrag(new VideosActivity(), "Video");
    viewPagerAdapter.addFrag(new FileActivity(), "File");
    viewPagerAdapter.addFrag(new VideosActivity(), "News");
    viewPagerAdapter.addFrag(new VideosActivity(), "Status Saver");
    viewPagerAdapter.addFrag(new VideosActivity(), "Popular Video");
    viewPagerAdapter.addFrag(new VideosActivity(), "Video To Mp3");
    viewPagerAdapter.addFrag(new VideosActivity(), "Video Cutter");

    viewPager.setAdapter(viewPagerAdapter);
    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
  }

  @Override
  public void onListItemClick(String title) {
    Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
  }
}
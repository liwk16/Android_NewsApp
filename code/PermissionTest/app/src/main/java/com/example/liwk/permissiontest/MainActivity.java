package com.example.liwk.permissiontest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liwk.permissiontest.activity.AskForPermission;
import com.example.liwk.permissiontest.activity.SearchActivity;
import com.example.liwk.permissiontest.activity.StorageActivity;
import com.example.liwk.permissiontest.adapter.NewsAdapter;
import com.example.liwk.permissiontest.database.MyDataBase;
import com.example.liwk.permissiontest.listener.MyClickListener;
import com.example.liwk.permissiontest.listener.MyRefreshListener;
import com.example.liwk.permissiontest.news.NewsBean;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        private ArrayList<View> viewList;
        private ArrayList<View> viewAliveList;
        private ArrayList<NewsAdapter> newsAdapterArrayList;

        private ArrayList<Integer> typeAliveList;

        private ViewPager mViewPager;
        private MyAdapter pageAdapter;
        private TabPageIndicator indicator;

        private static final String[] CONTENT = {"??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "?????????","??????","??????","??????","??????"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        viewList = new ArrayList<>();
        viewAliveList = new ArrayList<>();
        typeAliveList = new ArrayList<>();
        newsAdapterArrayList = new ArrayList<>();

        for(int i=0;i<CONTENT.length;++i)
        {
            newsAdapterArrayList.add(createOnePage(i));
        }

        for(int i=0;i<8;++i)
        {
            viewAliveList.add(viewList.get(i));
            typeAliveList.add(i);
        }

        mViewPager = (ViewPager)findViewById(R.id.view_pager);

        //pageAdapter = new MyAdapter(new ArrayList<View>());
        pageAdapter = new MyAdapter(viewAliveList);
        mViewPager.setAdapter(pageAdapter);

        indicator = (TabPageIndicator)findViewById(R.id.page_indicator);
        indicator.setViewPager(mViewPager);


        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //????????????????????????
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
                else
                {
                    Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                    initData();
                }
            }
        }
    }


    public void initData()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<CONTENT.length;++i)
                {
                    newsAdapterArrayList.get(i).addItems(getDataFromDataBase(i));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<CONTENT.length;++i)
                        {
                            newsAdapterArrayList.get(i).notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();
    }

    public NewsAdapter createOnePage(int typeID)
    {
        NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, typeID);
        MyClickListener clickListener = new MyClickListener(MainActivity.this, newsAdapter);
        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(MainActivity.this);
        ListView listView = new ListView(MainActivity.this);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(clickListener);
        swipeRefreshLayout.addView(listView);
        swipeRefreshLayout.setOnRefreshListener(new MyRefreshListener(MainActivity.this,
                swipeRefreshLayout, newsAdapter, typeID));

        viewList.add(swipeRefreshLayout);
        return newsAdapter;
    }

    public void initDrawer()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public ArrayList<NewsBean> getDataFromDataBase(int typeID)
    {
        ArrayList<NewsBean> list = new ArrayList<>();
        MyDataBase db = new MyDataBase(MainActivity.this);
        SQLiteDatabase sb = db.getReadableDatabase();
        String sql = "select * from news where type = "+ typeID;
        Cursor cursor = sb.rawQuery(sql, null);
        while(cursor.moveToNext())
        {
            NewsBean bean = new NewsBean();
            bean.title = cursor.getString(2);
            bean.des = cursor.getString(3);
            bean.icon_url = cursor.getString(4);
            bean.news_url = cursor.getString(5);
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;
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
        menu.add(1,1,1,"????????????");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == 1) {
            arrangeList();
        }

        return super.onOptionsItemSelected(item);
    }

    public void arrangeList()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("??????");

        final boolean[] bool = new boolean[CONTENT.length];
        for(int i=0;i<bool.length;++i)
        {
            bool[i] = false;
        }
        for(int i=0;i<typeAliveList.size();++i)
        {
            bool[typeAliveList.get(i)] = true;
        }

        builder.setMultiChoiceItems(CONTENT, bool, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                bool[which] = isChecked;
            }
        });


        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT);
                pageAdapter.vList.clear();
                typeAliveList.clear();
                for(int i=0;i<bool.length;++i)
                {
                    if(bool[i])
                    {
                        pageAdapter.vList.add(viewList.get(i));
                        typeAliveList.add(i);
                    }
                }
                pageAdapter.notifyDataSetChanged();
                indicator.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, StorageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            //Intent intent = new Intent(MainActivity.this, AskForPermission.class);
            //startActivity(intent);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class MyAdapter extends PagerAdapter
    {
        ArrayList<View> vList;

        public MyAdapter(ArrayList<View> viewsArray)
        {
            this.vList = viewsArray;
        }

        @Override
        public int getCount()
        {
            //return CONTENT.length;
            return vList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        public CharSequence getPageTitle(int position)
        {
            return CONTENT[typeAliveList.get(position)];
            //return CONTENT[position];

            //return "??????";
        }

        @Override
        public int getItemPosition(Object obj)
        {
            return POSITION_NONE;
        }



        @Override
        public void destroyItem(ViewGroup container, int position, Object obj)
        {
            container.removeView((View)obj);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            //????????????page?????????text??????
            // ???page??????????????????????????????Fragmenlayout??????????????? ?????????????????????????????????????????????????????????textview????????????view

            //??????????????????listview???????????????

            container.addView(vList.get(position));
            return vList.get(position);


            //????????????
            //TextView textView = new TextView(MainActivity.this);
            //textView.setText("??????");//?????????????????????page?????????????????????
            //textView.setTextSize(25);
            //textView.setGravity(Gravity.CENTER);
            //textView.setTextColor(Color.GREEN);
            //container.addView(textView);
            //return textView;
        }
    }
}

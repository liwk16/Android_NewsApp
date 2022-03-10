package com.example.liwk.permissiontest.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.example.liwk.permissiontest.R;
import com.example.liwk.permissiontest.adapter.NewsAdapter;
import com.example.liwk.permissiontest.database.MyDataBase;
import com.example.liwk.permissiontest.listener.MyClickListener;
import com.example.liwk.permissiontest.listener.MyStorageListener;
import com.example.liwk.permissiontest.news.NewsBean;

import java.util.ArrayList;

public class StorageActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.storage_toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        listView = (ListView)findViewById(R.id.listView);

        newsAdapter = new NewsAdapter(getApplicationContext(), 20);
        MyClickListener clickListener = new MyClickListener(getApplicationContext(),newsAdapter);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(clickListener);
        swipeRefreshLayout.setOnRefreshListener(new MyStorageListener(getApplicationContext(),
                swipeRefreshLayout, newsAdapter, 20));

        initData();
    }

    public void initData()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newsAdapter.replaceItems(getDataFromDataBase(20));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public ArrayList<NewsBean> getDataFromDataBase(int typeID)
    {
        ArrayList<NewsBean> list = new ArrayList<>();
        MyDataBase db = new MyDataBase(getApplicationContext());
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
}

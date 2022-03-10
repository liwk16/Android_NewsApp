package com.example.liwk.permissiontest.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.liwk.permissiontest.R;
import com.example.liwk.permissiontest.adapter.NewsAdapter;
import com.example.liwk.permissiontest.database.MyDataBase;
import com.example.liwk.permissiontest.listener.MyClickListener;
import com.example.liwk.permissiontest.news.NewsBean;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private ListView listView;
    private NewsAdapter newsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.btn);
        listView = (ListView)findViewById(R.id.search_lv);

        initListView();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchDst = editText.getText().toString();
                if(searchDst.length()<=0)
                {
                    Toast.makeText(getApplicationContext(),"请输入关键词",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<NewsBean> newsList = new ArrayList<>();

                        //db op
                        MyDataBase db = new MyDataBase(getApplicationContext());
                        SQLiteDatabase sb = db.getReadableDatabase();
                        String sql = "select * from news";
                        Cursor cursor = sb.rawQuery(sql, null);
                        while(cursor.moveToNext())
                        {
                            String title = cursor.getString(2);
                            if(title.contains(searchDst))
                            {
                                NewsBean bean = new NewsBean();
                                bean.title = title;
                                bean.des = cursor.getString(3);
                                bean.icon_url = cursor.getString(4);
                                bean.news_url = cursor.getString(5);
                                newsList.add(bean);
                            }
                        }
                        cursor.close();
                        db.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newsAdapter.replaceItems(newsList);
                                newsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        });


    }

    public void initListView()
    {
        //30 means search
        newsAdapter = new NewsAdapter(getApplicationContext(), 30);
        MyClickListener clickListener = new MyClickListener(getApplicationContext(),newsAdapter);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(clickListener);
    }



}

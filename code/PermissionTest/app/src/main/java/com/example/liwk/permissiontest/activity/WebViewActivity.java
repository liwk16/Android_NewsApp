package com.example.liwk.permissiontest.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.liwk.permissiontest.R;
import com.example.liwk.permissiontest.database.MyDataBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private Intent intent;
    private boolean setFavorite;
    private File picFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView)findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);

        intent = getIntent();
        String url = intent.getStringExtra("url");


        webView.loadUrl(url);
        webView.setWebViewClient(new MyWebViewClient());

        setFavorite = checkNewsState();
        downloadPic(intent.getStringExtra("icon_url"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        String temp = "收藏";
        if(setFavorite)temp = "取消收藏";

        menu.add(1,1,1,temp);
        menu.add(1,2,1,"分享");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        menu.clear();

        String temp = "收藏";
        if(setFavorite)temp = "取消收藏";
        menu.add(1,1,1,temp);
        menu.add(1,2,1,"分享");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case 1:
                if(setFavorite)
                {
                    deleteNews();
                    setFavorite = !setFavorite;
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    storeNews();
                    setFavorite = !setFavorite;
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                share();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void share()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        try{

            Uri picUri = FileProvider.getUriForFile(this,
                    ProviderUtil.getFileProviderName(this),picFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, picUri);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        String text = intent.getStringExtra("title")+"\n"+intent.getStringExtra("url");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra("sms_body", text);
        shareIntent.putExtra("Kdescription", text);
        startActivity(Intent.createChooser(shareIntent,"分享"));
    }

    public void downloadPic(final String image_url)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                picFile = download(image_url);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String info = "分享图片加载完成";
                        if(picFile == null)info = "分享图片加载失败";
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public File download(String image_url)
    {
        try
        {
            URL url = new URL(image_url);
            InputStream inputStream = url.openStream();
            File file = new File(Environment.getExternalStorageDirectory()+"/haha");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int hasRead = 0;
            while((hasRead = inputStream.read())!=-1)
            {
                fileOutputStream.write(hasRead);
            }
            fileOutputStream.close();
            inputStream.close();
            return file;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkNewsState()
    {
        //检查是否已经设为喜欢，以此来确定Menu中显示的文本
        String title = intent.getStringExtra("title");
        MyDataBase db = new MyDataBase(getApplicationContext());
        SQLiteDatabase sb = db.getReadableDatabase();
        //String sql = "select * from news where type = 20 and news_url = '"+news_url+"'";
        String sql = "select * from news where type = 20 and title = '"+title+"'";
        Cursor cursor = sb.rawQuery(sql, null);
        int count = 0;
        while(cursor.moveToNext())
        {
            ++count;
            Log.d("webView","findone");
        }
        Log.d("webView",""+count);
        cursor.close();
        db.close();

        return count!=0;
    }

    public void storeNews()
    {
        MyDataBase db = new MyDataBase(getApplicationContext());
        SQLiteDatabase sb = db.getWritableDatabase();
        String sql = "insert into news values(null,?,?,?,?,?)";

        String news_url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String des = intent.getStringExtra("des");
        String icon_url = intent.getStringExtra("icon_url");

        sb.execSQL(sql, new Object[]{20,
                            title, des,
                            icon_url, news_url});

        db.close();
    }

    public void deleteNews()
    {
        String news_url = intent.getStringExtra("url");

        MyDataBase db = new MyDataBase(getApplicationContext());
        SQLiteDatabase sb = db.getWritableDatabase();
        String sql = "delete from news where type = 20 and news_url = '"+news_url+"'";
        sb.execSQL(sql);

        db.close();
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}

class ProviderUtil {

    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
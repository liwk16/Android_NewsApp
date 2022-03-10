package rssparse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;

import com.sun.syndication.feed.synd.SyndCategory;  
import com.sun.syndication.feed.synd.SyndContent;  
import com.sun.syndication.feed.synd.SyndEnclosure;  
import com.sun.syndication.feed.synd.SyndEntry;  
import com.sun.syndication.feed.synd.SyndFeed;  
import com.sun.syndication.io.SyndFeedInput;  
import com.sun.syndication.io.XmlReader;  
import com.example.liwk.permissiontest.news.*;


public class RssParser {
	
	public static String[] rssList = {"http://news.qq.com/newsgn/rss_newsgn.xml",
									"http://news.qq.com/newsgj/rss_newswj.xml",
									"http://sports.qq.com/basket/rss_basket.xml",
									"http://ent.qq.com/movie/rss_movie.xml",
									"http://sports.qq.com/rss_newssports.xml"};							
	
	
	public static final String[] CONTENT = {"推荐", "热点", "北京", "视频", "图片", "问答", "娱乐", "科技", "懂车帝","财经","军事","篮球","足球"};

	
	public ServerSocket server = null;
	OutputStream outputStream = null;
	public ArrayList<ArrayList<NewsBean>> allList;
	public int[] cursor;
	public int[] choiceCounter;
	
	public RssParser() throws Exception
	{
		server = new ServerSocket(2000);
		allList = new ArrayList<>();
		cursor = new int[CONTENT.length];
		choiceCounter = new int[CONTENT.length];
		for(int i=0;i<CONTENT.length;++i)
		{
			ArrayList<NewsBean> list = new ArrayList<>();
			allList.add(list);
			cursor[i] = 0;
			choiceCounter[i] = 0;
		}		
	}
	
	public static void main(String[] args) throws Exception
	{
		// run part
		
		RssParser rssparser = new RssParser();
		
		rssparser.initData();
		System.out.println("Init Done!");
		rssparser.connect();
		
		// run part
		
		//parse(1);
		
		//test part
		//parse(3);
		
		//ArrayList<NewsBean> temp = parse(0);
		//for(NewsBean item : temp)
		//{
		//	System.out.println(item.news_url);
		//	System.out.println(item.icon_url);
		//}
	}
	
	public void connect() throws Exception
	{
		int count = 0;
		Socket socket = null;
		while(true)
		{
			System.out.println(count++);
			try {
				socket = server.accept();
				
				new CntThread(socket, this).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void initData()
	{
		for(int i=0;i<CONTENT.length;++i)
		{
			allList.get(i).addAll(parse(i));
			System.out.println("complete : "+ i);
		}
	}
	
	public ArrayList<NewsBean> fetch(int typeID)
	{
		choiceCounter[typeID]++;
		int oriLoc = cursor[typeID];
		ArrayList<NewsBean> typeList = allList.get(typeID);
		
		cursor[typeID] = Math.min(cursor[typeID]+10, typeList.size());
		
		ArrayList<NewsBean> tempList = new ArrayList<>();
		for(int i=oriLoc;i<cursor[typeID];++i)
		{
			tempList.add(typeList.get(i));
		}
		System.out.println("fetch "+ tempList.size() + " items");
		return tempList;
	}
	
	public ArrayList<NewsBean> preferGet()
	{
		System.out.println("Enter perferGet");
		ArrayList<NewsBean> temp = new ArrayList<NewsBean>();
		int count = 20;
		Random rand = new Random();
		
		int[] choiceInc = new int[CONTENT.length];
		int sum = 0;
		for(int i=0;i<choiceInc.length;++i)
		{
			choiceInc[i] = choiceCounter[i]+1;
			System.out.print(choiceCounter[i]);
			sum += choiceCounter[i];
		}
		
		System.out.println('\n'+sum);
		choiceInc[0] = 2;
		System.out.println("Enter while");
		while(count>0&&sum>0)
		{
			int type = rand.nextInt(CONTENT.length);
			//System.out.println(type);
			if(choiceInc[type]>0)
			{
				NewsBean bean = tryToGetOne(type);
				if(bean!=null) {
					temp.add(bean);
					--count;
					--choiceInc[type];
					--sum;
				}
			}
			
		}
		System.out.println("Enter while2");
		while(count-->0)
		{
			NewsBean bean = tryToGetOne(rand.nextInt(CONTENT.length));
			temp.add(bean);
		}
		
		System.out.println("recommend refresh");
		return temp;
	}
	
	
	public NewsBean tryToGetOne(int typeID)
	{
		ArrayList<NewsBean> arrayList = allList.get(typeID);
		if(arrayList.size()!=0)
		{
			Random rand = new Random();
			return arrayList.get(rand.nextInt(arrayList.size()));
		}
		return null;
	}
	
	
	
	public void updateRepository(int typeID)
	{
		if(allList.get(typeID).size()-cursor[typeID]>5)return;
		ArrayList<NewsBean> tempList;
		if(typeID == 0) {tempList = preferGet();}
		else {tempList = parse(typeID);}
		ArrayList<NewsBean> typeList = allList.get(typeID);
		NewsBean item1 = typeList.get(typeList.size()-1);
		NewsBean item2 = tempList.get(tempList.size()-1);
		if(itemCheck(item1,item2))
		{
			System.out.println("no update");
			return;
		}
		
		allList.get(typeID).addAll(tempList);
		
		System.out.println("typeID: "+typeID+" cursor: "+cursor[typeID]+" size: "+typeList.size());
	}
	
	public boolean itemCheck(NewsBean item1, NewsBean item2)
	{
		return item1.title.equals(item2.title);
	}
	
	public static void checkString(InputStream inputStream) throws Exception
	{
		//ByteArrayInputStream byteIn = new ByteArrayInputStream(test.getBytes("ISO-8859-1"));
		
		ObjectInputStream objIn = new ObjectInputStream(inputStream);
		Object obj = null;
		while((obj = objIn.readObject())!=null)
		{
			NewsBean entry = (NewsBean)obj;
			
			System.out.println("标题：" + entry.title);  
            System.out.println("连接地址：" + entry.news_url);  
            System.out.println("标题简介：" + entry.des);  
			
		}
		objIn.close();
	}
	
	
	public static String serializeToString(Object obj) throws Exception{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);  
        objOut.writeObject(obj);  
        String str = byteOut.toString("ISO-8859-1");//此处只能是ISO-8859-1,但是不会影响中文使用
        return str;
    }
    //反序列化
    public static Object deserializeToObject(String str) throws Exception{
         ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));  
         ObjectInputStream objIn = new ObjectInputStream(byteIn);  
         Object obj =objIn.readObject();  
         return obj;  
    }
	
    
    public static byte[] newsToString(List entries) throws Exception
    {
    	ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);  
    	for(int i=0;i<entries.size();++i)
    	{
    		SyndEntry entry = (SyndEntry)entries.get(i);
    		/*
    		System.out.println("标题：" + entry.getTitle());  
            System.out.println("连接地址：" + entry.getLink());  
            SyndContent description = entry.getDescription();  
            System.out.println("标题简介：" + description.getValue());  
            System.out.println("发布时间：" + entry.getPublishedDate());  
    		*/
    		NewsBean bean = new NewsBean();
    		bean.title = entry.getTitle();
    		bean.des = entry.getDescription().getValue();
    		bean.news_url = entry.getLink();
    		bean.icon_url = null;
    		
    		objOut.writeObject(bean);
    	}
    	objOut.writeObject(null);
    	objOut.close();
    	
    	//return byteOut.toString("ISO-8859-1");
    	return byteOut.toByteArray();
    }
    
    
	public static ArrayList<NewsBean> parse(int urlType)//无脑返回20个
	{
		urlType = urlType%(rssList.length);
		String urlString = rssList[urlType];
		ArrayList<NewsBean> arrayList = new ArrayList<>();
		try
		{
			URL url = new URL(urlString);
			
			XmlReader reader = new XmlReader(url);
			//System.out.println("编码格式："+reader.getEncoding());
			
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(reader);
			
			List entries = feed.getEntries();
			
			for(Object entry : entries)
			{
				SyndEntry entity = (SyndEntry)entry;
				NewsBean bean = new NewsBean();
	    		bean.title = entity.getTitle();
	    		bean.des = entity.getDescription().getValue();
	    		bean.news_url = entity.getLink();
	    		
	    		//bean.icon_url = null;
	    		System.out.println(bean.news_url);
	    		bean.icon_url = ImgFinder.getImageURLWithJSoup(bean.news_url);
	    		System.out.println(bean.icon_url);
	    		
	    		arrayList.add(bean);
			}
			//rlt = newsToString(entries);
			
			//for(int i=0;i<entries.size();++i)
			//{
				//SyndEntry entry = (SyndEntry)entries.get(i);
				
				
				
				
				/*
				System.out.println("标题：" + entry.getTitle());  
                System.out.println("连接地址：" + entry.getLink());  
                SyndContent description = entry.getDescription();  
                System.out.println("标题简介：" + description.getValue());  
                System.out.println("发布时间：" + entry.getPublishedDate());  
                  */
                 
                // 以下是Rss源可先的几个部分     
                //System.out.println("标题的作者：" + entry.getAuthor());  
                  
                // 此标题所属的范畴     
                /*List categoryList = entry.getCategories();  
                if (categoryList != null) {  
                    for (int m = 0; m < categoryList.size(); m++) {  
                        SyndCategory category = (SyndCategory) categoryList.get(m);  
                        System.out.println("此标题所属的范畴：" + category.getName());  
                    }  
                }  */
                  
                  
                // 得到流媒体播放文件的信息列表     
                /*List enclosureList = entry.getEnclosures();  
                if (enclosureList != null) {  
                    for (int n = 0; n < enclosureList.size(); n++) {  
                        SyndEnclosure enclosure = (SyndEnclosure) enclosureList.get(n);  
                        System.out.println("流媒体播放文件：" + entry.getEnclosures());  
                    }  
                }  */
			//}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return arrayList;
	}
}

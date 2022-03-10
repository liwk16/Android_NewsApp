package rssparse;

import java.net.URL;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.internal.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.safety.*;
import org.jsoup.select.*;

public class ImgFinder {
	
	public static final String bannedString = "icon_logo";
	
	public static void main(String[] args) throws Exception
	{
		String url = "https://news.qq.com/a/20180907/093131.htm";
		String url2 = "https://news.qq.com/a/20180907/094778.htm";
		
		//String imgUrl = getImgURL(url2);
		//System.out.println(imgUrl);
		String imgURL = getImageURLWithJSoup(url);
		System.out.println(imgURL);
	}
	
	public static String transferURL(String urlString)
	{
		if(!urlString.contains("https"))
		{
			StringBuilder sb = new StringBuilder(urlString);
			int loc = sb.indexOf("http")+4;
			sb.insert(4, "s");
			return sb.toString();
		}
		else return urlString;
	}
	
	public static String getImageURLWithJSoup(String urlString) throws Exception
	{
		urlString = transferURL(urlString);
		Document doc = Jsoup.connect(urlString).get();
		Elements media = doc.select("img");
		
		int area = 0;
		
		for(Element src: media)
		{
			//System.out.println(src.toString());
			if(src.attr("style").contains("display:block"))
			{
				return src.attr("abs:src");
			}
		}
		return null;
	}
	
	
	public static String getImgURL(String urlString) throws Exception
	{
		urlString = transferURL(urlString);
		
		URL url = new URL(urlString);
		InputStreamReader ir = new InputStreamReader(url.openStream());
		BufferedReader br = new BufferedReader(ir);
		
		
		String line;
		//int count = 0;
		while((line = br.readLine())!=null)
		{
			//++count;
			if(line.contains("<img"))
				{
					ArrayList<String> tempList = getImgStr(line);
					for(String str: tempList)
					{
						if(str.contains(bannedString))continue;
						//System.out.println(count);
						if(str.length()<5)continue;
						if(str.contains("||"))continue;
						return str;
					}
				}
		}
		//System.out.println(count);
		return null;
	}
	
    public static ArrayList<String> getImgStr(String htmlStr) {
        ArrayList<String> pics = new ArrayList<String>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }
}	

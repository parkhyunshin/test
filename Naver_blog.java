import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import java.sql.*;
import java.math.*;

import java.net.URL;
import java.net.URLEncoder;

import java.text.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;


/**
 *
 * @author red
 */
public class naver_blog {

    public static void main(String[] args){
/*
 *요청 변수 (request parameter)
        key             // 이용 등록을 통해 받은 key 스트링을 입력(필수)
        target          // 서비스를 위해서는 필수 지정
        query           // 검색을 원하는 질의, UTF-8 인코딩(필수)
        start           // 검색의 시작위치를 지정(최대 1000)
 *출력 결과 필드 (response field)
        link            // 검색 결과 문서의 하이퍼텍스트 link
*/
	String keyword = args[0]; //이부부은 검색어를 UTF-8로 넣어줄거임.  
	String query = null;
	String table = args[1];
	String eclass = "naver_blog";
	String ecoclass = eclass+"_co";
	long redssh = 0;
	
	try {
	query = URLEncoder.encode(keyword,"UTF-8");
	} catch (UnsupportedEncodingException e1) {
	e1.printStackTrace();
	}  

	String key = "0ff763733eaf4f02b1ab6c4ba58fee18";	// API Key
	String [] targets = new String[]{"blog"};
	URL url = null;
	int start = 1;

	long ftime = System.currentTimeMillis();
        SimpleDateFormat fdayTime = new SimpleDateFormat("yyyyMMddHH");
        String fstr = fdayTime.format(new Date(ftime));
	
	String str = null;
	long curtime = System.currentTimeMillis();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	str = sdf.format(curtime);
//while(start < 5000)
//{
	for (String target : targets){
try {
		url = new URL("http://openapi.naver.com/search?"+"key="+key+"&query="+query+"&target="+target+"&start="+start+"&"+"display=100&sort=date");		// API 호출
	} catch (IOException e) {
	System.err.println("Fatal transport error: " + e.getMessage());
	e.printStackTrace();
	}

	String surl = url.toString();

        try {		
		Document basedoc = Jsoup.connect(surl).get(); 

        	Elements baseanchors;				// 
		baseanchors=basedoc.select("item");		// 
/*
 *String 형태인 HashMap을 만들기
	tmap=오늘 , ymap=어제 , comap=commentMap
 * */
		HashMap<String,String> tmap = new HashMap();
		HashMap<String,String> ymap = new HashMap();
		HashMap<String,String> tcomap = new HashMap();
		HashMap<String,String> ycomap = new HashMap();

		String today = str.substring(0,8);
		String yesterday = String.valueOf(Integer.parseInt(str.substring(0,8))-1);

		tmap = new keymap().readmap(today,eclass,args[1],args[0]);
		ymap = new keymap().readmap(yesterday,eclass,args[1],args[0]);

		tcomap = new keymap().readmap(today,ecoclass,args[1],args[0]);
		ycomap = new keymap().readmap(yesterday,ecoclass,args[1],args[0]);

		int num = 1;		

		for (Element baseanchor: baseanchors) {

		boolean go = false;
		boolean cogo = false;

		String link1 = baseanchor.select("link").first().ownText();

		link1 = link1.replace(" ","");

		String link2 = baseanchor.select("bloggerlink").first().ownText();

		if (link1 == null || link1 == ""||link1.length() == 0)
		{
			link1 = baseanchor.select("link").first().nextSibling().toString().replace("=","");
		}	

		if (ymap == null || !ymap.containsKey(link1))
		{
			if(tmap == null || !tmap.containsKey(link1))
			{
				go = true;
			}
		}

		if (tmap == null)
		{
			tmap = new HashMap();
		}

		try {

			String colink = null;
			Document codoc = null;
			String codate = null;
			String cobody = null;
			String borco = "b";
			String pwnum = null;
			String mwnum = null;
			int ipwnum = 0;
			int imwnum = 0;
			String lastlink = null;

			Document seconddoc = Jsoup.connect(link1).userAgent("Mozilla").timeout(0).get();
			Elements elements = seconddoc.select("frame");


			String red = null;
			String body = "check";
			String date = null;
			
			if (elements.size() != 0)
			{
				String rednum = null;
				for (Element element : elements)
				{
					red = element.attr("src");

					if(red.indexOf("logNo=") != -1 && red.indexOf("&beginTime") != -1)
					{
						rednum = red.substring(red.lastIndexOf("logNo=")+6,red.indexOf("&beginTime"));
					}

					if (rednum == null && red.lastIndexOf("articleno=") != -1 && red.indexOf("&amp") != -1)
					{
						rednum = red.substring(red.lastIndexOf("articleno=")+10,red.indexOf("&amp"));
					}

					if (rednum == null && red.lastIndexOf("articleno=") != -1 && red.indexOf("&admin") != -1)
					{
						rednum = red.substring(red.lastIndexOf("articleno=")+10,red.indexOf("&admin"));
					}

					if (rednum == null && red.lastIndexOf("postSeq=") != -1 && red.indexOf("&amp") != -1)
					{
						rednum = red.substring(red.lastIndexOf("postSeq=")+8,red.indexOf("&amp"));
					}

					if (rednum == null && red.lastIndexOf("postSeq=") != -1 && red.indexOf("&mf") != -1)
					{
						rednum = red.substring(red.lastIndexOf("postSeq=")+8,red.indexOf("&mf"));
					}
				}

				lastlink = link2+"/"+rednum;

				if (lastlink.indexOf("http://") == -1 && lastlink.indexOf("www") == -1 && lastlink.indexOf("blog") != -1)
				{
					lastlink = lastlink.replace("blog","http://m.blog");
				}

				else if(lastlink.indexOf("www") != -1)
				{
					lastlink = lastlink.replace("www","http://m");
				}
				else if(lastlink.indexOf("http://") != -1)
				{
					lastlink = lastlink.replace("http://","http://m.");
				}


				if (lastlink.indexOf("http://m.blog.naver.com") != -1)
				{
					String user_id = null;
					user_id = lastlink.substring(lastlink.indexOf("com/")+4,lastlink.lastIndexOf("/"));
					colink = lastlink.replace(user_id+"/","CommentList.nhn?blogId="+user_id+"&logNo=");
				}


			if(go == true)
			{
				Document doc = Jsoup.connect(lastlink).userAgent("Mozilla").timeout(0).get();

				if (lastlink.indexOf("cyworld") != -1)
				{

					Elements baseelements = doc.select("br");
			                StringBuilder rsb = new StringBuilder();
			                for (Element baseelement : baseelements)
			                {
                        			String rline = null;
			                        if((rline = baseelement.select("br").first().nextSibling().toString()) != null)
						{
			                                rsb.append(rline);
	                                        }
                                        }

					body = rsb.toString();
					date = doc.getElementsByClass("view_title").text();
			                date = date.substring(date.indexOf("|")+1).replace(":","").replace(" ","").replace(".","");

				}
				else if (lastlink.indexOf("naver") != -1)
				{
					if (doc.select("div#_post_property").size() != 0)
					{
						long unidate = Long.parseLong(doc.select("div#_post_property").attr("adddate"));
						redssh = unidate;
						Date date2 = new Date(unidate);
						date = sdf.format(date2);
						body = doc.select("p").text();
					}	
				}	

				else if (lastlink.indexOf("daum") != -1)
				{
					Elements baseelements = doc.select("br");
                                        StringBuilder rsb = new StringBuilder();
                                        for (Element baseelement : baseelements)
                                        {
                                                String rline = null;
                                                if(baseelement.select("br").text().length() != 0)
                                                {
                                                        rsb.append(baseelement.select("br").first().nextSibling().toString());
                                                }
                                        }

                                        body = rsb.toString();

					if (body == null)
					{
						body = doc.select("p").text();
					}


					Elements subeles = doc.select("span");
					for (Element subele : subeles)
					{
						if (subele.attr("class").equals("date"))
						{
							date = subele.text().replace(":","").replace(" ","").replace(".","");
						}
					}
				}
			} 
			}
			else
			{
			if (go == true)
			{
				body = seconddoc.select("p").text();
				lastlink = link1;
				Elements subeles = seconddoc.select("span");
                                        for (Element subele : subeles)
                                        {
                                                if (subele.attr("class").equals("date"))
                                                {
                                                        date = subele.text().replace(":","").replace(" ","").replace("|","");
                                                }
                                        }
				if (date == null || date.length() == 0)
				{
					Elements subeles2 = seconddoc.select("dd");
					for (Element subele2 : subeles2)
                                        {
                                                if (subele2.attr("class").equals("date"))
                                                {
                                                        date = subele2.text().replace(":","").replace(" ","").replace("-","");
                                                }
                                        }
				}

				if (date == null || date.length() == 0)
				{
					Elements subeles2 = seconddoc.select("abbr");
					for (Element subele2 : subeles2)
                                        {
                                                if (subele2.attr("class").equals("published"))
                                                {
                                                        date = subele2.text().replace(":","").replace(" ","").replace("/","");
                                                }
                                        }
				}

				if (date == null || date.length() == 0)
				{
					Elements subeles2 = seconddoc.select("em");
					for (Element subele2 : subeles2)
                                        {
                                                if (subele2.attr("class").equals("date"))
                                                {
                                                        date = subele2.text().replace(":","").replace(" ","").replace("/","");
                                                }
                                        }
				}
			}
			}

			if (body.length() != 0 && go == true)
			{	
				if (date != null && date.length() >= 12)
				{
					date = date.replace("/","").substring(0,12);
				}
				else
				{
					date = null;
				}

				BigDecimal bnum = new BigDecimal(num);

				tmap.put(link1,"1");

				new db_insert().insert(str,date,target,bnum,lastlink,body,args[1],args[0],eclass,borco,colink,ipwnum,imwnum);
			}
			
			


			if (ycomap == null || !ycomap.containsKey(colink))
			{
				if(tcomap == null || !tcomap.containsKey(colink))
				{
					cogo = true;
				}
			}

			if (tcomap == null)
			{
				tcomap = new HashMap();
			}

			if (colink != null && cogo == true)
			{				
				codoc = Jsoup.connect(colink).userAgent("Mozilla").timeout(0).get();
				Elements elementss = codoc.select("li");
				int i = 1;
				int lnum = 9999;

				for (Element element : elementss)
				{
					BigDecimal bconum = new BigDecimal(i);

					int conum  = element.select("li").size();
					String wnum = null;
					
					if (element.getElementsByClass("txt").select("p").size() > 0)
					{
						wnum = element.getElementsByClass("txt").select("p").first().attr("id");
						cobody = element.getElementsByClass("txt").select("p").first().text();
					}

		                        if (wnum == null || wnum.length() == 0)
		                        {
		                                wnum = element.select("div").attr("id");
		                                wnum = wnum.substring(wnum.indexOf("_"));

						if (element.select("p").size() > 0)
						{
							cobody = element.select("p").first().text();
						}
						else
						{
							cobody = element.select("p").text();
						}
		                        }
		
		                        if (wnum != null)
		                        {
		                                mwnum = wnum.substring(wnum.indexOf("_")+1);
		                        }
		
		                        if (conum > 1)
		                        {
		                                lnum = conum;
		                                pwnum = mwnum;
		                        }
		                        else if (conum == 1 && lnum == 9999)
		                        {
		                                pwnum = mwnum;
		                        }
		
		                        if (lnum != 9999)
		                        {
		                                lnum = lnum - 1;
		                        }
		
		                        if (lnum == 0)
		                        {
		                                lnum = 9999;
		                        }
		
		                        codate = element.getElementsByClass("dsc_date").first().text();
		
		                        int cotime = 0;
		                        int comin = 0;
		
					if (codate.indexOf("시") != -1)
		                        {
		                                cotime = Integer.parseInt(codate.substring(0,codate.indexOf("시")));
		                        }
		
		                        if (codate.indexOf("분") != -1)
		                        {
		                                comin = Integer.parseInt(codate.substring(0,codate.indexOf("분")));
		                        }
		
		                        long cacul = Long.parseLong(String.valueOf((cotime * 60 * 60) + (comin * 60)));
		
		                        long unixTime = System.currentTimeMillis()/1000L;
		
		                        if (cotime == 0 && comin == 0)
		                        {
		                                codate = codate.replace(".","").replace(" ","").replace(":","");
		                        }
		                        else
		                        {
		                                codate = sdf.format(new Date((unixTime - cacul)*1000L));
		                        }


					if (!ycomap.containsValue(codate))
	                                {
						if(!ycomap.containsValue(codate))
						{
							ipwnum = Integer.parseInt(pwnum);
							imwnum = Integer.parseInt(mwnum);
							borco = "c";
							new db_insert().insert(str,codate,target,bconum,lastlink,cobody,args[1],args[0],eclass,borco,colink,ipwnum,imwnum);
							tcomap.put(colink,codate);
							i = i + 1;							
						}
					}

				}
			}

		}catch (IOException ex) {
			new db_insert().error_up(str,eclass,args[1],args[0],ex.toString());
		}
		num = num + 1;
			
		}

		new keymap().savemap(tcomap,str,ecoclass,args[1],args[0]);

		new keymap().savemap(tmap,str,eclass,args[1],args[0]);

	} catch (IOException ex) {
		new db_insert().error_up(str,eclass,args[1],args[0],ex.toString());
    	} 	
	}
	start = start + 100;
//}
    }

}


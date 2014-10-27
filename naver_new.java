import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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
public class naver_news {
    public static void main(String[] args){
        String keyword = args[0]; //이부부은 검색어를 UTF-8로 넣어줄거임.
        String query = null;
        String table = args[1];
        String eclass = "naver_news";

        try {
        query = URLEncoder.encode(keyword,"UTF-8");
        } catch (UnsupportedEncodingException e1) {
        e1.printStackTrace();
        }

        String key = "0ff763733eaf4f02b1ab6c4ba58fee18";
        String [] targets = new String[]{"news"};
        URL url = null;
        int start = 1;

        long ftime = System.currentTimeMillis();
        SimpleDateFormat fdayTime = new SimpleDateFormat("yyyyMMddHH");
        String fstr = fdayTime.format(new Date(ftime));

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmm");
        String str = dayTime.format(new Date(time));

//while(start < 5000)
//{
        for (String target : targets){
        try {
                url = new URL("http://openapi.naver.com/search?"+"key="+key+"&query="+query+"&target="+target+"&start="+start+"&sort=date&"+"display=100");
        } catch (IOException e) {
        System.err.println("Fatal transport error: " + e.getMessage());
        e.printStackTrace();
        }

        String surl = url.toString();

        try {
                Document basedoc = Jsoup.connect(surl).get();

                Elements baseanchors;
                baseanchors=basedoc.select("item");

                int num = 1;

                HashMap<String,String> tmap = new HashMap();
                HashMap<String,String> ymap = new HashMap();
                HashMap<String,String> tcomap = new HashMap();
                HashMap<String,String> ycomap = new HashMap();

                String colink = null;
                Document codoc = null;
                String codate = null;
                String cobody = null;
                String borco = "b";
                String pwnum = null;
                String mwnum = null;
                int ipwnum = 0;
                int imwnum = 0;

                String today = str.substring(0,8);
                String yesterday = String.valueOf(Integer.parseInt(str.substring(0,8))-1);

                tmap = new keymap().readmap(today,eclass,args[1],args[0]);
                ymap = new keymap().readmap(yesterday,eclass,args[1],args[0]);

                for (Element baseanchor: baseanchors) {

                String link = baseanchor.select("originallink").first().ownText();
                link = link.replace(" ","");
                boolean go = false;

                if (link == null || link == ""||link.length() == 0)
                {
                        link = baseanchor.select("link").first().nextSibling().toString().replace("=","");
                }
                if (ymap == null || !ymap.containsKey(link))
                {
                        if(tmap == null || !tmap.containsKey(link))
                        {
                                go = true;
                        }
                }

                if (tmap == null)
                {
                        tmap = new HashMap();
                }

                if(go == true)
                {
                try {
                        Document doc = Jsoup.connect(link).userAgent("Mozilla").timeout(0).get();

                        String text = doc.text();

                        Pattern p = Pattern.compile(".*(201[4-9])(-|.|년|년 )([1-9]|0[1-9]|1[0-2])(-|.|월|월 )(3[0-1]|[1-2][0-9]|0[1-9]|[0-9]).*(1[0-9]|2[0-3]|[0-9])(:|.|시|시 )([1-5][0-9]|0[1-9]|[1-9]).*");

                        Matcher m = p.matcher(text);

                        String ndate = null;

                        boolean b = m.matches();

                        if(b && m.groupCount() == 8)
                        {
                                String year = m.group(1);
                                String mon = m.group(3);
                                String day = m.group(5);
                                String ht = m.group(6);
                                String mt = m.group(8);
                                if (m.group(3).length() == 1)
                                {
                                        mon = "0" + m.group(3);
                                }
                                if (m.group(5).length() == 1)
                                {
                                        day = "0" + m.group(5);
                                }
                                if (m.group(6).length() == 1)
                                {
                                        ht = "0" + m.group(6);
                                }
                                if (m.group(8).length() == 1)
                                {
                                        mt = "0" + m.group(8);
                                }
                                ndate = year+mon+day+ht+mt;
                        }
                        else
                        {
                                ndate = "null";
                        }

                        Elements anchors;

                        String body=null;
                if (target == "news")
                {
                        if (doc.getElementsByClass("view_r").size() != 0)
                        {
                                body = doc.getElementsByClass("view_r").first().text();
                        }

                        else if (doc.getElementsByClass("news_body_area").size() != 0)
                        {
                                body = doc.getElementsByClass("news_body_area").first().text();
                        }

                        else if (doc.getElementsByClass("view_setting").size() != 0)
                        {
                                body = doc.getElementsByClass("view_setting").first().text();
                        }

                        else if (doc.getElementsByClass("news_bm").size() != 0)
                        {
                                body = doc.getElementsByClass("news_bm").first().text();
                        }

                        else if (doc.getElementsByClass("news_text").size() != 0)
                        {
                                body = doc.getElementsByClass("news_text").first().text();
                        }

                        else if (doc.getElementsByClass("container").size() != 0)
                        {
                                body = doc.getElementsByClass("container").first().text();
                        }

                        else if (doc.getElementsByClass("tab01").size() != 0)
                        {
                                body = doc.getElementsByClass("tab01").first().text();
                        }

                        else if (doc.getElementsByClass("container_sub_wrap").size() != 0)
                        {
                                body = doc.getElementsByClass("container_sub_wrap").first().text();
                        }

                        else if (doc.getElementsByClass("read_txt").size() != 0)
                        {
                                body = doc.getElementsByClass("read_txt").first().text();
                        }

                        else if (doc.getElementsByClass("viewConts").size() != 0)
                        {
                                body = doc.getElementsByClass("viewConts").first().text();
                        }

                        else if (doc.getElementsByClass("CONTENT_TXT").size() != 0)
                        {
                                body = doc.getElementsByClass("CONTENT_TXT").first().text();
                        }

                        else if (doc.getElementsByClass("btn_area01").size() != 0)
                        {
                                body = doc.getElementsByClass("btn_area01").first().text();
                        }

                        else if (doc.getElementsByClass("article-text").size() != 0)
                        {
                                body = doc.getElementsByClass("article-text").first().text();
                        }

                        else if (doc.getElementsByClass("caption_split").size() != 0)
                        {
                                body = doc.getElementsByClass("caption_split").first().text();
                        }

                        else if (doc.getElementsByClass("article").size() != 0)
                        {
                                body = doc.getElementsByClass("article").first().text();
                        }

                        else if (doc.select("div#news_text").size() != 0)
                        {
                                body = doc.select("div#news_text").first().text();
                        }

                        else if (doc.select("div#CmAdContent").size() != 0)
                        {
                                body = doc.select("div#CmAdContent").first().text();
                        }

                        else if (doc.select("div#news_read_text").size() != 0)
                        {
                                body = doc.select("div#news_read_text").first().text();
                        }

                        else if (doc.select("div#viewcontent_ad").size() != 0)
                        {
                                body = doc.select("div#viewcontent_ad").first().text();
                        }

                        else if (doc.select("div#ContentMain").size() != 0)
                        {
                                body = doc.select("div#ContentMain").first().text();
                        }

                        else if (doc.select("class#read_body").size() != 0)
                        {
                                body = doc.select("class#read_body").first().text();
                        }

                        else if (doc.select("div#article").size() != 0)
                        {
                                body = doc.select("div#article").first().text();
                        }

                        else if (doc.select("div#articleBody").size() != 0)
                        {
                                body = doc.select("div#articleBody").first().text();
                        }

                        else if (doc.select("div#container").size() != 0)
                        {
                                body = doc.select("div#container").first().text();
                        }

                        else if (doc.select("div#content_ADTOM").size() != 0)
                        {
                                body = doc.select("div#content_ADTOM").first().text();
                        }

                        else if (doc.select("div#content").size() != 0)
                        {
                                body = doc.select("div#content").first().text();
                        }

                        else if (doc.select("div#GS_Content").size() != 0)
                        {
                                body = doc.select("div#GS_Content").first().text();
                        }

                        else if (doc.select("div#ct").size() != 0)
                        {
                                body = doc.select("div#ct").first().text();
                        }

                        else if (doc.select("div#bodytext").size() != 0)
                        {
                                body = doc.select("div#bodytext").first().text();
                        }

                        else if (doc.select("div#CLtag").size() != 0)
                        {
                                body = doc.select("div#CLtag").first().text();
                        }

                        else if (doc.select("div#newsContent").size() != 0)
                        {
                                body = doc.select("div#newsContent").first().text();
                        }

                        else if (doc.select("div#news_textArea").size() != 0)
                        {
                                body = doc.select("div#news_textArea").first().text();
                        }

                        else if (doc.select("div#article_content").size() != 0)
                        {
                                body = doc.select("div#article_content").first().text();
                        }

                        else if (doc.select("div#NewsAdContent").size() != 0)
                        {
                                body = doc.select("div#NewsAdContent").first().text();
                        }

                        else if (doc.select("div#article-body").size() != 0)
                        {
                                body = doc.select("div#article-body").first().text();
                        }

                        else if ( doc.getElementsByClass("article_body").size() != 0)
                        {
                                Document doc2 = doc;

                                doc2.select("a").remove();
                                doc2.select("b").remove();
                                doc2.select("p").remove();
                                doc2.select("h1").remove();
                                doc2.select("h2").remove();
                                doc2.select("h3").remove();

                                anchors = doc2.getElementsByClass("article_body");

                                body = anchors.first().text();
                        }

                        else
                        {
                                body="check";
                        }
                }

                BigDecimal bnum = new BigDecimal(num);

                tmap.put(link,"1");

                new db_insert().insert(str,ndate,target,bnum,link,body,args[1],args[0],eclass,borco,colink,ipwnum,imwnum);

                }catch (IOException ex) {
                        new db_insert().error_up(str,eclass,args[1],args[0],ex.toString());
                }
                }
                num = num + 1;

                }

                new keymap().savemap(tmap,str,eclass,args[1],args[0]);

        } catch (IOException ex) {
                new db_insert().error_up(str,eclass,args[1],args[0],ex.toString());
        }
        }
start = start + 100;
//}
}
}

package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class Kn extends Spider {
    private final String url="https://knvod.com/";
    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";
    private final String cookie="ecPopup=1;X-Robots-Tag=CDN-VERIFY";
    private String req(String url, Map<String, String> header) {
        return OkHttp.string(url, header);
    }
    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Cookie", cookie);
        return header;
    }
    public String homeContent(boolean filter) throws Exception {
        String html = req(url, getHeader());
        Document doc = Jsoup.parse(html);
        System.out.println(doc);
        return "s";
    }
    public static void main (String[]args){
        try {
            Kn kn = new Kn();
//             测试 homeContent 方法
            String homeContent = kn.homeContent(false);
            System.out.println("Home Content: " + homeContent);}
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * @author bgcode
 * 56动漫
 */
public class Dm56 extends Spider {
    private final String siteUrl="https://www.56dm.cc";
    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";
    private String req(String url, Map<String, String> header) {
        return OkHttp.string(url, header);
    }
    private final String cookie="PHPSESSID=ejukn60i0o1jcaq9cb4btnnr0n; _ga_8JCZ6DPVZK=GS1.1.1741625610.1.0.1741625610.60.0.0; _ga=GA1.1.2083697891.1741625611; notice_closed=true";

    public  String decodeUnicode(String input) throws UnsupportedEncodingException {
        Pattern pattern = Pattern.compile("%u([0-9A-Fa-f]{4})");
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            int codePoint = Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, new String(Character.toChars(codePoint)));
        }
        matcher.appendTail(sb);
        return URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8.name());

    }
   

    private  String decrypt(String data, String keyHex, String ivHex) throws Exception {
        byte[] keyBytes = hexToBytes(keyHex);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        byte[] ivBytes = hexToBytes(ivHex);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decodedData = Base64.getDecoder().decode(data);
        byte[] decryptedBytes = cipher.doFinal(decodedData);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    private Response req(Request request) throws Exception {
        return okClient().newCall(request).execute();
    }
    private String req(Response response) throws Exception {
        if (!response.isSuccessful()) return "";
        String content = response.body().string();
        response.close();
        return content;
    }
    private OkHttpClient okClient() {
        return OkHttp.client();
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }
    private Response req(String url) throws Exception {
        return OkHttp.newCall(url);
    }
    private Map<String, String> getHeader(String referer) {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Referer", referer);
        return header;
    }
    private Map<String, String> getHeader(String referer, String cookie) {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Referer", referer);
        header.put("Cookie", cookie);
        return header;
    }
    private Map<String, String> getSearchHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }
    private String find(String regex, String html) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(html);
        return m.find() ? m.group(1).trim() : "";
    }
    private String find(String regex, String html, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(html);
        return m.find() ? m.group(group).trim() : "";
    }
    private  String decodeUnicodeEscape(String input) throws UnsupportedEncodingException {
        // 定义匹配 %u 形式的 Unicode 转义序列的正则表达式
        Pattern pattern = Pattern.compile("%u([0-9A-Fa-f]{4})");
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            char c = (char) Integer.parseInt(hex, 16);
            matcher.appendReplacement(result, String.valueOf(c));
        }
        matcher.appendTail(result);
        String a=result.toString();
        System.out.println(a);
        try {
            a=URLDecoder.decode(a, "UTF-8");
            StringBuilder result1 = new StringBuilder();
            for (int i = 0; i < a.length(); i++) {
                char c = a.charAt(i);
                if (c >= '\u4e00' && c <= '\u9fff') {
                    result1.append(URLEncoder.encode(String.valueOf(c), "UTF-8"));
                } else {
                    result1.append(c);
                }
            }
            return result1.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return input;
        }

    }

    private Map<String, String> getsHeader(String searchUrl,String cookie) {
        Map<String, String> header = new HashMap<>();
        header.put("host", "www.56dm.cc");
        header.put("accept", "application/json, text/javascript, */*; q=0.01");
        header.put("accept-encoding", "gzip, deflate, br, zstd");
        header.put("accept-language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        header.put("content-length", "0");
        header.put("cookie", cookie);
        header.put("dnt", "1");
        header.put("origin", "https://www.56dm.cc");
        header.put("priority", "u=1, i");
        header.put("referer", searchUrl);
        header.put("sec-ch-ua", "\"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Google Chrome\";v=\"134\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "\"macOS\"");
        header.put("sec-fetch-dest", "empty");
        header.put("sec-fetch-mode", "cors");
        header.put("sec-fetch-site", "same-origin");
        header.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        header.put("x-requested-with", "XMLHttpRequest");
        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        String html = req(siteUrl, getHeader());
        Document doc = Jsoup.parse(html);
        Elements aList = doc.select(".snui-header-menu-nav> ul>li > a");
        JSONArray classes = new JSONArray();
        for (int i = 1; i < aList.size(); i++) {
            Element a = aList.get(i);
            String text = a.text();
            String href = a.attr("href");
            // 使用 equals 方法进行字符串比较
            if ("/".equals(href)) {
                // 首页特殊处理
                classes.put(new JSONObject().put("type_id", "").put("type_name", text));
            } else if (href != null && href.contains("/type/")) {
                // 只记录符合条件的链接
                String typeId =  find("/type/(\\d+)\\.html", href);
                classes.put(new JSONObject().put("type_id", typeId).put("type_name", text));
            }
        }
        Elements elements = doc.select("[class=cCBf_FAAEfbc clearfix] > li");
        JSONArray videos = new JSONArray();
        for (Element element : elements) {
            Element a = element.selectFirst("a");
            String vodId = a.attr("href");
            String name = a.attr("title");
            String pic = a.attr("data-original");
            String remark = element.select(".dAD_BBCI").text();

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl = siteUrl + "/type/" + tid+".html";
        if (!pg.equals("1")) cateUrl =  siteUrl + "/type/" +tid+"-"+pg+".html";
        String html = req(cateUrl, getHeader(siteUrl));
        JSONArray videos = new JSONArray();
        Document doc = Jsoup.parse(html);
        Elements items = doc.select(".cCBf_FAAEfbc>li");
        for (Element item : items) {
            String vodId = item.select(".cCBf_FAAEfbc__dbD > a").attr("href");
            String name = item.select(".cCBf_FAAEfbc__dbD > a").attr("title");;
            String pic = item.select(".cCBf_FAAEfbc__dbD > a").attr("data-original");
            String remark = item.select(".dAD_BBCI>b").text();
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        int page = Integer.parseInt(pg), count ;
        try {
            Element last = Jsoup.parse(html).select(".cCBf_ADFF__bdFa >li>a").last();
            String lastPageNum1 =last.attr("href");
            count=  Integer.parseInt(find("/type/\\d+-(\\d+)\\.html", lastPageNum1));
        } catch (Exception ignore) {
            count = 1;
        }
        JSONObject result = new JSONObject();
        result.put("page", page);
        result.put("pagecount", count);
        result.put("limit", videos.length());
        result.put("total", Integer.MAX_VALUE);
        result.put("list", videos);
        return result.toString();
    }


    @Override
    public String detailContent(List<String> ids) throws Exception {
        String igd = ids.get(0);
        String detailUrl = siteUrl + igd;
        String html = req(detailUrl, getHeader());
        Document doc = Jsoup.parse(html);

        String name = doc.select(".cCBf_DABCcac__hcIdeE>h1").text();

        String pic = doc.select(".cCBf_DABCcac__deCef >a> img").attr("data-original");
        Elements as=doc.select(".cCBf_DABCcac__hcIdeE>p");
        String  typeName = as.get(0).text();
        String year = as.get(4).text();;
        String area = as.get(1).text();

        String remark = as.get(8).text();
        String director = find("导演：(.*?)</p>", html);
        String actor = find("主演：(.*?)</p>", html);
        String description = find("剧情：(.*?)</p>", html);
        Elements circuits = doc.select(".channel-tab>li");
        Map<String, String> playMap = new LinkedHashMap<>();
        for (Element circuit : circuits) {
            String circuitName = circuit.select("a").text();
            String circuitUrl = circuit.select("a").attr("href");
            Elements sourceList = doc.select(circuitUrl+">ul>li>a");
            List<String> vodItems = new ArrayList<>();
            for (Element source : sourceList){
                String episodeUrl = siteUrl + source.attr("href");
                String sa=source.text();
                String  a=  find("\\d+",sa,0);
                String episodeName = "第" + a+ "集";
                vodItems.add(episodeName + "$" + episodeUrl);
                if (vodItems.size() > 0) {
                    playMap.put(circuitName, TextUtils.join("#", vodItems));
                }
            }
        }

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name); // 影片名称
        vod.put("vod_pic", pic); // 图片
        vod.put("type_name", typeName); // 影片类型 选填
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", area); // 地区 选填
        vod.put("vod_remarks", remark); // 备注 选填
        vod.put("vod_actor", actor); // 主演 选填
        vod.put("vod_director", director); // 导演 选填
        vod.put("vod_content", description); // 简介 选填
        if (playMap.size() > 0) {
            vod.put("vod_play_from", TextUtils.join("$$$", playMap.keySet()));
            vod.put("vod_play_url", TextUtils.join("$$$", playMap.values()));
        }
        JSONArray jsonArray = new JSONArray().put(vod);
        JSONObject result = new JSONObject().put("list", jsonArray);
        return result.toString();
    }



    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String html ;
        String searchUrl;
        if ("1".equals(pg)) {
            searchUrl = siteUrl + "/search/-------------.html?wd=" + URLEncoder.encode(key);
        } else {
            searchUrl = siteUrl + "/search/" + URLEncoder.encode(key) + "----------" + pg + "---.html";
        }
        String cookie="";
        Response response=req(searchUrl);        
        Headers headers = response.headers();
        cookie = headers.get("set-cookie").split(";")[0];
        OkHttp.post("https://www.56dm.cc/index.php/ajax/verify_check?type=search","{}",getsHeader(searchUrl,cookie));
        html = req(searchUrl, getHeader(searchUrl, cookie+"; notice_closed=true"));
        Document doc = Jsoup.parse(html);
        System.out.println(html);
        Elements elements = doc.select("[class=cCBf_FAAEfbc clearfix] > li");
        JSONArray videos = new JSONArray();
        for (Element element : elements) {
            Element a = element.selectFirst("a");
            String vodId = a.attr("href");
            String name = a.attr("title");
            String pic = a.attr("data-original");
            String remark = element.select(".dAD_BBCI").text();
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

   
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        // String html = req(id, getHeader(siteUrl));
        // JSONObject jsonObject = new JSONObject(find("var player_aaaa=(.*?)</script>", html));
        // JSONObject vodData = jsonObject.getJSONObject("vod_data");
        JSONObject result = new JSONObject();
        // String dmid = new JSONObject(find("var d4ddy=(.*?)</script>", html)).get("dmid").toString();
        // String link = jsonObject.get("link").toString();
        // String nid = jsonObject.get("nid").toString();
        // String link_next = jsonObject.get("link_next").toString();
        // String url = decodeUnicodeEscape(jsonObject.get("url").toString());
        // String sid = jsonObject.get("sid").toString();
        // String name = decodeUnicodeEscape(vodData.get("vod_name").toString());
        // String pic = vodData.get("vod_pic").toString();
        // String MacPlayerConfig = jsonObject.get("from").toString();
        // String finalurl="";
        // if(MacPlayerConfig.equals("tudou")){
        //     finalurl="https://art2.v2player.top:8989/player/?t=td&url="+ url +"&dmid="+ dmid +"&next="+link_next+"&name="+name+"&sid="+sid+"&nid="+nid+"&cur="+link+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc&pic="+pic;
        // }else if (MacPlayerConfig.equals("1080zyk")||MacPlayerConfig.equals("bfzym3u8")||MacPlayerConfig.equals("mp4")||MacPlayerConfig.equals("xigua")||MacPlayerConfig.equals("ffm3u8")||MacPlayerConfig.equals("lzm3u8")){
        //     finalurl="https://art.v2player.top:8989/player/?url="+url +"&dmid="+ dmid +"&next="+link_next+"&name="+name+"&nid="+nid+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc&pic="+pic;
        // }else if(MacPlayerConfig.equals("mag")){
        //     finalurl="https://art2.v2player.top:8989/player/?t=bus&url="+ url +"&dmid="+ dmid +"&next="+link_next+"&name="+name+"&sid="+sid+"&nid="+nid+"&cur="+link+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc&pic="+pic ;
        // }
        // System.out.println(finalurl);
        // html = req(finalurl, getHeader(siteUrl));
        // String lasturl;String fainlurl1 = "";

        // if(html.contains("playData")){
        //    lasturl = find("playData\\(\\'(.*?)\\'\\)", html);
        //    String[] parts = lasturl.split("\',\'");
        //    String data = parts[0];
        //    String ivHex = parts[1];
        //    String keyHex = "41424142454637373739393943434344";
        //    fainlurl1= decrypt(data, keyHex, ivHex);
        // }else if(html.contains("Artplayer")){
        //    fainlurl1 = find("url: '(.*?)\\'", html).replace("https://m3u8xx.sgzm.net:2087/","");
        // }
        // fainlurl1=decodeUnicodeEscape(fainlurl1);
        result.put("parse", 1);
        result.put("header", "");
        result.put("playUrl", "");
        result.put("url", id);
        return result.toString();
}

}
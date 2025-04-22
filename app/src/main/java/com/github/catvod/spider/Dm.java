package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.net.getData;
import com.github.catvod.utils.decrpy;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author bgcode
 * 56动漫
 */
public class Dm extends Spider {
    private final String siteUrl = "https://www.56dm.cc";
    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";
    private String req(String url, Map<String, String> header) {
        return OkHttp.string(url, header);
    }
    private String header(String key, String value) throws Exception {
        return   getData.req(key,value);
    }
    private OkResult post(String url, String json, Map<String, String> header) {
        return   OkHttp.post(url, json, header);
    }
    private Response req(String url) throws IOException {
        return OkHttp.newCall(url);
    }
    private String decrypt(String data, String key, String iv) throws Exception {
        return decrpy.Dm(data,key,iv);
    }
    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
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
    public  String decodeUnicodeEscape(String input) throws UnsupportedEncodingException {
        // 定义匹配 %u 形式的 Unicode 转义序列的正则表达式
        Pattern pattern = Pattern.compile("%u([0-9A-Fa-f]{4})");
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            char c = (char) Integer.parseInt(hex, 16);
//            matcher.appendReplacement(result, String.valueOf(c));
        }
//        matcher.appendTail(result);
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
        header.put("cookie", cookie);
        header.put("referer", searchUrl);
        header.put("user-agent", userAgent);
        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        String html = req(siteUrl, getHeader());
        Document doc = Jsoup.parse(html);
        Elements aList = doc.select(".snui-header-menu-nav> ul>li > a");
        JSONArray classes = new JSONArray();
        for (int i = 0; i < aList.size(); i++) {
            Element a = aList.get(i);
            String text = a.text();
            String href = a.attr("href");
            // 使用 equals 方法进行字符串比较
            if ("/".equals(href)) {
                // 首页特殊处理
                classes.put(new JSONObject().put("type_id", "").put("type_name", text));
            } else if (href != null && href.contains("/type/")) {
                // 只记录符合条件的链接
                String typeId = find("/type/(\\d+)\\.html", href);
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
        String cateUrl = siteUrl + "/type/" + tid + ".html";
        if (!pg.equals("1")) cateUrl = siteUrl + "/type/" + tid + "-" + pg + ".html";
        String html = req(cateUrl, getHeader(siteUrl));
        JSONArray videos = new JSONArray();
        Document doc = Jsoup.parse(html);
        Elements items = doc.select(".cCBf_FAAEfbc>li");
        for (Element item : items) {
            String vodId = item.select(".cCBf_FAAEfbc__dbD > a").attr("href");
            String name = item.select(".cCBf_FAAEfbc__dbD > a").attr("title");
            String pic = item.select(".cCBf_FAAEfbc__dbD > a").attr("data-original");
            String remark = item.select(".dAD_BBCI>b").text();
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        int page = Integer.parseInt(pg), count;
        try {
            Element last = Jsoup.parse(html).select(".cCBf_ADFF__bdFa >li>a").last();
            String lastPageNum1 = last.attr("href");
            count = Integer.parseInt(find("/type/\\d+-(\\d+)\\.html", lastPageNum1));
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
        Elements as = doc.select(".cCBf_DABCcac__hcIdeE>p");
        String typeName = as.get(0).text();
        String year = as.get(4).text();
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
            Elements sourceList = doc.select(circuitUrl + ">ul>li>a");
            List<String> vodItems = new ArrayList<>();
            for (Element source : sourceList) {
                String episodeUrl = siteUrl + source.attr("href");
                String sa = source.text();
                String a = find("\\d+", sa, 0);
                String episodeName = "第" + a + "集";
                vodItems.add(episodeName + "$" + episodeUrl);
//                System.out.println(episodeUrl);
//                if (vodItems.size() > 0) {
//                 playMap.put(circuitName, TextUtils.join("#", vodItems));
//            }
                System.out.println(vodItems);
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
//        if (playMap.size() > 0) {
//             vod.put("vod_play_from", TextUtils.join("$$$", playMap.keySet()));
//             vod.put("vod_play_url", TextUtils.join("$$$", playMap.values()));
//        }
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
        String  cookie = header(searchUrl,"set-cookie").split(";")[0];
        post("https://www.56dm.cc/index.php/ajax/verify_check?type=search","{}",getsHeader(searchUrl,cookie));
        html = req(searchUrl, getHeader(searchUrl, cookie+"; notice_closed=true"));
        Document doc = Jsoup.parse(html);
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

    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String html = req(id, getHeader(siteUrl));
        JSONObject jsonObject = new JSONObject(find("var player_aaaa=(.*?)</script>", html));
        JSONObject vodData = jsonObject.getJSONObject("vod_data");
        JSONObject result = new JSONObject();
        String dmid = new JSONObject(find("var d4ddy=(.*?)</script>", html)).get("dmid").toString();
        String link = jsonObject.get("link").toString();
        String nid = jsonObject.get("nid").toString();
        String link_next = jsonObject.get("link_next").toString();
        String url = decodeUnicodeEscape(jsonObject.get("url").toString());
        String sid = jsonObject.get("sid").toString();
        String name = decodeUnicodeEscape(vodData.get("vod_name").toString());
        String pic = vodData.get("vod_pic").toString();
        String MacPlayerConfig = jsonObject.get("from").toString();
        String finalurl="";
        if(MacPlayerConfig.equals("tudou")){
            finalurl="https://art2.v2player.top:8989/player/?t=td&url="+ url +"&dmid="+ dmid +"&next="+link_next+"&name="+name+"&sid="+sid+"&nid="+nid+"&cur="+link+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc&pic="+pic;
        }else if (MacPlayerConfig.equals("1080zyk")||MacPlayerConfig.equals("bfzym3u8")||MacPlayerConfig.equals("mp4")||MacPlayerConfig.equals("xigua")||MacPlayerConfig.equals("ffm3u8")||MacPlayerConfig.equals("lzm3u8")){
            finalurl="https://art.v2player.top:8989/player/?url="+url +"&dmid="+ dmid +"&next="+link_next+"&name="+name+"&nid="+nid+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc&pic="+pic;
        }else if(MacPlayerConfig.equals("mag")){
            finalurl="https://art2.v2player.top:8989/player/?t=bus&url="+ url +"&dmid="+ dmid +"&next="+link_next+"&name="+name+"&sid="+sid+"&nid="+nid+"&cur="+link+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc&pic="+pic ;
        }
        System.out.println(finalurl);
        html = req(finalurl, getHeader(siteUrl));
        String lasturl;String fainlurl1 = "";

        if(html.contains("playData")){
            lasturl = find("playData\\(\\'(.*?)\\'\\)", html);
            String[] parts = lasturl.split("\',\'");
            String data = parts[0];
            String ivHex = parts[1];
            String keyHex = "41424142454637373739393943434344";
            fainlurl1= decrypt(data, keyHex, ivHex);
        }else if(html.contains("Artplayer")){
            fainlurl1 = find("url: '(.*?)\\'", html).replace("https://m3u8xx.sgzm.net:2087/","");
        }
        fainlurl1=decodeUnicodeEscape(fainlurl1);
        result.put("parse", 0);
        result.put("header", "");
        result.put("playUrl", "");
        result.put("url", fainlurl1);
        return result.toString();
    }




    public static void main (String[]args){
        try {
            Dm Dm = new Dm();
//             测试 homeContent 方法
            String homeContent = Dm.homeContent(false);
            System.out.println("Home Content: " + homeContent);
//                JSONObject aa = new JSONObject(homeContent);
//                JSONArray vodData = aa.getJSONArray("class");
//                JSONObject aaaa = vodData.getJSONObject(1);
//            System.out.println(aaaa);
////             测试 categoryContent 方法
//                String categoryContent = Dm.categoryContent("1", "1", false, new HashMap<>());
//            System.out.println("Category Content: " + categoryContent);
////
////            // 测试 detailContent 方法
//            List<String> ids = new ArrayList<>();
//            ids.add("/anime/e43620b8ca3139cb59251ecd.html");
//            String detailContent = Dm.detailContent(ids);
////
//            System.out.println("Detail Content: " + detailContent);

//             测试 searchContent 方法
            String searchContent = Dm.searchContent("海", false);
            System.out.println("Search Content: " + searchContent);

////             测试 playerContent 方法
//                String playerContent = Dm.playerContent("flag", "https://www.56dm.cc/anime/9b90cfa776c0257c5b7371dd/play/3/1.html", new ArrayList<>());
//                System.out.println("Player Content: " + playerContent);
////            try {
            // 示例密文，假设是 Base64 编码的
//                String data = "2k/1N8ureGJjohfnfoT7tA==";  // 示例密钥的十六进制表示
//                String keyHex = "41424142454637373739393943434344";
//                // 示例 IV 的十六进制表示，需要是有效的十六进制字符串
//                String ivHex = "53fa93a903842b5185d8d97fca08019b";

//                String decryptedText = Dm.decrypt(data, keyHex, ivHex);
//                System.out.println("解密后的文本: " + decryptedText);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}


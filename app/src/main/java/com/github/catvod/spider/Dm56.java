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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    /**
     * 发起一个HTTP请求并返回响应结果
     * 此方法使用OkHttp客户端来发送请求，并接收服务器的响应
     *
     * @param request HTTP请求对象，包含请求URL、方法、头信息和请求体等
     * @return Response对象，包含服务器返回的状态码、头信息和响应体等
     * @throws Exception 如果在执行请求过程中发生错误，将抛出异常
     */
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
//        String typeName = doc.select(".cCBf_DABCcac__hcIdeE>p").text();
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
        // 第一页
        //https://www.56dm.cc/search/-------------.html?wd=
        // https://www.56dm.cc/search/**----------1---.html
        // 第二页
        // https://www.56dm.cc/search/**----------2---.html

        String html = "";


        String searchUrl;
        if ("1".equals(pg)) {
            searchUrl = siteUrl + "/search/-------------.html?wd=" + URLEncoder.encode(key);
        } else {
            searchUrl = siteUrl + "/search/" + URLEncoder.encode(key)+"----------"+pg+"---.html";
        };

        html = req(searchUrl, getHeader(siteUrl,cookie));
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("[class=cCBf_FAAEfbc clearfix] > li");
        JSONArray videos=new JSONArray(); ;
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

    // @Override
    // public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
    //     JSONObject result = new JSONObject();
    //     result.put("parse", 0);
    //     result.put("header", "");
    //     result.put("playUrl", "");
    //     result.put("url", id);
    //     return result.toString();
    // }
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String lastUrl = id;
        String html = req(lastUrl, getHeader(lastUrl));
        // 2. 定义正则表达式匹配目标变量
        String jsonStr = find("var player_aaaa=(.*?)</script>", html);
        JSONObject jsonObject = new JSONObject(jsonStr);
        String dmid=new JSONObject(find("var d4ddy=(.*?)</script>",html)).get("dmid").toString();
        String link = jsonObject.get("link").toString();
        String url = jsonObject.get("url").toString();
        String link_next=jsonObject.get("link_next").toString();
        String sid=jsonObject.get("sid").toString();
        String nid=jsonObject.get("nid").toString();
        JSONObject vodData = jsonObject.getJSONObject("vod_data");
        String name = vodData.get("vod_name").toString();
        String pic = vodData.get("vod_pic").toString();
        String secendurl="https://art2.v2player.top:8989/player/?t=bus&url="+url+"&dmid="+dmid+"&next="+link_next+"&name="+name+"&sid="+sid+"&nid="+nid+"&cur="+link+"&ph=https://as.cfhls.top/&h=https://www.56dm.cc"+"&pic"+pic;
        // 打印根级别的键值对
    //    System.out.println(secendurl);
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("header", "");
        result.put("playUrl", "");
        result.put("url", secendurl);
        return result.toString();
    }

}

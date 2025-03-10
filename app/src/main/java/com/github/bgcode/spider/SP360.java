package com.github.bgcode.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.bgcode.crawler.Spider;
import com.github.bgcode.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author zhixc
 * 360
 */
public class SP360 extends Spider {

    //private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:102.0) Gecko/20100101 Firefox/102.0";
    private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";
    //private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";

    // 包含要跳过的站点字符串
    private String skipSiteStr = "";

    private String req(String url, String referer) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", userAgent)
                .addHeader("Referer", referer)
                .build();
        OkHttpClient okHttpClient = OkHttp.client();
        Response response = okHttpClient.newCall(request).execute();
        if (response.body() == null) return "";
        String content = response.body().string();
        response.close();
        return content;
    }

    private String reqBusy(String url, String breakFlag) throws Exception {
        String result = "";
        for (int k = 0; k < 8; k++) {
            result = req(url, "https://api.web.360kan.com");
            if (result.contains(breakFlag)) break;
            Thread.sleep(500); // 休眠 500 毫秒，即 0.5 秒
        }
        return result;
    }

    private String jsonArray2String(JSONArray arr) throws Exception {
        if (arr == null) return "";
        return arr.toString().replace("\"", "").replace("[", "").replace("]", "");
    }

    /*private String getPic(String cdnCover) {
        int length = "qhimg.com".length();
        int midIndex = cdnCover.indexOf("qhimg.com") + length;
        String prefix = cdnCover.substring(0, midIndex);
        String suffix = cdnCover.substring(midIndex);
        return prefix + "/dmfd/224_314_75" + suffix;
    }*/

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        skipSiteStr = extend != null ? extend : "";
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("1", "2", "3", "4");
        List<String> typeNames = Arrays.asList("电影", "电视剧", "综艺", "动漫");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String f = "{\"1\": [{\"key\": \"class\", \"name\": \"类型\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"喜剧\", \"v\": \"喜剧\"}, {\"n\": \"爱情\", \"v\": \"爱情\"}, {\"n\": \"动作\", \"v\": \"动作\"}, {\"n\": \"恐怖\", \"v\": \"恐怖\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"剧情\", \"v\": \"剧情\"}, {\"n\": \"犯罪\", \"v\": \"犯罪\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"战争\", \"v\": \"战争\"}, {\"n\": \"悬疑\", \"v\": \"悬疑\"}, {\"n\": \"动画\", \"v\": \"动画\"}, {\"n\": \"文艺\", \"v\": \"文艺\"}, {\"n\": \"记录\", \"v\": \"记录\"}, {\"n\": \"传记\", \"v\": \"传记\"}, {\"n\": \"歌舞\", \"v\": \"歌舞\"}, {\"n\": \"古装\", \"v\": \"古装\"}, {\"n\": \"历史\", \"v\": \"历史\"}, {\"n\": \"惊悚\", \"v\": \"惊悚\"}, {\"n\": \"伦理\", \"v\": \"伦理\"}, {\"n\": \"其他\", \"v\": \"其他\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部年代\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"更早\", \"v\": \"更早\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部地区\", \"v\": \"\"}, {\"n\": \"内地\", \"v\": \"大陆\"}, {\"n\": \"中国香港\", \"v\": \"香港\"}, {\"n\": \"中国台湾\", \"v\": \"台湾\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"德国\", \"v\": \"德国\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"其他\", \"v\": \"其他\"}]}, {\"key\": \"actor\", \"name\": \"明星\", \"value\": [{\"n\": \"全部明星\", \"v\": \"\"}, {\"n\": \"成龙\", \"v\": \"成龙\"}, {\"n\": \"周星驰\", \"v\": \"周星驰\"}, {\"n\": \"李连杰\", \"v\": \"李连杰\"}, {\"n\": \"林正英\", \"v\": \"林正英\"}, {\"n\": \"吴京\", \"v\": \"吴京\"}, {\"n\": \"徐峥\", \"v\": \"徐峥\"}, {\"n\": \"黄渤\", \"v\": \"黄渤\"}, {\"n\": \"王宝强\", \"v\": \"王宝强\"}, {\"n\": \"李小龙\", \"v\": \"李小龙\"}, {\"n\": \"张国荣\", \"v\": \"张国荣\"}, {\"n\": \"洪金宝\", \"v\": \"洪金宝\"}, {\"n\": \"姜文\", \"v\": \"姜文\"}, {\"n\": \"沈腾\", \"v\": \"沈腾\"}, {\"n\": \"邓超\", \"v\": \"邓超\"}, {\"n\": \"巩俐\", \"v\": \"巩俐\"}, {\"n\": \"马丽\", \"v\": \"马丽\"}, {\"n\": \"闫妮\", \"v\": \"闫妮\"}, {\"n\": \"周冬雨\", \"v\": \"周冬雨\"}, {\"n\": \"刘昊然\", \"v\": \"刘昊然\"}, {\"n\": \"汤唯\", \"v\": \"汤唯\"}, {\"n\": \"舒淇\", \"v\": \"舒淇\"}, {\"n\": \"白百何\", \"v\": \"白百何\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"最近热映\", \"v\": \"rankhot\"}, {\"n\": \"最近上映\", \"v\": \"ranklatest\"}, {\"n\": \"最受好评\", \"v\": \"rankpoint\"}]}], \"2\": [{\"key\": \"class\", \"name\": \"类型\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"言情\", \"v\": \"言情\"}, {\"n\": \"剧情\", \"v\": \"剧情\"}, {\"n\": \"伦理\", \"v\": \"伦理\"}, {\"n\": \"喜剧\", \"v\": \"喜剧\"}, {\"n\": \"悬疑\", \"v\": \"悬疑\"}, {\"n\": \"都市\", \"v\": \"都市\"}, {\"n\": \"偶像\", \"v\": \"偶像\"}, {\"n\": \"古装\", \"v\": \"古装\"}, {\"n\": \"军事\", \"v\": \"军事\"}, {\"n\": \"警匪\", \"v\": \"警匪\"}, {\"n\": \"历史\", \"v\": \"历史\"}, {\"n\": \"励志\", \"v\": \"励志\"}, {\"n\": \"神话\", \"v\": \"神话\"}, {\"n\": \"谍战\", \"v\": \"谍战\"}, {\"n\": \"青春\", \"v\": \"青春\"}, {\"n\": \"家庭\", \"v\": \"家庭\"}, {\"n\": \"动作\", \"v\": \"动作\"}, {\"n\": \"情景\", \"v\": \"情景\"}, {\"n\": \"武侠\", \"v\": \"武侠\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"其他\", \"v\": \"其他\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部年代\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"更早\", \"v\": \"更早\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部地区\", \"v\": \"\"}, {\"n\": \"内地\", \"v\": \"内地\"}, {\"n\": \"中国香港\", \"v\": \"香港\"}, {\"n\": \"中国台湾\", \"v\": \"台湾\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"新加坡\", \"v\": \"新加坡\"}]}, {\"key\": \"actor\", \"name\": \"明星\", \"value\": [{\"n\": \"全部明星\", \"v\": \"\"}, {\"n\": \"杨幂\", \"v\": \"杨幂\"}, {\"n\": \"热巴\", \"v\": \"迪丽热巴\"}, {\"n\": \"张嘉译\", \"v\": \"张嘉译\"}, {\"n\": \"赵丽颖\", \"v\": \"赵丽颖\"}, {\"n\": \"赵又廷\", \"v\": \"赵又廷\"}, {\"n\": \"胡歌\", \"v\": \"胡歌\"}, {\"n\": \"孙俪\", \"v\": \"孙俪\"}, {\"n\": \"韩东君\", \"v\": \"韩东君\"}, {\"n\": \"周迅\", \"v\": \"周迅\"}, {\"n\": \"张一山\", \"v\": \"张一山\"}, {\"n\": \"李小璐\", \"v\": \"李小璐\"}, {\"n\": \"李沁\", \"v\": \"李沁\"}, {\"n\": \"陈坤\", \"v\": \"陈坤\"}, {\"n\": \"刘亦菲\", \"v\": \"刘亦菲\"}, {\"n\": \"唐嫣\", \"v\": \"唐嫣\"}, {\"n\": \"李小冉\", \"v\": \"李小冉\"}, {\"n\": \"周冬雨\", \"v\": \"周冬雨\"}, {\"n\": \"于和伟\", \"v\": \"于和伟\"}, {\"n\": \"李易峰\", \"v\": \"李易峰\"}, {\"n\": \"雷佳音\", \"v\": \"雷佳音\"}, {\"n\": \"何冰\", \"v\": \"何冰\"}, {\"n\": \"阮经天\", \"v\": \"阮经天\"}, {\"n\": \"梅婷\", \"v\": \"梅婷\"}, {\"n\": \"徐峥\", \"v\": \"徐峥\"}, {\"n\": \"祖峰\", \"v\": \"祖峰\"}, {\"n\": \"秦海璐\", \"v\": \"秦海璐\"}, {\"n\": \"杨紫\", \"v\": \"杨紫\"}, {\"n\": \"任嘉伦\", \"v\": \"任嘉伦\"}, {\"n\": \"贾乃亮\", \"v\": \"贾乃亮\"}, {\"n\": \"罗晋\", \"v\": \"罗晋\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"最近热映\", \"v\": \"rankhot\"}, {\"n\": \"最近上映\", \"v\": \"ranklatest\"}, {\"n\": \"最受好评\", \"v\": \"rankpoint\"}]}], \"3\": [{\"key\": \"class\", \"name\": \"类型\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"脱口秀\", \"v\": \"脱口秀\"}, {\"n\": \"真人秀\", \"v\": \"真人秀\"}, {\"n\": \"搞笑\", \"v\": \"搞笑\"}, {\"n\": \"选秀\", \"v\": \"选秀\"}, {\"n\": \"八卦\", \"v\": \"八卦\"}, {\"n\": \"访谈\", \"v\": \"访谈\"}, {\"n\": \"情感\", \"v\": \"情感\"}, {\"n\": \"生活\", \"v\": \"生活\"}, {\"n\": \"晚会\", \"v\": \"晚会\"}, {\"n\": \"音乐\", \"v\": \"音乐\"}, {\"n\": \"职场\", \"v\": \"职场\"}, {\"n\": \"美食\", \"v\": \"美食\"}, {\"n\": \"时尚\", \"v\": \"时尚\"}, {\"n\": \"游戏\", \"v\": \"游戏\"}, {\"n\": \"少儿\", \"v\": \"少儿\"}, {\"n\": \"体育\", \"v\": \"体育\"}, {\"n\": \"纪实\", \"v\": \"纪实\"}, {\"n\": \"科教\", \"v\": \"科教\"}, {\"n\": \"曲艺\", \"v\": \"曲艺\"}, {\"n\": \"歌舞\", \"v\": \"歌舞\"}, {\"n\": \"财经\", \"v\": \"财经\"}, {\"n\": \"汽车\", \"v\": \"汽车\"}, {\"n\": \"播报\", \"v\": \"播报\"}, {\"n\": \"其他\", \"v\": \"其他\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部地区\", \"v\": \"\"}, {\"n\": \"中国大陆\", \"v\": \"中国大陆\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"中国香港\", \"v\": \"中国香港\"}, {\"n\": \"中国台湾\", \"v\": \"中国台湾\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"欧洲\", \"v\": \"欧洲\"}, {\"n\": \"其他\", \"v\": \"泰国\"}]}, {\"key\": \"actor\", \"name\": \"明星\", \"value\": [{\"n\": \"全部明星\", \"v\": \"\"}, {\"n\": \"邓超\", \"v\": \"邓超\"}, {\"n\": \"陈赫\", \"v\": \"陈赫\"}, {\"n\": \"何炅\", \"v\": \"何炅\"}, {\"n\": \"汪涵\", \"v\": \"汪涵\"}, {\"n\": \"王俊凯\", \"v\": \"王俊凯\"}, {\"n\": \"黄磊\", \"v\": \"黄磊\"}, {\"n\": \"谢娜\", \"v\": \"谢娜\"}, {\"n\": \"黄渤\", \"v\": \"黄渤\"}, {\"n\": \"周杰伦\", \"v\": \"周杰伦\"}, {\"n\": \"薛之谦\", \"v\": \"薛之谦\"}, {\"n\": \"Angelababy\", \"v\": \"Angelababy\"}, {\"n\": \"易烊千玺\", \"v\": \"易烊千玺\"}, {\"n\": \"岳云鹏\", \"v\": \"岳云鹏\"}, {\"n\": \"王嘉尔\", \"v\": \"王嘉尔\"}, {\"n\": \"鹿晗\", \"v\": \"鹿晗\"}, {\"n\": \"杨幂\", \"v\": \"杨幂\"}, {\"n\": \"沈腾\", \"v\": \"沈腾\"}, {\"n\": \"张艺兴\", \"v\": \"张艺兴\"}, {\"n\": \"潘玮柏\", \"v\": \"潘玮柏\"}, {\"n\": \"华晨宇\", \"v\": \"华晨宇\"}, {\"n\": \"李维嘉\", \"v\": \"李维嘉\"}, {\"n\": \"宋小宝\", \"v\": \"宋小宝\"}, {\"n\": \"贾玲\", \"v\": \"贾玲\"}, {\"n\": \"沙溢\", \"v\": \"沙溢\"}, {\"n\": \"撒贝宁\", \"v\": \"撒贝宁\"}, {\"n\": \"涂磊\", \"v\": \"涂磊\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"最近热映\", \"v\": \"rankhot\"}, {\"n\": \"最近上映\", \"v\": \"ranklatest\"}]}], \"4\": [{\"key\": \"class\", \"name\": \"类型\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"热血\", \"v\": \"热血\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"美少女\", \"v\": \"美少女\"}, {\"n\": \"魔幻\", \"v\": \"魔幻\"}, {\"n\": \"经典\", \"v\": \"经典\"}, {\"n\": \"励志\", \"v\": \"励志\"}, {\"n\": \"少儿\", \"v\": \"少儿\"}, {\"n\": \"冒险\", \"v\": \"冒险\"}, {\"n\": \"搞笑\", \"v\": \"搞笑\"}, {\"n\": \"推理\", \"v\": \"推理\"}, {\"n\": \"恋爱\", \"v\": \"恋爱\"}, {\"n\": \"治愈\", \"v\": \"治愈\"}, {\"n\": \"幻想\", \"v\": \"幻想\"}, {\"n\": \"校园\", \"v\": \"校园\"}, {\"n\": \"动物\", \"v\": \"动物\"}, {\"n\": \"机战\", \"v\": \"机战\"}, {\"n\": \"亲子\", \"v\": \"亲子\"}, {\"n\": \"儿歌\", \"v\": \"儿歌\"}, {\"n\": \"运动\", \"v\": \"运动\"}, {\"n\": \"悬疑\", \"v\": \"悬疑\"}, {\"n\": \"怪物\", \"v\": \"怪物\"}, {\"n\": \"战争\", \"v\": \"战争\"}, {\"n\": \"益智\", \"v\": \"益智\"}, {\"n\": \"青春\", \"v\": \"青春\"}, {\"n\": \"童话\", \"v\": \"童话\"}, {\"n\": \"竞技\", \"v\": \"竞技\"}, {\"n\": \"动作\", \"v\": \"动作\"}, {\"n\": \"社会\", \"v\": \"社会\"}, {\"n\": \"友情\", \"v\": \"友情\"}, {\"n\": \"真人版\", \"v\": \"真人版\"}, {\"n\": \"电影版\", \"v\": \"电影版\"}, {\"n\": \"OVA版\", \"v\": \"OVA版\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部年代\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"2006\", \"v\": \"2006\"}, {\"n\": \"2005\", \"v\": \"2005\"}, {\"n\": \"2004\", \"v\": \"2004\"}, {\"n\": \"更早\", \"v\": \"更早\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部地区\", \"v\": \"\"}, {\"n\": \"内地\", \"v\": \"大陆\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"日本\", \"v\": \"日本\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"最近热映\", \"v\": \"rankhot\"}, {\"n\": \"最近上映\", \"v\": \"ranklatest\"}]}]}";
        JSONObject filterConfig = new JSONObject(f);
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterConfig);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        JSONArray videos = new JSONArray();
        String hotUrl = "https://api.web.360kan.com/v1/rank?cat=2&callback=";
        String referer = "https://www.360kan.com/rank/dianying";
        String html = req(hotUrl, referer);
        JSONArray data = new JSONObject(html).optJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            String id = item.optString("ent_id");
            String vid = "https://api.web.360kan.com/v1/detail?cat=1&id=" + id + "&callback=";
            String detailReferer = "https://www.360kan.com/m/" + id + ".html";
            JSONObject detailObj = new JSONObject().put("detailUrl", vid).put("detailReferer", detailReferer);
            String name = item.optString("title");
            String pic = item.optString("cover");
            String remark = jsonArray2String(item.optJSONArray("moviecat"));
            JSONObject vod = new JSONObject();
            vod.put("vod_id", detailObj.toString());
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
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        HashMap<String, String> ext = new HashMap<>();
        if (extend != null && extend.size() > 0) {
            ext.putAll(extend);
        }
        String by = ext.get("by") == null ? "ranklatest" : ext.get("by");
        String classType = ext.get("class") == null ? "" : ext.get("class");
        String year = ext.get("year") == null ? "" : ext.get("year");
        String area = ext.get("area") == null ? "" : ext.get("area");
        String actor = ext.get("actor") == null ? "" : ext.get("actor");
        StringBuilder sb = new StringBuilder()
                .append("rank=").append(by)
                .append("&cat=").append(URLEncoder.encode(classType))
                .append("&year=").append(year)
                .append("&area=").append(URLEncoder.encode(area))
                .append("&act=").append(URLEncoder.encode(actor));

        String detailClassId = "";
        String categoryClassId = "";
        switch (tid) {
            case "1": // 电影
                categoryClassId = "dianying";
                detailClassId = "m";
                break;
            case "2": // 电视剧
                categoryClassId = "dianshi";
                detailClassId = "tv";
                break;
            case "3": // 综艺
                categoryClassId = "zongyi";
                detailClassId = "va";
                break;
            case "4": // 动漫
                categoryClassId = "dongman";
                detailClassId = "ct";
        }
        // https://api.web.360kan.com/v1/filter/list?catid=1&rank=rankhot&cat=喜剧&year=&area=香港&act=周星驰&size=35&callback=
        // referer = https://www.360kan.com/dianying/list?rank=rankhot&cat=喜剧&year=&area=香港&act=周星驰
        // 第二页
        // https://api.web.360kan.com/v1/filter/list?catid=1&rank=rankhot&cat=喜剧&year=&area=香港&act=周星驰&size=35&pageno=2&callback=
        // referer = https://www.360kan.com/dianying/list?rank=rankhot&cat=喜剧&year=&area=香港&act=周星驰&pageno=2
        String cateUrl = "https://api.web.360kan.com/v1/filter/list?catid=" + tid + "&" + sb + "&size=35&callback=";
        String referer = "https://www.360kan.com/" + categoryClassId + "/list?" + sb;
        if (!pg.equals("1")) {
            // 第二页开始
            cateUrl = "https://api.web.360kan.com/v1/filter/list?catid=" + tid + "&" + sb + "&size=35&pageno=" + pg + "&callback=";
            referer = "https://www.360kan.com/" + categoryClassId + "/list?" + sb + "&pageno=" + pg;
        }
        JSONArray videos = new JSONArray();
        JSONArray items = new JSONObject(reqBusy(cateUrl, "\"msg\":\"ok\""))
                .optJSONObject("data")
                .optJSONArray("movies");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            // detailUrl = https://api.web.360kan.com/v1/detail?cat=1&id=gKTiahH7QXH4TB&callback=
            String id = item.optString("id");
            String vid = "https://api.web.360kan.com/v1/detail?cat=" + tid + "&id=" + id + "&callback=";
            String detailReferer = "https://www.360kan.com/" + detailClassId + "/" + id + ".html";
            JSONObject detailObj = new JSONObject().put("detailUrl", vid).put("detailReferer", detailReferer);
            String name = item.optString("title");
            if (name.contains("幕后记录") || name.contains("闭幕式") || name.contains("赏析")) {
                continue; // 跳过垃圾数据
            }
            String pic = "https:" + item.optString("cdncover");
            //String pic = "https:" + getPic(item.optString("cdncover"));
            //String remark = item.optString("pubdate");
            String remark = "";
            if (tid.equals("2") || tid.equals("4")) {
                // 电视剧或动漫
                String upInfo = item.optString("upinfo");
                String total = item.optString("total");
                remark = upInfo.equals(total) ? "已完结" : "更新至" + upInfo + "集";
            }
            if (tid.equals("3")) {
                // 综艺
                remark = "更新至" + item.optString("tag");
            }

            String vodId = detailObj.toString();
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }

        JSONObject result = new JSONObject();
        result.put("pagecount", 999);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject detailObj = new JSONObject(ids.get(0));
        String detailUrl = detailObj.optString("detailUrl");
        String detailReferer = detailObj.optString("detailReferer");
        String detailResult = reqBusy(detailUrl, "\"msg\":\"Success\"");
        JSONObject data = new JSONObject(detailResult).optJSONObject("data");
        String name = data.optString("title");

        boolean isTV = data.has("allepidetail"); // 电视或者动漫的标志
        boolean isVarietyShow = data.has("defaultepisode"); // 综艺类型标志

        Map<String, String> playMap = new LinkedHashMap<>();
        JSONObject playLinksDetail = data.optJSONObject("playlinksdetail");
        JSONArray playLinkSites = data.optJSONArray("playlink_sites");
        // subStr =  https://api.web.360kan.com/v1/detail?cat=3&id=Y8glbalv7Jc6DD
        String subStr = detailUrl.substring(0, detailUrl.indexOf("&callback="));
        if (isTV) {
            // 进入这里说明是电视剧或者动漫
            // 有很多集
            // 需要针对一些还没有查询出来的数据做查询，获取其他站源的数据
            for (int i = 0; i < playLinkSites.length(); i++) {
                String site = String.valueOf(playLinkSites.get(i));
                if (skipSiteStr.contains(site)) continue;
                String upInfoUrl = subStr + "&site=" + site + "&callback=";
                String upInfoResult = reqBusy(upInfoUrl, "\"msg\":\"Success\"");
                JSONObject allUpInfo = new JSONObject(upInfoResult)
                        .optJSONObject("data")
                        .optJSONObject("allupinfo");
                int upInfo = 0;
                if (allUpInfo.has(site)) {
                    upInfo = Integer.parseInt(String.valueOf(allUpInfo.get(site)));
                }
                List<String> vodItems = new ArrayList<>();
                for (int j = 0; j < upInfo; j += 200) {
                    int end = j + 200;
                    if (end > upInfo) {
                        end = upInfo;
                    }
                    int start = j + 1;
                    String episodeDetailUrl = subStr + "&start=" + start + "&end=" + end + "&site=" + site + "&callback=";
                    String episodeResult = reqBusy(episodeDetailUrl, "\"msg\":\"Success\"");
                    JSONArray episodes2 = new JSONObject(episodeResult)
                            .optJSONObject("data")
                            .optJSONObject("allepidetail")
                            .optJSONArray(site);
                    if (episodes2 == null) continue;
                    for (int k = 0; k < episodes2.length(); k++) {
                        JSONObject item = episodes2.optJSONObject(k);
                        String episodeName = "第" + item.optString("playlink_num") + "集";
                        String episodeUrl = item.optString("url");
                        vodItems.add(episodeName + "$" + episodeUrl);
                    }
                }
                if (vodItems.size() > 0) {
                    playMap.put(site, TextUtils.join("#", vodItems));
                }
            }
        } else if (isVarietyShow) {
            // 进入这里说明是综艺类型
            // 遍历 playLinkSites 发起请求获取
            for (int i = 0; i < playLinkSites.length(); i++) {
                String site = String.valueOf(playLinkSites.get(i));
                if (skipSiteStr.contains(site)) continue;
                if (data.has("tag")) {
                    // 有 tag 标签 ，说明有按年份区分该综艺节目
                    Iterator<String> keys = data.optJSONObject("tag").keys();
                    List<String> vodItems = new ArrayList<>();
                    while (keys.hasNext()) {
                        String year = keys.next();
                        String episodeDetailUrl = subStr + "&site=" + site + "&year=" + year + "&callback=";
                        String episodeResult = reqBusy(episodeDetailUrl, "\"msg\":\"Success\"");
                        JSONArray episodes = new JSONObject(episodeResult)
                                .optJSONObject("data")
                                .optJSONArray("defaultepisode");
                        if (episodes == null) continue;
                        for (int j = 0; j < episodes.length(); j++) {
                            JSONObject item = episodes.optJSONObject(j);
                            String episodeName = item.optString("period") + " " + item.optString("name");
                            String episodeUrl = item.optString("url");
                            vodItems.add(episodeName + "$" + episodeUrl);
                        }
                    }
                    if (vodItems.size() > 0) {
                        playMap.put(site, TextUtils.join("#", vodItems));
                    }
                }
            }
        } else {
            // 电影的默认处理
            for (int i = 0; i < playLinkSites.length(); i++) {
                String site = String.valueOf(playLinkSites.get(i));
                if (skipSiteStr.contains(site)) continue;
                String episodeUrl = playLinksDetail.optJSONObject(site).optString("default_url");
                List<String> vodItems = new ArrayList<>();
                vodItems.add("正片" + "$" + episodeUrl);
                playMap.put(site, TextUtils.join("#", vodItems));
            }
        }

        String pic = data.optString("cdncover");
        String typeName = jsonArray2String(data.optJSONArray("moviecategory"));
        String year = data.optString("pubdate");
        String area = jsonArray2String(data.optJSONArray("area"));
        String actor = jsonArray2String(data.optJSONArray("actor"));
        String director = jsonArray2String(data.optJSONArray("director"));
        String description = data.optString("description");

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name); // 影片名称
        vod.put("vod_pic", pic); // 图片
        vod.put("type_name", typeName); // 影片类型 选填
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", area);// 地区 选填
        vod.put("vod_remarks", ""); // 备注 选填
        vod.put("vod_actor", actor);// 主演 选填
        vod.put("vod_director", director); // 导演 选填
        vod.put("vod_content", description); // 简介 选填
        if (playMap.size() > 0) {
            vod.put("vod_play_from", TextUtils.join("$$$", playMap.keySet()));
            vod.put("vod_play_url", TextUtils.join("$$$", playMap.values()));
        }

        JSONArray array = new JSONArray().put(vod);
        JSONObject result = new JSONObject().put("list", array);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String keyword = URLEncoder.encode(key);
        String searchUrl = "https://api.so.360kan.com/index?force_v=1&kw=" + keyword + "&from=&pageno=1&v_ap=1&tab=all";
        String referer = "https://so.360kan.com/?kw=" + keyword;
        String rs = req(searchUrl, referer);
        JSONArray videos = new JSONArray();
        JSONArray rows = new JSONObject(rs)
                .optJSONObject("data")
                .optJSONObject("longData")
                .optJSONArray("rows");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject item = rows.optJSONObject(i);
            String id = item.optString("en_id");
            String tid = item.optString("cat_id");
            String vid = "https://api.web.360kan.com/v1/detail?cat=" + tid + "&id=" + id + "&callback=";
            String detailReferer = item.optString("url");
            JSONObject detailObj = new JSONObject().put("detailUrl", vid).put("detailReferer", detailReferer);
            String name = item.optString("titleTxt");
            String pic = item.optString("cover");
            String remark = item.optString("cat_name");

            String vodId = detailObj.toString();
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
        HashMap<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        JSONObject result = new JSONObject()
                .put("parse", 1) // 需要嗅探
                .put("jx", 1) // 需要调用解析接口进行解析
                .put("header", header.toString())
                .put("playUrl", "")
                .put("url", id);
        return result.toString();
    }
}

package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import org.json.*;
import java.util.*;

public class Gqzy extends Spider {

    private   String req(String url, Map<String, String> header) {
        return OkHttp.string(url, header);
    }
    String url="https://yzzy.tv";
    String ua="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36";
    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", ua);
        header.put("HOST", "yzzy.tv");
        return header;
    }
    @Override
    public String homeContent(boolean filter) throws Exception {
        String data=req(url+"/inc/apijson.php?ac=detail",getHeader());
        JSONArray veidos=new JSONArray(new JSONObject(data).getJSONArray("list"));
        JSONArray videos=new JSONArray();
        for(int i=0;i<veidos.length();i++){
            JSONObject vod = new JSONObject();
            JSONObject  item = veidos.getJSONObject(i);
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remark").toString());
            videos.put(vod);
        }

        String f="[{\"type_id\":5,\"type_name\":\"动作片\"},{\"type_id\":6,\"type_name\":\"喜剧片\"},{\"type_id\":7,\"type_name\":\"爱情片\"},{\"type_id\":8,\"type_name\":\"科幻片\"},{\"type_id\":9,\"type_name\":\"恐怖片\"},{\"type_id\":10,\"type_name\":\"剧情片\"},{\"type_id\":11,\"type_name\":\"战争片\"},{\"type_id\":12,\"type_name\":\"国产剧\"},{\"type_id\":13,\"type_name\":\"台湾剧\"},{\"type_id\":14,\"type_name\":\"韩国剧\"},{\"type_id\":15,\"type_name\":\"欧美剧\"},{\"type_id\":16,\"type_name\":\"香港剧\"},{\"type_id\":17,\"type_name\":\"泰国剧\"},{\"type_id\":18,\"type_name\":\"日本剧\"},{\"type_id\":19,\"type_name\":\"福利\"},{\"type_id\":20,\"type_name\":\"记录片\"},{\"type_id\":41,\"type_name\":\"动画片\"},{\"type_id\":54,\"type_name\":\"海外剧\"},{\"type_id\":61,\"type_name\":\"倫理片\"},{\"type_id\":62,\"type_name\":\"大陆综艺\"},{\"type_id\":63,\"type_name\":\"港台综艺\"},{\"type_id\":64,\"type_name\":\"日韩综艺\"},{\"type_id\":65,\"type_name\":\"欧美综艺\"},{\"type_id\":66,\"type_name\":\"国产动漫\"},{\"type_id\":67,\"type_name\":\"日韩动漫\"},{\"type_id\":68,\"type_name\":\"欧美动漫\"},{\"type_id\":69,\"type_name\":\"港台动漫\"},{\"type_id\":70,\"type_name\":\"海外动漫\"},{\"type_id\":78,\"type_name\":\"搞笑\"},{\"type_id\":79,\"type_name\":\"音乐\"},{\"type_id\":80,\"type_name\":\"影视\"},{\"type_id\":81,\"type_name\":\"汽车\"},{\"type_id\":83,\"type_name\":\"短剧大全\"},{\"type_id\":92,\"type_name\":\"预告片\"},{\"type_id\":93,\"type_name\":\"预告片\"},{\"type_id\":94,\"type_name\":\"体育\"}]";
        JSONArray classes = new JSONArray(f);
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("list", videos);
        return result.toString();
    }
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl=url+"/inc/apijson.php?ac=detail&pg"+pg+"&t="+tid;
        JSONArray list = new JSONObject(req(cateUrl,getHeader())).getJSONArray("list");
        JSONArray videos = new JSONArray();
        for (int i = 0; i < list.length(); i++) {
            JSONObject vod = new JSONObject();
            JSONObject  item = list.getJSONObject(i);
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remark").toString());
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("page", Integer.parseInt(pg));
        result.put("pagecount", 999);
        result.put("limit", list.length());
        result.put("total", Integer.MAX_VALUE);
        result.put("list", videos);
        return result.toString();
    }
    public String detailContent(List<String> ids) throws Exception {
        String data=req(url+"/inc/apijson.php?ac=detail&ids="+ids.get(0),getHeader());
        JSONObject list=new JSONObject(new JSONObject(data).getJSONArray("list").get(0).toString());
        JSONObject vod = new JSONObject();
        vod.put("vod_id", list.get("vod_id").toString());
        vod.put("vod_name", list.get("vod_name").toString()); // 影片名称
        vod.put("vod_year", list.get("vod_year").toString()); // 年份 选填
        vod.put("vod_area", list.get("vod_area").toString()); // 地区 选填
        vod.put("vod_remarks", list.get("vod_remark").toString()); // 备注 选填
        vod.put("vod_content", list.get("vod_content").toString()); // 简介 选填
        vod.put("vod_play_from", list.get("vod_play_from").toString());
        vod.put("vod_play_url", list.get("vod_play_url").toString());
        JSONArray jsonArray = new JSONArray().put(vod);
        JSONObject result = new JSONObject().put("list", jsonArray);
        return result.toString();
    }
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        result.put("parse", 1);
        result.put("header", "");
        result.put("playUrl", "");
        result.put("url", id);
        return result.toString();
    }
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String data=req(url+"/inc/apijson.php?ac=detail&pg"+pg+"&wd="+key,getHeader());
        JSONArray res=  new JSONObject(data).getJSONArray("list");
        JSONArray videos=new JSONArray();
        for(int i=0;i<res.length();i++){
            JSONObject item = res.getJSONObject(i);
            JSONObject vod = new JSONObject();
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remark").toString());
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

//    public static void main(String[] args) throws Exception {
//       Gqzy gqzy = new Gqzy();
//       System.out.println(gqzy.homeContent(true));
//       System.out.println(gqzy.categoryContent("6","2",true,null));
//       System.out.println(gqzy.detailContent(new ArrayList<String>() {{ add("22"); }}));
//       System.out.println(gqzy.searchContent("海", false, "2"));
//   }
}

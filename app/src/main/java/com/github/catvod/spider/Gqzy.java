package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.getData;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gqzy extends Spider {
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
        String data=getData.req(url+"/inc/apijson.php?ac=list",getHeader());
        JSONArray classes=new JSONArray(new JSONObject(data).getJSONArray("class"));
        for(int i = classes.length() - 1; i >= 0; i--){
            JSONObject obj=classes.getJSONObject(i);
            String type_id=obj.getString("type_id");
            if(type_id.equals("1")||type_id.equals("2")||type_id.equals("3")||type_id.equals("4")){
               classes.remove(i);
            }
            obj.put("type_id", Integer.parseInt(type_id));
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        return result.toString();
    }
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl=url+"/inc/apijson.php?ac=detail&pg"+pg+"&t="+tid;
        JSONArray list = new JSONObject(getData.req(cateUrl,getHeader())).getJSONArray("list");
        JSONArray videos = new JSONArray();
        for (int i = 0; i < list.length(); i++) {
            JSONObject vod = new JSONObject();
            JSONObject  item = list.getJSONObject(i);
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remarks").toString());
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
        String data=getData.req(url+"/inc/apijson.php?ac=detail&ids="+ids.get(0),getHeader());
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
        String data=getData.req(url+"/inc/apijson.php?ac=detail&pg"+pg+"&wd="+key,getHeader());
        JSONArray res=  new JSONObject(data).getJSONArray("list");
        JSONArray videos=new JSONArray();
        for(int i=0;i<res.length();i++){
            JSONObject item = res.getJSONObject(i);
            JSONObject vod = new JSONObject();
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remarks").toString());
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

//    public static void main(String[] args) throws Exception {
//       Gqzy gqzy = new Gqzy();
//       System.out.println(gqzy.homeContent(true));
//   }
}

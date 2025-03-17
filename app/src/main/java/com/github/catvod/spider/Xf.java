package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Xf extends Spider {
    private String url="https://pzoap.moedot.net";
    private String UA="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36";
    private String req(String url, Map<String, String> headers) throws Exception {
        return OkHttp.string(url, headers);
    }
    private Map<String, String> getheader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", UA);
        header.put("referer", url);
        return header;
    }
    @Override
    public String homeContent(boolean filter) throws Exception {
        // String site=url+"/xgapp.php/v2/video?pg=1&tid=1&class=&area=&lang=&year=";
        // JSONArray list = new JSONObject(req(site,getheader())).getJSONArray("data");
        // JSONArray videos = new JSONArray();
        // for (int i = 0; i < list.length(); i++) {
        //     JSONObject vod = new JSONObject();
        //     JSONObject  item = list.getJSONObject(i);
        //     vod.put("vod_id", item.get("vod_id").toString());
        //     vod.put("vod_name", item.get("vod_name").toString());
        //     vod.put("vod_pic", item.get("vod_pic").toString());
        //     vod.put("vod_remarks", item.get("vod_remarks").toString());
        //     videos.put(vod);
        // }
        String f="[{\"type_name\":\"连载新番\",\"type_id\":1},{\"type_name\":\"完结旧番\",\"type_id\":2},{\"type_name\":\"剧场版\",\"type_id\":3},{\"type_name\":\"美漫区\",\"type_id\":21},{\"type_name\":\"贤者专区\",\"type_id\":22}]";
        JSONArray classes = new JSONArray(f);
        JSONObject result = new JSONObject();
        result.put("class", classes);
        // result.put("list", videos);
        return result.toString();
    }
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl = url+"/xgapp.php/v2/video?pg="+pg+"&tid="+tid+"&class=&area=&lang=&year=";
        JSONArray list = new JSONObject(req(cateUrl,getheader())).getJSONArray("data");
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
        String igd = ids.get(0);
        String site = url + "/xgapp.php/v2/video_detail?id="+igd;
        JSONObject list = new JSONObject(req(site,getheader())).getJSONObject("data").getJSONObject("vod_info");
        JSONArray videos = list.getJSONArray("vod_url_with_player");
        String vod_play_from = "", vod_play_url="";
        for (int i = 0; i < videos.length(); i++) {
            JSONObject video = videos.getJSONObject(i);
            if(i==list.length()-1){
                vod_play_from+=video.get("name").toString();
                vod_play_url+=video.get("url").toString();
            }else {
                vod_play_from+=video.get("name").toString()+"$$$";
                vod_play_url+=video.get("url").toString()+"$$$";
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", list.get("vod_name").toString()); // 影片名称
        vod.put("vod_year", list.get("vod_year").toString()); // 年份 选填
        vod.put("vod_area", list.get("vod_area").toString()); // 地区 选填
        vod.put("vod_remarks", list.get("vod_remarks").toString()); // 备注 选填
        vod.put("vod_content", list.get("vod_content").toString()); // 简介 选填
        vod.put("vod_play_from", vod_play_from);
        vod.put("vod_play_url", vod_play_url);
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


    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String data=req(url+"/xgapp.php/v2/search?pg="+pg+"&text="+key,getheader());
        JSONArray res=  new JSONObject(data).getJSONArray("data");
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

}

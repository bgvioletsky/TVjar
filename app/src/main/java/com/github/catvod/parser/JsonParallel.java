/*
 * @Author: catvod
 * @Date: 2025-03-10 12:25:31
 * @LastEditTime: 2025-03-11 02:40:51
 * @LastEditors: catvod
 * @Description: 描述
 * @FilePath: /catvod/app/src/main/java/com/github/catvod/parser/JsonParallel.java
 * 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
 */
package com.github.catvod.parser;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Misc;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Request;

/**
 * 并发解析，直到获得第一个结果
 * <p>
 * 默认解析超时时间为15秒，如果需要请自定义SpiderReq的HttpClient
 * <p>
 * Author: CatVod
 */
public class JsonParallel {
    private static final String ParseOKTag = "p_json_parse";

    public static JSONObject parse(LinkedHashMap<String, String> jx, String url) {
        try {
            if (jx.size() > 0) {
                ExecutorService executorService = Executors.newFixedThreadPool(3);
                CompletionService<JSONObject> completionService = new ExecutorCompletionService<JSONObject>(executorService);
                List<Future<JSONObject>> futures = new ArrayList<>();
                Set<String> jxNames = jx.keySet();
                for (String jxName : jxNames) {
                    String parseUrl = jx.get(jxName);
                    futures.add(completionService.submit(new Callable<JSONObject>() {
                        @Override
                        public JSONObject call() throws Exception {
                            try {
                                HashMap<String, String> reqHeaders = JsonBasic.getReqHeader(parseUrl);
                                String realUrl = reqHeaders.get("url");
                                reqHeaders.remove("url");
                                SpiderDebug.log(realUrl + url);
                                Request.Builder builder = new Request.Builder().get().url(realUrl + url);
                                for (String key : reqHeaders.keySet()) builder.addHeader(key, reqHeaders.get(key));
                                builder.tag(ParseOKTag);
                                String json = OkHttp.client().newCall(builder.build()).execute().body().string();
                                JSONObject taskResult = Misc.jsonParse(url, json);
                                taskResult.put("jxFrom", jxName);
                                SpiderDebug.log(taskResult.toString());
                                return taskResult;
                            } catch (Throwable th) {
                                SpiderDebug.log(th);
                                return null;
                            }
                        }
                    }));
                }
                JSONObject pTaskResult = null;
                for (int i = 0; i < futures.size(); ++i) {
                    Future<JSONObject> completed = completionService.take();
                    try {
                        pTaskResult = completed.get();
                        if (pTaskResult != null) {
                            OkHttp.cancel(ParseOKTag);
                            for (int j = 0; j < futures.size(); j++) {
                                try {
                                    futures.get(j).cancel(true);
                                } catch (Throwable th) {
                                    SpiderDebug.log(th);
                                }
                            }
                            futures.clear();
                            break;
                        }
                    } catch (Throwable th) {
                        SpiderDebug.log(th);
                    }
                }
                try {
                    executorService.shutdownNow();
                } catch (Throwable th) {
                    SpiderDebug.log(th);
                }
                if (pTaskResult != null)
                    return pTaskResult;
            }
        } catch (Throwable th) {
            SpiderDebug.log(th);
        }
        return new JSONObject();
    }
}

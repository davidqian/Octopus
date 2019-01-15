package com.my.Octopus.net.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送HTTP请求工具类
 *
 * @author davidqian
 */
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    static PoolingClientConnectionManager connectionManager = null;
    static HttpClient httpclient = null;

    static {
        connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(10);
        httpclient = new DefaultHttpClient(connectionManager);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        httpclient.getParams().setParameter("http.connection-manager.timeout", 600000L);
    }

    public static String doGet(String url) {

        HttpGet httpGet = new HttpGet(url);

        String resStr = "";
        try {
            HttpResponse response1 = httpclient.execute(httpGet);
            HttpEntity entity = response1.getEntity();
            resStr = EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.error("HttpClientUtil doGet error:", e);
        } finally {
            if (httpGet != null) {
                httpGet.abort();
            }
        }

        return resStr;
    }

    public static String doPost(String url, List<NameValuePair> nvps) {

        String resStr = "";
        HttpPost httpPost = new HttpPost(url);
        try {
            if (nvps == null || nvps.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            }
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            resStr = EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("HttpClientUtil doPost error:", e);
        } finally {
            if (httpPost != null) {
                httpPost.abort();
            }
        }
        return resStr;
    }
}

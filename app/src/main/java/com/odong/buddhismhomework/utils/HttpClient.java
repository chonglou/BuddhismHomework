package com.odong.buddhismhomework.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Created by flamen on 15-3-3.
 */
public class HttpClient {
    private HttpClient(){}

    public static String get(String url) throws IOException {
        return call(getClient(false), new HttpGet(url));
    }
    public static String getS(String url) throws IOException,URISyntaxException{
       return call(getClient(false), new HttpGet(new URI(url)));
    }

    private static org.apache.http.client.HttpClient getClient(boolean ssl){
        if(ssl){
            SchemeRegistry sr = new SchemeRegistry();
            sr.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            HttpParams hp = new BasicHttpParams();
            SingleClientConnManager cm = new SingleClientConnManager(hp, sr);
            return new DefaultHttpClient(cm, hp);
        }
        else {
            return new DefaultHttpClient();
        }
    }

    private static String call(org.apache.http.client.HttpClient client, HttpUriRequest request) throws IOException {


        HttpResponse hr = client.execute(request);
        InputStream is = hr.getEntity().getContent();
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        return null;
    }
}

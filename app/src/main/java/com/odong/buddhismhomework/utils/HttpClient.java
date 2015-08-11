package com.odong.buddhismhomework.utils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by flamen on 15-3-3.
 */
public class HttpClient {
    private HttpClient() {
    }

    public static String get(String url) throws IOException, URISyntaxException {
        //return call(new HttpGet(new URI(url)));
        return url;
    }

//    private static String call(HttpUriRequest request) throws IOException {
//
//        org.apache.http.client.HttpClient client = new DefaultHttpClient();
//        HttpResponse hr = client.execute(request);
//        InputStream is = hr.getEntity().getContent();
//        if (is != null) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            return sb.toString();
//        }
//        return null;
//    }
}

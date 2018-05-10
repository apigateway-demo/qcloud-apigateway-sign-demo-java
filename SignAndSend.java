package apigatewayDemo;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

public class SignAndSend {
    private static final String CONTENT_CHARSET = "UTF-8";
    private static final String HMAC_ALGORITHM = "HmacSHA1"; 
    public static String sign(String secret, String timeStr) 
    		throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException 
    {
        //get signStr
        String signStr = "date: "+timeStr+"\n"+"source: "+"source";
        //get sig
        String sig = null;
        Mac mac1 = Mac.getInstance(HMAC_ALGORITHM);
        byte[] hash;
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac1.getAlgorithm());
        mac1.init(secretKey);
        hash = mac1.doFinal(signStr.getBytes(CONTENT_CHARSET));
        sig = new String(Base64.encode(hash));
        System.out.println("signValue--->" + sig);
        return sig;
    }
    public static String sendGet(String url, String secretId, String secretKey) {
        String result = "";
        BufferedReader in = null;
    	//get current GMT time
        Calendar cd = Calendar.getInstance();  
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);  
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); 
        String timeStr = sdf.format(cd.getTime());  
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            HttpURLConnection httpUrlCon = (HttpURLConnection)connection;
            // 设置通用的请求属性
            httpUrlCon.setRequestProperty("Host", url);
            httpUrlCon.setRequestProperty("Accept", "text/html, */*; q=0.01");
            httpUrlCon.setRequestProperty("Source","source");
            httpUrlCon.setRequestProperty("Date",timeStr);
            String sig = sign(secretKey,timeStr);
            String authen = "hmac id=\""+secretId+"\", algorithm=\"hmac-sha1\", headers=\"date source\", signature=\""+sig+"\"";
            System.out.println("authen --->" + authen);
            httpUrlCon.setRequestProperty("Authorization",authen);
            httpUrlCon.setRequestProperty("X-Requested-With","XMLHttpRequest");
            httpUrlCon.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
            // 建立实际的连接
            httpUrlCon.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = httpUrlCon.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
            		httpUrlCon.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
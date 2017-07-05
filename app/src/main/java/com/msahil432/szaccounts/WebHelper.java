package com.msahil432.szaccounts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * It will handle post and get requests of http
 *
 */

public class WebHelper {

    private static WebHelper helper;
    private String response;

    private WebHelper(){}

    public static WebHelper instance(){
        if(helper==null){
            helper = new WebHelper();
        }
        return helper;
    }

    public static boolean isNetAvailable(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int lookupHost(String hostname) {
        InetAddress inetAddress;

        try {
            inetAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return -1;
        }

        byte[] addrBytes;
        int addr;
        addrBytes = inetAddress.getAddress();
        addr = ((addrBytes[3] & 0xff) << 24)
                | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8)
                | (addrBytes[0] & 0xff);

        return addr;
    }


    public String getJson(String url) throws Exception {
        response="";

        response = fetchJson(url);

        if(response.contains("doneSahil")){
            response = response.replace("doneSahil", "");
        }else{
            throw new Exception(response);
        }

        return response;
    }

    public String postJson(String url, JSONObject jsonObject) throws Exception {
        response = "";

        response = postToServer(url, jsonObject.toString());

        if(response.contains("doneSahil")){
            response = response.replace("doneSahil", "");
        }else{
            throw new Exception(response);
        }

        return response;
    }

    private String fetchJson(String url){
        String result = "";
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("GET");
            connection.setDoInput(true); //Sets the flag indicating whether this {@code URLConnection} allows input
            connection.setDoOutput(true);
            connection.connect();

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while ((line = bufferedReader.readLine()) != null)
                stringBuffer.append(line + "\r\n");

            inputStream.close();
            connection.disconnect();
            bufferedReader.close();

            result = "doneSahil" + stringBuffer.toString();
        } catch (Exception e) {
            result = "Exception occured: "+e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    private String postToServer(String urlString, String JsonObject) throws Exception {

        String result ="";

        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("content-type", "application/json");
        httpURLConnection.setDoInput(true); //Sets the flag indicating whether this {@code URLConnection} allows input
        httpURLConnection.setDoOutput(true);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());

        outputStreamWriter.write(JsonObject);
        outputStreamWriter.flush();

        StringBuilder stringBuilder = new StringBuilder();
        int res = httpURLConnection.getResponseCode();
        if (res == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            result = "doneSahil" + stringBuilder.toString();

            br.close();
            httpURLConnection.disconnect();
            outputStreamWriter.close();

        } else {
            throw new Exception(httpURLConnection.getResponseMessage());
        }
        return result;
    }

}
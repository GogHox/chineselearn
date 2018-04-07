package chineselearn.goghox.com.global;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by GogHox on 2018/4/3.
 */

public class Server {
    private static OkHttpClient httpClient = new OkHttpClient();
    public static String ip = "192.168.157.128";
    public static Integer port = 8000;
    public static String host = "http://" + Server.ip + ":" + Server.port;

    public static String run(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    public static ArrayList<String> getSingleWord(){
        ArrayList<String> wordBeans = new ArrayList<>();
        try {
            String string = run(host + "/get_single_word");
            JSONArray ja = new JSONArray(string);
            for (int i = 0; i < ja.length(); i++){
                wordBeans.add(ja.getJSONObject(i).getString("word"));
            }
            return wordBeans;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getDoubleWord(){
        ArrayList<String> wordBeans = new ArrayList<>();
        try {
            String string = run(host + "/get_double_word");
            JSONArray ja = new JSONArray(string);
            for (int i = 0; i < ja.length(); i++){
                wordBeans.add(ja.getJSONObject(i).getString("word"));
            }
            return wordBeans;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getMoreWord(){
        ArrayList<String> wordBeans = new ArrayList<>();
        try {
            String string = run(host + "/get_phrase");
            JSONArray ja = new JSONArray(string);
            for (int i = 0; i < ja.length(); i++){
                wordBeans.add(ja.getJSONObject(i).getString("word"));
            }
            return wordBeans;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

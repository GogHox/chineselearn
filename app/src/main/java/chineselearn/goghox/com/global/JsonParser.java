package chineselearn.goghox.com.global;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by GogHox on 2018/3/10.
 */

public class JsonParser {
    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append(obj.getString("w"));
                    //ret.append("【置信度】" + obj.getInt("sc"));
                    //ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }
    public static ArrayList<String> parseGrammarResultArray(String json) {
        //StringBuffer ret = new StringBuffer();
        ArrayList<String> ret = new ArrayList<>();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        //ret.append("没有匹配结果.");
                        return null;
                    }
                    //ret.append(obj.getString("w"));
                    ret.add(obj.getString("w"));
                    //ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //ret.append("没有匹配结果.");
        }
        return ret;
        //return ret.toString();
    }
}

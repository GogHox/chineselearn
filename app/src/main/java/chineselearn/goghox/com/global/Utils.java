package chineselearn.goghox.com.global;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by GogHox on 2018/3/9.
 */

public class Utils {
    public static int getScreenWidthDp(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) (displayMetrics.density * displayMetrics.widthPixels);
    }
}

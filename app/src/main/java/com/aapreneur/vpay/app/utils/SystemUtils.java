package com.aapreneur.vpay.app.utils;

/**
 * Created by anmol on 04-04-2018.
 */
import android.content.res.Resources;
public class SystemUtils {
    public static int getScreenOrientation() {
        return Resources.getSystem().getConfiguration().orientation;
    }
}

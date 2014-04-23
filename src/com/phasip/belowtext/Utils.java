package com.phasip.belowtext;

/**
 * Created with IntelliJ IDEA.
 * User: phasip
 * Date: 2/5/14
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static String join(String[] arr, String separator, int start) {
        if (arr.length <= start)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            String s = arr[i];
            sb.append(s);
            sb.append(",");
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }
    public static String join(String[] arr, String separator) {
        return join(arr,separator,0);
    }
}

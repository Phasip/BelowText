package com.phasip.belowtext;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: phasip
 * Date: 2/4/14
 * Time: 8:31 PM
 *
 * Helper functions to do HTTP calls.
 */
public class MyHttpClient {
    private String USER_AGENT = "OS Test User Agent";

    /**
     * Download a file that is compressed with gzip and save it extracted.
     * @param urlString URL to download from
     * @param outfile  File to download to.
     * @return
     */
    public static boolean downloadGunzip(String urlString,File outfile) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
            GZIPInputStream in = new GZIPInputStream(con.getInputStream());
            FileOutputStream out = new FileOutputStream(outfile);

            int i = 0;
            byte[] bytesIn = new byte[1024*5];
            while ((i = in.read(bytesIn)) >= 0) {
                out.write(bytesIn, 0, i);
            }
            out.close();
            in.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }
}

package com.phasip.belowtext;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: phasip
 * Date: 2/4/14
 * Time: 8:44 PM
 *
 * Implements the calls used by this application for OpenSubtitles.
 * Uses the aXMLRPC library https://github.com/timroes/aXMLRPC, compiled and
 * tested with v1.7.2
 *
 */
public class OpenSubtitleAPI {
    private String USER_AGENT = "BelowText";
    private XMLRPCClient client;
    private String token;

    public OpenSubtitleAPI() {
        URL url = null;
        try {
            //Had problems with new URL("http://api.opensubtitles.org/xml-rpc");
            url = new URL("http", "api.opensubtitles.org", 80, "/xml-rpc");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot connect to api!",e);
        }
        client = new XMLRPCClient(url,USER_AGENT);
        login();
    }

    public Map<String,Object> searchMap(String[] languages,String hash, Long size, String tag,String query ) {
        HashMap hm = new HashMap<String,Object>();

        hm.put("sublanguageid",Utils.join(languages, ","));
        if (hash != null)
            hm.put("moviehash",hash);
        if (size != null)
            hm.put("moviebytesize",size.toString());
        if (tag != null)
            hm.put("tag",tag);
        if (query != null)
            hm.put("query",query);

        return hm;
    }

    public boolean login() {
        String user = "";
        String pass = "";
        String language = "en";
        String useragent = USER_AGENT;
        try {
            Map ret = (Map)client.call("LogIn",user,pass,language,useragent);
            if (Settings.getSettings().printDebug()) {
                System.err.println("Login returns: " + ret);
            }
            if ("414 Unknown User Agent".equals(ret.get("status"))) {
                throw new RuntimeException("Did opensubtitles ban our useragent?, Something is wrong!");
            }
            token = (String)ret.get("token");
        } catch (XMLRPCException e) {
            throw new RuntimeException("Cannot parse login data",e);
        }
        return true;
    }
    public boolean logout() {
        boolean isSuccess = true;
        try {
            client.call("LogOut",token);
        } catch (XMLRPCException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        token = null;
        return isSuccess;
    }

    public Map<String,Object>[] search(String[] languages, String hash, long size, String filename, int limit) {
        Map<String,Object> s1 = searchMap(languages,hash,size,null,null);
        Map<String,Object> s2 = searchMap(languages,null,null,filename,null);
        Map<String,Object> limitMap = new HashMap<String, Object>();
        limitMap.put("limit", new Integer(limit));
        try {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(s1);
            list.add(s2);

            Map o = (Map)client.call("SearchSubtitles",token,list,limitMap);
            Object data = o.get("data");
            if (data instanceof Object[]) {
                Object[] obj = (Object[])data;
                Map<String,Object>[] ret = new Map[obj.length];
                for (int i = 0; i < obj.length; i++) {
                    Map<String,Object> o1 = (Map<String,Object>)obj[i];
                    ret[i] = o1;
                }
                return ret;

            }
            return null;
        } catch (XMLRPCException e) {
            e.printStackTrace();
            return null;
        }
    }



}

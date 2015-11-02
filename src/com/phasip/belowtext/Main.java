package com.phasip.belowtext;

import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: phasip
 * Date: 2/4/14
 * Time: 8:14 PM
 *
 * Very simple program that downloads subtitles from opensubtitles based on the hash or filename of a movie.
 * It does not care much for errors since almost all errors are fatal anyway.
 */
public class Main {
    public Settings settings;
    public static void main(String[] args) throws IOException {
        new Main().run(args);

    }
    public void run(String[] args) throws IOException {
        try {
            settings = Settings.loadSettings(args);
        } catch (Settings.BadParametersException e) {
            System.err.println(e.getMessage());
            System.err.println();
            System.err.println("Usage: java -jar BelowText.jar [-d] [-c result_limit] [-l lang] movie file name");
            System.err.println("-c result_limit\tDefines the max number of results to retrieve (default: 3)");
            System.err.println("-d print debug stuff");
            System.err.println("-s do not download if subtitles exists");
            //System.err.println("-s\t\tUse console output instead of gui");
            System.err.println("-l language\tDefine language to download subtitle for, \\can be defined multiple times for multiple languages. (default: eng)");
            System.exit(-1);
        }

        //TODO: Add language parameters and number of subtitles limit. Maybe also search method selections.
        String file = settings.getMoviefile();
        File f = new File(file);
        if (!f.exists())
        {
            System.err.println("File does not exist!");
            System.exit(-2);
        }
        if (f.isDirectory()) {
            System.err.println("File specified is folder!");
            System.exit(-3);
        }

        String hash = OpenSubtitlesHasher.computeHash(f);
        long size = f.length();
        String filename = f.getName();
        String[] languages = settings.getLanguages();
        if ( settings.shouldSkipIfSubtitlesExist() && genSubFile(f,0).exists()) {
            System.err.println("Subtitle exists already, skipping.");
            System.exit(-4);
        }
        OpenSubtitleAPI api = new OpenSubtitleAPI();
        Map<String,Object> results[] = api.search(languages,hash,size,filename,settings.getCount());
        if (results != null)
            handleResult(results,f);
        else
            System.err.println("Could not find any subtitles!");

        api.logout();
    }

    public void handleResult(Map<String,Object> results[],File movieFile) {
        for (int i = 0; i < results.length; i++) {
            Map<String, Object> result = results[i];
            String link = (String)result.get("SubDownloadLink");

            File outFile = genSubFile(movieFile);
            if (outFile == null) {
                System.err.println("Fail to generate next subtitle name!");
                break;
            }
            System.out.println("Downloading Subtitles to " + outFile);
            if (!MyHttpClient.downloadGunzip(link,outFile)) {
                outFile.delete();
            }
        }
    }
    private File genSubFile(File f,int idx) {
        String str = f.getName();
        int last = str.lastIndexOf(".");
        String newExt = ".srt";
        String fileStart = str;

        if (last != -1)
            fileStart = str.substring(0,last);

        File newFile = null;
        if (idx < 1) {
            newFile = new File(f.getParent(), fileStart + newExt);
        } else {
            newFile = new File(f.getParent(), fileStart + "." + idx + newExt);
        }
        return newFile;
    }

    /**
     * Creates a file that will be automatically found by VLC
     * If a file with that name exists it creates files by the format moviename.1.srt where
     * moviename is the name of f without it's extension.
     * @param f Movie to generate a subtitle file for.
     * @return A created file to store subtitles in or null if we fail.
     */
    private File genSubFile(File f) {
        try {
            File newFile;
            for (int i = 0; i < 1000; i++) {
                newFile = genSubFile(f,i);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                    return newFile;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


}

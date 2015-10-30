package com.phasip.belowtext;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: phasip
 * Date: 2/5/14
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Settings {
    public static String COUNT_FLAG = "-c";
    public static String LANGUAGE_FLAG = "-l";
    public static String NO_FLAGS = "--";
    public static String DEBUG_FLAG = "-d";
    private boolean debug = false;
    private String[] languages;
    private int count = 3;
    private String moviefile = null;
    private static Settings singleton;

    public boolean printDebug() {
        return debug;
    }

    public static class BadParametersException extends Exception {
        public BadParametersException(String message) {
            super(message);
        }
    }

    public static Settings loadSettings(String[] args) throws BadParametersException{
        if (singleton == null)
            singleton = new Settings();
        singleton.loadSettingsFromArgs(args);
        return singleton;
    }
    public static Settings getSettings() {
        if (singleton == null)
            throw new RuntimeException("Cannot call getSettings before loadSettings (newb!)");
        return singleton;
    }

    private void loadSettingsFromArgs(String[] args) throws BadParametersException {
        //Don't add dependency on parser
        int i = 0;
        ArrayList<String> languages = new ArrayList<String>();
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            boolean hasNext = (i == args.length-1);
            if (arg.equals(COUNT_FLAG)) {
                if (!hasNext) throw new BadParametersException("Count flag without parameter");
                String param = args[i+1];
                try {
                    count = Integer.parseInt(param);
                } catch ( NumberFormatException e) {
                    throw new BadParametersException("Count parameter not a number!");
                }
            } else if (arg.equals(LANGUAGE_FLAG)) {
                if (!hasNext) throw new BadParametersException("Language flag without parameter");
                String param = args[i+1];
                languages.add(param);
            } else if (arg.equals(DEBUG_FLAG)) {
                debug = true;
            } else if (arg.equals(NO_FLAGS)) {
                i++;
                break;
            } else {
                break;
            }
        }
        if (i == args.length)
            throw new BadParametersException("No Movie Name!");
        if (languages.size() == 0)
            languages.add("eng");

        this.languages = languages.toArray(new String[]{});
        moviefile = Utils.join(args," ",i);
    }

    public String[] getLanguages() {
        return languages;
    }


    public int getCount() {
        return count;
    }

    public String getMoviefile() {
        return moviefile;
    }
}

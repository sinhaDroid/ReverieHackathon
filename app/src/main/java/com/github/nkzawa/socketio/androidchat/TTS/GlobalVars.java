package com.github.nkzawa.socketio.androidchat.TTS;

import java.util.ArrayList;

public class GlobalVars {
    public static String BASE_REQ_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    public static ArrayList<String> LANGUAGE_CODES = new ArrayList<String>();
    public static int DEFAULT_LANG_POS = 16;

    public static ArrayList<LanguageModel> LANGUAGE_MODEL = new ArrayList<>();

    public GlobalVars() {
    }

    public static void initializeCodes() {
        if (LANGUAGE_MODEL.isEmpty()) {
            LANGUAGE_MODEL.add(new LanguageModel("English", "en", "en"));
            LANGUAGE_MODEL.add(new LanguageModel("Hindi", "hi", "hi"));
            LANGUAGE_MODEL.add(new LanguageModel("Bengali", "bn", "bn-IN"));
            LANGUAGE_MODEL.add(new LanguageModel("Kannada", "kn", "kn-IN"));
            LANGUAGE_MODEL.add(new LanguageModel("Malayalam", "ml", "ml-IN"));
            LANGUAGE_MODEL.add(new LanguageModel("Marathi", "mr", "mr-IN"));
            LANGUAGE_MODEL.add(new LanguageModel("Tamil", "ta", "ta-IN"));
            // LANGUAGE_MODEL.add(new LanguageModel("Punjabi", "pa", "pa-IN"));
        }
    }
}

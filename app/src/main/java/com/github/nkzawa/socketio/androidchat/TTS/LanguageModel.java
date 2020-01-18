package com.github.nkzawa.socketio.androidchat.TTS;

public class LanguageModel {
    public String name;
    public String code;
    public String localCode;

    public LanguageModel(String name, String code, String localCode){
        this.name = name;
        this.code = code;
        this.localCode = localCode;
    }

    @Override
    public String toString() {
        return name;
    }
}

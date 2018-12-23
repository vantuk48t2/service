package com.example.music;


public class Constants {
    public interface ACTION{
        public static String MAIN_ACTION ="com.example.music.action.main";
        public static String PREV_ACTION ="com.example.music.action.prev";
        public static String PLAY_ACTION ="com.example.music.action.play";
        public static String NEXT_ACTION="com.example.music.action.next";
        public static String STARTFOREGROUND_ACTION  ="com.example.music.action.startforeground";
        public static String STOPFOREGROUND_ACTION  ="com.example.music.action.stopforeground";
    }
    public interface NOTIFICATION_ID{
        public static int FOREGROUND_SERVICE = 123;
    }
}

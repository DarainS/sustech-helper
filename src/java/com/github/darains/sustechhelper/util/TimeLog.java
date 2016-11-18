package com.github.darains.sustechhelper.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeLog{

    static SimpleDateFormat formatter ;
    public static String timeInfo() {
        return timeInfo("MM-dd HH:mm:ss");
    }
    public static String timeInfo(String format){
        Date date = new Date();
        formatter= new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return "["+dateString+"] ";
    }

}

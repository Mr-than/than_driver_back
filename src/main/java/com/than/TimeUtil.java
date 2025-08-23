package com.than;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Than
 * @package: com.than.time
 * @className: TimeUtil
 * @description: 时间工具
 * @date: 2023/8/23 18:11
 */
public class TimeUtil {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ");

    /**
     * <p>获得目前时间</p>
     * @return 目前格式化之后的时间
     */
    public static String getThisTime(){
        Date now = new Date();
        return simpleDateFormat.format(now);
    }

    /**
     * <p>获得时间戳</p>
     * @return 当前时间戳
     */
    public static long getTime(){
        return System.currentTimeMillis();
    }

    public static Date getOffsetData(int day){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, day);
        return instance.getTime();
    }
}

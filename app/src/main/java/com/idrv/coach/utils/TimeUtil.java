package com.idrv.coach.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * time: 6/12/15
 * description: 时间、日期的基本工具类
 *
 * @author tangsong
 */
public class TimeUtil {
    /**
     * 返回日期: 20141112
     *
     * @return
     */
    public static long toDate(long millis) {
        Date currentTime = new Date();
        currentTime.setTime(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return Long.parseLong(formatter.format(currentTime));
    }

    public static long minuteToMillis(int minute) {
        return minute * 60 * 1000;
    }


    /**
     * 一分钟的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * 一月的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;

    /**
     * 一年的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    /**
     * 根据时间段毫秒数获取书面描述
     *
     * @param timePassed 时间段 单位(ms)
     * @return XX秒 XX分
     */
    public static String refreshUpdatedAtValue(long timePassed) {
        StringBuffer updateAtValue = new StringBuffer();
        if (timePassed < ONE_MINUTE) {
            updateAtValue.append("刚刚");
//            updateAtValue.append(String.valueOf(timePassed / 1000)).append("秒前");
        } else if (timePassed < ONE_HOUR) {
            updateAtValue.append(String.valueOf(timePassed / ONE_MINUTE)).append("分钟前");
        } else if (timePassed < ONE_DAY) {
            updateAtValue.append(String.valueOf(timePassed / ONE_HOUR)).append("小时前");
        } else if (timePassed < ONE_MONTH) {
            updateAtValue.append(String.valueOf(timePassed / ONE_DAY)).append("天前");
        } else if (timePassed < ONE_YEAR) {
            updateAtValue.append(String.valueOf(timePassed / ONE_MONTH)).append("月前");
        } else {
            updateAtValue.append(String.valueOf(timePassed / ONE_YEAR)).append("年前");
        }
        return updateAtValue.toString();
    }

    public static String getChineseTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(new Date(time));
    }

    public static String getSubscribeTime(long time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String formatTime = format.format(new Date(time));
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            if (formatTime.startsWith(String.valueOf(year))) {
                return formatTime.substring(5);
            } else {
                return formatTime;
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String getLastMessageTime(long targetTime) {
        String result;
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(targetTime);

        Calendar currentCalendar = Calendar.getInstance();
        Calendar targetCalendar = Calendar.getInstance();

        currentCalendar.setTime(today);
        targetCalendar.setTime(otherDay);

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int targetMonth = targetCalendar.get(Calendar.MONTH);

        if (currentYear != targetYear) {
            //如果不是同一年
            return getSimpleTime(targetTime);
        }

        int temp = currentCalendar.get(Calendar.DAY_OF_MONTH)
                - targetCalendar.get(Calendar.DAY_OF_MONTH);

        switch (temp) {
            case 0:
                result = getHourAndMin(targetTime);
                break;
            case 1:
                result = "昨天" + getHourAndMin(targetTime);
                break;
            default:
                result = getChinaDate(targetTime);
                break;
        }
        return result;
    }

    public static String getFeedsTime(long targetTime) {
        return getFeedsTime(targetTime, System.currentTimeMillis());
    }

    public static String getFeedsTime(long targetTime, long currentTime) {
        String result = "";
        Date today = new Date(currentTime);
        Date otherDay = new Date(targetTime);

        Calendar currentCalendar = Calendar.getInstance();
        Calendar targetCalendar = Calendar.getInstance();

        currentCalendar.setTime(today);
        targetCalendar.setTime(otherDay);

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int targetMonth = targetCalendar.get(Calendar.MONTH);

        if (currentYear != targetYear || currentMonth != targetMonth) {
            //如果不是同一年,或者同一月
            return getChinaDate(targetTime);
        }

        int temp = currentCalendar.get(Calendar.DAY_OF_MONTH)
                - targetCalendar.get(Calendar.DAY_OF_MONTH);

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(targetTime);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(targetTime);
                break;

            default:
                // result = temp + "天前 ";
                result = getChinaDate(targetTime);
                break;
        }

        return result;
    }

    public static long getTempTime(String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(time);
            long tempTime = date.getTime();
            return tempTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    public static String getHmsTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("y/M/d");
        return format.format(new Date(time));
    }

    public static String getChinaDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getChinaDate(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = format.parse(time);
            return getChinaDate(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getLongTime(String millisTime) {
        return !TextUtils.isEmpty(millisTime) ? (long) (Double.valueOf(millisTime) * 1000) : System.currentTimeMillis();
    }

    public static boolean isValidDate() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours > 7) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(String time) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time1 = sdf.format(date);
        if (TextUtils.isEmpty(time)) {
            return false;
        } else if (time.equals(time1)) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(String time1, String time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return sdf.format(date1).equals(sdf.format(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSimpleTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getCurrentTimeStr() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getSimpleTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        return format.format(new Date(time));
    }

    public static boolean showMessageTime(String currentTime, String prevTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date currentDate = sdf.parse(currentTime);
            Date prevDate = sdf.parse(prevTime);

            if (currentDate.getTime() - prevDate.getTime() >= 300000) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getMonth(String time) {
        int month = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            month = calendar.get(Calendar.MONTH) + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return month;
    }

    public static String getMonth(String time, String[] months) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(time);
            Calendar targetCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();

            targetCalendar.setTime(date);
            currentCalendar.setTime(new Date());

            int month = targetCalendar.get(Calendar.MONTH);

            int targetYear = targetCalendar.get(Calendar.YEAR);
            int currentYear = currentCalendar.get(Calendar.YEAR);

            boolean isSameYear = targetYear == currentYear;
            String result = isSameYear ? months[month] : targetYear + "年" + months[month];
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return months[0];
    }

    /**
     * 获取倒计时时间
     *
     * @return
     */
    public static long getDownTime() {
        // 获取当前时间
        Date nowTime = new Date();
        // 获取8点的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long result = calendar.getTimeInMillis() - nowTime.getTime();
        if (result > 0)
            return result;
        else
            return result + 86400000;
    }

    /**
     * 年份计算年龄
     *
     * @param year
     * @return
     */
    public static String getAge(String year) {
        if (!TextUtils.isEmpty(year)) {
            Calendar calendar = Calendar.getInstance();
            int result = calendar.get(Calendar.YEAR) - Integer.parseInt(year.substring(0, 4));
            result++;
            // 第一次进来，这里的年龄会很大，超过了产品的预期，故特殊处理下
            if (result > 47) {
                return "未设置";
            }
            return result + "年";
        } else {
            return "";
        }
    }

    /**
     * 比较目标时间与当前时间的间隔天数是否对大于期望值
     *
     * @param time
     * @return
     */
    public static boolean compareDate(String time, int targetDays) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date targetDate = simpleDateFormat.parse(time);
            String currentTimeStr = simpleDateFormat.format(new Date(System.currentTimeMillis()));
            Date currentDate = simpleDateFormat.parse(currentTimeStr);

            long targetTime = targetDate.getTime();
            long currentTime = currentDate.getTime();

            return (currentTime - targetTime) >= targetDays * 24 * 60 * 60 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断目标时间是否在当前时间之后.
     *
     * @param time
     * @return
     */
    public static boolean compareDate(String time) {
        if (TextUtils.isEmpty(time)) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date targetDate = simpleDateFormat.parse(time);
            long targetTime = targetDate.getTime();

            return targetTime >= System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int compareDate(String date1, String date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getTimestamp(String targetTime, long currentTime) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(targetTime));
        long time1 = cal.getTimeInMillis();
        long between_days = (time1 - currentTime) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

}

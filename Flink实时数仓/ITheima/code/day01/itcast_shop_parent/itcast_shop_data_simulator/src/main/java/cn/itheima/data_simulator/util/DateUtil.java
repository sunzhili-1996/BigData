package cn.itheima.data_simulator.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date StringToDate(String orgFormat,String dateString){
        SimpleDateFormat sdf= null;
        Date date = null;
        try{
            sdf= new SimpleDateFormat(orgFormat);
            date = sdf.parse(dateString);
        }catch(ParseException e){
            System.out.println(e.getMessage());
        }
        return date;

    }

    public static String dateToString(Date date,String format){
        SimpleDateFormat sdf= null;
        String dateString = null;
        sdf= new SimpleDateFormat(format);
        dateString = sdf.format(date);
        return dateString;
    }

    public static String timeToDateString(Long dateTime,String format){
        Date date = new Date(dateTime);

        SimpleDateFormat sdf= null;
        String dateString = null;
        sdf= new SimpleDateFormat(format);
        dateString = sdf.format(date);
        return dateString;
    }

    public static Long DateStringToTime(String orgFormat,String dateString){
        Date date = StringToDate(orgFormat,dateString);
        return date.getTime();
    }


    public static String dateFormat(String orgFormat,String dateString,String format){
        Date date = StringToDate(orgFormat,dateString);
        String dateS = dateToString(date,format);
        return dateS;
    }

    /**
     * 获得当月1号零时零分零秒
     * @return
     */
    public static Date initDateByMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        System.out.println(dateToString(initDateByMonth(), "yyyy-MM-dd HH:mm:ss"));
    }
}

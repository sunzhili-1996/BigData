package cn.itheima.data_simulator.util;


import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDifference {

    public static Date randomDate(String beginDate, String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if(start.getTime() >= end.getTime()){
                return null;
            }
            long date = random(start.getTime(),end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static long random(long begin,long end){
        long rtn = begin + (long)(Math.random() * (end - begin));
        if(rtn == begin || rtn == end){
            return random(begin,end);
        }
        return rtn;
    }
    /***
     * @comments 测试使用
     * @param args
     */
    public static void main(String[] args) {
        TimeDifference td = new TimeDifference();
        String startDate = "2020-03-01 00:00:00";
        String finishDate = "2020-03-30 00:00:00";

        System.out.println(randomDate(startDate,finishDate ));

        Date date = randomDate(startDate,finishDate);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }
}
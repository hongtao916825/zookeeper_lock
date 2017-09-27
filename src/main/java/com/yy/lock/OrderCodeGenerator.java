package com.yy.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luyuanyuan on 2017/9/22.
 */
public class OrderCodeGenerator {

    // 自增长序列
    private static int i = 0;

   public String getOrderCode(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return simpleDateFormat.format(date) + ++i;
   }

    public static void main(String[] args) {

        OrderCodeGenerator orderCodeGenerator = new OrderCodeGenerator();
        int a = 10;

        while (a-->0){
            System.out.println(orderCodeGenerator.getOrderCode());
        }

    }

}

package com.itheima.canal_demo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.itheima.protobuf.DemoModel;

/**
 * potobuf的使用方式
 * 使用potobuf进行数据的序列化和反序列化
 */
public class ProtobufDemo {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        //实例化protobuf的对象
        DemoModel.User.Builder builder  = DemoModel.User.newBuilder();
        //给user对象进行赋值
        builder.setId(1);   //用户id
        builder.setName("张三");//用户名
        builder.setSex("男");//用户性别

        //获取User对象的属性
        DemoModel.User userBuilder = builder.build();
        System.out.println(userBuilder.getId());
        System.out.println(userBuilder.getName());
        System.out.println(userBuilder.getSex());

        /**
         * 数据的序列化和反序列化
         * 序列化：意味着可以将对象转换成字节码数据存储到kafka中
         * 反序列：意味着可以将kafka中的数据消费出来以后，反序列化成对象使用
         */
        //将一个对象序列换成二进制的字节码数据存储到kafka
        byte[] bytes = builder.build().toByteArray();
        for (byte aByte : bytes) {
            System.out.println(aByte);
        }

        //从kafka中心消费出来的序列化数据，可以反序列化成对象使用
        DemoModel.User user = DemoModel.User.parseFrom(bytes);
        System.out.println(user.getId());
        System.out.println(user.getName());
        System.out.println(user.getSex());
    }
}

package com.zhoufa.test;

import com.zhoufa.test.entity.Soap;

/**
 * @author zhoufa
 */
public class Demo {

    public static void main(String[] args) {
        System.out.println("这个是我在Mac pro上的第一个输出内容");
        Soap soap = new Soap();
        System.out.println(soap);
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
        }
    }
}

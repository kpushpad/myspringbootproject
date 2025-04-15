package com.kpushpad.springboot.kvstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class KvStoreBootApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(KvStoreBootApplication.class,
                args);
        // System.out.println(ctx);
    }
}

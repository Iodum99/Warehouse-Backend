package com.example.warehouse;

import com.example.warehouse.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WarehouseApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(WarehouseApplication.class, args);
        UserService userService = ctx.getBean(UserService.class);
        userService.initialize();
    }

}

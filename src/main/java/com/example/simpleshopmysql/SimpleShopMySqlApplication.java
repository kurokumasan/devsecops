package com.example.simpleshopmysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
//@EnableJdbcHttpSession
public class SimpleShopMySqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleShopMySqlApplication.class, args);
    }

}

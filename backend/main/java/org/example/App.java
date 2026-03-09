package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Hello world!
 *
 */
@ServletComponentScan//开启
@SpringBootApplication
public class App
{
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}

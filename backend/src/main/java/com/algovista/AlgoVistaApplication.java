package com.algovista;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.algovista.**.mapper")
public class AlgoVistaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlgoVistaApplication.class, args);
    }
}

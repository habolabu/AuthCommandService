package edu.ou.authcommandservice;

import edu.ou.coreservice.annotation.BaseCommandAnnotation;
import org.springframework.boot.SpringApplication;

@BaseCommandAnnotation
public class AuthCommandServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthCommandServiceApplication.class, args);
    }

}

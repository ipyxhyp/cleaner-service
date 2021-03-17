package org.ptr.robot.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication
public class CleanerRobotApplication extends SpringBootServletInitializer {


    // for launching with bootRun
    public static void main(String[] args) {
        try {
            SpringApplication.run(CleanerRobotApplication.class, args);
        } catch (Exception e) {
            log.error("CountryTripsApplication error", e);
            System.exit(1);
        }
    }
}

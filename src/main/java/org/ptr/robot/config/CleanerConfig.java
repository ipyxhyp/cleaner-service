package org.ptr.robot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "org.ptr.robot.service", "org.ptr.robot.model"
})
public class CleanerConfig {

}

package com.example.fitlens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.example.fitlens.config.AdminUserProperties;
import com.example.fitlens.config.OAuthProperties;
import com.example.fitlens.config.AiProperties;
import com.example.fitlens.config.UploadProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({
        AdminUserProperties.class,
        OAuthProperties.class,
        AiProperties.class,
        UploadProperties.class
})
public class FitLensApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitLensApplication.class, args);
    }

}

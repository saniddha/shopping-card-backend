package au.com.informedia.mailconnector.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ConfigConst configConst;

    public WebConfig(ConfigConst configConst) {
        this.configConst = configConst;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedOrigins(configConst.getCorsUrl())
                .allowedHeaders("*");
    }
}

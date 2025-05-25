package dk.dma.baleen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward root to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        // Forward specific Angular routes to index.html
        registry.addViewController("/home").setViewName("forward:/index.html");
        registry.addViewController("/subscribers").setViewName("forward:/index.html");
        registry.addViewController("/logging").setViewName("forward:/index.html");
        registry.addViewController("/niord").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static resources from the browser subdirectory
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/browser/");
    }
}
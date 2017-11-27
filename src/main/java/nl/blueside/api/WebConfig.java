package nl.blueside.sp_api;

//import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@ComponentScan("nl.blueside.sp_api")
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter
{

    public WebConfig() {
        super();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry)
    {
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/sp/**");
    }

}

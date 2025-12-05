package hoang.shop.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class I18nConfig {

    @Bean
    MessageSource messageSource() {
        var ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames("classpath:messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        ms.setUseCodeAsDefaultMessage(true); // thiếu key -> in chính key
        return ms;
    }

    @Bean
    LocalValidatorFactoryBean getValidator(MessageSource ms) {
        var v = new LocalValidatorFactoryBean();
        v.setValidationMessageSource(ms);
        return v;
    }
}

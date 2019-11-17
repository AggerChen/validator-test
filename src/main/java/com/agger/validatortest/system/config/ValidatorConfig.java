package com.agger.validatortest.system.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @classname: ValidatorConfig
 * @description: 参数验证框架配置
 * @author chenhx
 * @date 2019-11-17 16:03
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public Validator Validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // true开启快速校验，判断到有一个校验不通过就返回
                .failFast(false)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}

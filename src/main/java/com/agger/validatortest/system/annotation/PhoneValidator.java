package com.agger.validatortest.system.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @classname: PhoneValidator
 * @description: 自定义注解：phone字段格式验证
 * @author chenhx
 * @date 2019-11-17 17:00
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD,PARAMETER})
//指定真正执行校验规则的类
@Constraint(validatedBy = PhoneValidatotClass.class)
public @interface PhoneValidator {
    String message()  default "手机号码格式不正确";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}

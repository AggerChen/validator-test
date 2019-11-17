package com.agger.validatortest.system.annotation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @classname: PhoneValidationValidator
 * @description: 手机号码规则验证器
 * @author chenhx
 * @date 2019-11-17 17:11
 */
public class PhoneValidatotClass implements ConstraintValidator<PhoneValidator, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtils.isBlank(value)){
            return false;
        }
        Matcher m = PHONE_PATTERN.matcher(value);
        return m.matches();
    }
}

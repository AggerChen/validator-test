package com.agger.validatortest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @classname: ValidatorTestApplication
 * @description: 示例项目没有链接数据库所以先排除数据库自动配置
 * @author chenhx
 * @date 2019-11-17 21:49
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ValidatorTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidatorTestApplication.class, args);
    }

}

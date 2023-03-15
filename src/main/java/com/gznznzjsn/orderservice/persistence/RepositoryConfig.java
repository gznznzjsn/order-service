package com.gznznzjsn.orderservice.persistence;

import com.gznznzjsn.orderservice.persistence.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RepositoryConfig {

    @Bean
    public R2dbcCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new OrderReadConverter());
        converters.add(new OrderWriteConverter());
        converters.add(new AssignmentReadConverter());
        converters.add(new AssignmentWriteConverter());
        converters.add(new TaskReadConverter());
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, converters);
    }

}

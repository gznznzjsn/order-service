package com.gznznzjsn.orderservice.persistence.converter;

import com.gznznzjsn.orderservice.domain.Task;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class TaskReadConverter implements Converter<Row, Task> {

    @Override
    public Task convert(Row source) {
        return Task.builder()
                .id(source.get("task_id", Long.class))
                .build();
    }

}
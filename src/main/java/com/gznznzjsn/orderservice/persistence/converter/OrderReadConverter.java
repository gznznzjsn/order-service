package com.gznznzjsn.orderservice.persistence.converter;

import com.gznznzjsn.orderservice.domain.Order;
import com.gznznzjsn.orderservice.domain.OrderStatus;
import com.gznznzjsn.orderservice.domain.User;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;

@ReadingConverter
public class OrderReadConverter implements Converter<Row, Order> {

    @Override
    public Order convert(Row source) {
        return Order.builder()
                .id(source.get("order_id", Long.class))
                .status(OrderStatus.valueOf( source.get("status", String.class)))
                .user(User.builder()
                        .id(source.get("user_id", Long.class))
                        .build()
                )
                .arrivalTime(source.get("arrival_time", LocalDateTime.class))
                .createdAt(source.get("created_at", LocalDateTime.class))
                .finishedAt(source.get("finished_at", LocalDateTime.class))
                .build();
    }

}
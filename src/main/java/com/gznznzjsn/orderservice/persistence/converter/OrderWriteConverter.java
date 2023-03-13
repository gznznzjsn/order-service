package com.gznznzjsn.orderservice.persistence.converter;

import com.gznznzjsn.orderservice.domain.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

@WritingConverter
public class OrderWriteConverter implements Converter<Order, OutboundRow> {

    @Override
    public OutboundRow convert(Order order) {
        OutboundRow row = new OutboundRow();
        if (order.getStatus() != null) {
            row.put("status", Parameter.from(order.getStatus()));
        }
        if (order.getUser() != null && order.getUser().getId() != null) {
            row.put("user_id", Parameter.from(order.getUser().getId()));
        }
        if (order.getArrivalTime() != null) {
            row.put("arrival_time", Parameter.from(order.getArrivalTime()));
        }
        if (order.getCreatedAt() != null) {
            row.put("created_at", Parameter.from(order.getCreatedAt()));
        }
        if (order.getFinishedAt() != null) {
            row.put("finished_at", Parameter.from(order.getFinishedAt()));
        }
        return row;
    }

}
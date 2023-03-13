package com.gznznzjsn.orderservice.persistence.converter;

import com.gznznzjsn.orderservice.domain.Assignment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

@WritingConverter
public class AssignmentWriteConverter implements Converter<Assignment, OutboundRow> {

    @Override
    public OutboundRow convert(Assignment assignment) {
        OutboundRow row = new OutboundRow();
        if (assignment.getOrder() != null && assignment.getOrder().getId() != null) {
            row.put("order_id", Parameter.from(assignment.getOrder().getId()));
        }
        if (assignment.getSpecialization() != null) {
            row.put("specialization", Parameter.from(assignment.getSpecialization()));
        }
        if (assignment.getEmployee() != null && assignment.getEmployee().getId() != null) {
            row.put("employee_id", Parameter.from(assignment.getEmployee().getId()));
        }
        if (assignment.getStatus() != null) {
            row.put("status", Parameter.from(assignment.getStatus()));
        }
        if (assignment.getStartTime() != null) {
            row.put("start_time", Parameter.from(assignment.getStartTime()));
        }
        if (assignment.getFinalCost() != null) {
            row.put("final_cost", Parameter.from(assignment.getFinalCost()));
        }
        if (assignment.getUserCommentary() != null) {
            row.put("user_commentary", Parameter.from(assignment.getUserCommentary()));
        }
        if (assignment.getEmployeeCommentary() != null) {
            row.put("employee_commentary", Parameter.from(assignment.getEmployeeCommentary()));
        }
        return row;
    }

}
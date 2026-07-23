package br.com.logicore.modules.department.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentSummaryResponse {

    private long total;

    private long active;

    private long inactive;
}
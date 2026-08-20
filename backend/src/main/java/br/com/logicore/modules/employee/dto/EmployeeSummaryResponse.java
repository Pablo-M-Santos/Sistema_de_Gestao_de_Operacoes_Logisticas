package br.com.logicore.modules.employee.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@SuppressWarnings("unused")
public class EmployeeSummaryResponse {

    private long total;

    private long active;

    private long inactive;

    private long withAddress;

    private long withoutAddress;
}


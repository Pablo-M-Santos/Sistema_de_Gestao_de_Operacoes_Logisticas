package br.com.logicore.modules.driver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverSummaryResponse {

    private long total;
}

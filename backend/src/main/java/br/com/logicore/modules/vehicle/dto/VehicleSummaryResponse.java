package br.com.logicore.modules.vehicle.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleSummaryResponse {

    private long total;

    private long active;

    private long inactive;
}

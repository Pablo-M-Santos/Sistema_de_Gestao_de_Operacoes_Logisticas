package br.com.logicore.modules.cargo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CargoSummaryResponse {

    private long total;

    private long active;

    private long inactive;
}
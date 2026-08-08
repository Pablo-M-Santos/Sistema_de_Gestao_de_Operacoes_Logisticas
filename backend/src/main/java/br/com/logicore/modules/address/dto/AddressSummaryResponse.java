package br.com.logicore.modules.address.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@SuppressWarnings("unused")
public class AddressSummaryResponse {

    private long total;

    private long withCoordinates;

    private long withoutCoordinates;
}



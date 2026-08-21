package br.com.logicore.modules.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientSummaryResponse {

    private long total;

    private long active;

    private long inactive;

    private long withAddress;

    private long withoutAddress;
}

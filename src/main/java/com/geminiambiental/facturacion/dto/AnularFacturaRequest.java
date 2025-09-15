package com.geminiambiental.facturacion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnularFacturaRequest {
    
    @JsonProperty("motivo")
    private String motivo;
}
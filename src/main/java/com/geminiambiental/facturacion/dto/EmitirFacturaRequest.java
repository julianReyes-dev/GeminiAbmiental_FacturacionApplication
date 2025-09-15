package com.geminiambiental.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmitirFacturaRequest {
    
    @JsonProperty("idsServicios")
    @NotNull(message = "La lista de servicios es requerida")
    @NotEmpty(message = "Debe incluir al menos un servicio")
    private List<@NotBlank(message = "ID de servicio no puede estar vacío") String> idsServicios;
    
    @JsonProperty("observaciones")
    private String observaciones;
}
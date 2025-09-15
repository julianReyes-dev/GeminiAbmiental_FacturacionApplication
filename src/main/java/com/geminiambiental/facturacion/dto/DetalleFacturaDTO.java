package com.geminiambiental.facturacion.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleFacturaDTO {
    private String idServicio;
    private String descripcionServicio;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;
}
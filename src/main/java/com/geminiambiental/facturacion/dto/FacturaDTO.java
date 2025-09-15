package com.geminiambiental.facturacion.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FacturaDTO {
    private String idFactura;
    private String dniCliente;
    private String nombreCliente;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaPago;
    private BigDecimal montoTotal;
    private String estado;
    private String observaciones;
    private String tipoServicio;
    private List<DetalleFacturaDTO> detalles;
}
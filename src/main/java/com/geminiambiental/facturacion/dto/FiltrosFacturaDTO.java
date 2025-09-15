package com.geminiambiental.facturacion.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FiltrosFacturaDTO {
    private String cliente;
    private String estado;
    private String servicio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int page = 0;
    private int size = 10;
    private String sortBy = "fechaEmision";
    private String sortDir = "DESC";
}
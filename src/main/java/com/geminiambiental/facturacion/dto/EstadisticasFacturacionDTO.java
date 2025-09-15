package com.geminiambiental.facturacion.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EstadisticasFacturacionDTO {
    private long facturasPendientes;
    private long facturasVencidas;
    private long facturasPagadas;
    private long facturasAnuladas;
    private BigDecimal montoTotalPendiente;
    private BigDecimal montoTotalVencido;
    private BigDecimal montoTotalPagado;
}
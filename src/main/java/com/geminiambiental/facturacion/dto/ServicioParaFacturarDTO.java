package com.geminiambiental.facturacion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioParaFacturarDTO {
    
    private String idServicio;
    private String idCotizacion;
    private String dniEmpleadoAsignado;
    private LocalDate fecha;
    private LocalTime hora;
    private String duracionEstimada;
    private String observaciones;
    private String prioridad;
    private String estado;
    private String tipoServicio;
    private String nombreCliente;
    private BigDecimal montoTotal;
    private List<ProductoUtilizadoDTO> productosUtilizados;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoUtilizadoDTO {
        private String idProducto;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String unidadMedida;
    }
}
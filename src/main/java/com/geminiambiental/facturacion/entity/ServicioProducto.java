package com.geminiambiental.facturacion.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "servicio_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioProducto {
    
    @EmbeddedId
    private ServicioProductoId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idServicio")
    @JoinColumn(name = "ID_servicio")
    @JsonBackReference
    private Servicio servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "ID_producto")
    private Producto producto;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioActual;
}
package com.geminiambiental.facturacion.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {
    
    @Id
    @Column(name = "ID_producto", length = 36)
    private String idProducto;
    
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;
    
    @Column(name = "precio_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioActual;
    
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;
    
    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;
    
    @Column(name = "ID_categoria_producto", length = 36)
    private String idCategoriaProducto;
}
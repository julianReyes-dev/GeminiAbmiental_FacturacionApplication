package com.geminiambiental.facturacion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioProductoId implements Serializable {
    
    @Column(name = "ID_servicio", length = 36)
    private String idServicio;
    
    @Column(name = "ID_producto", length = 36)
    private String idProducto;
}
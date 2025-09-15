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
public class DetalleFacturaId implements Serializable {
    
    @Column(name = "ID_factura", length = 36)
    private String idFactura;
    
    @Column(name = "ID_servicio", length = 36)
    private String idServicio;
}
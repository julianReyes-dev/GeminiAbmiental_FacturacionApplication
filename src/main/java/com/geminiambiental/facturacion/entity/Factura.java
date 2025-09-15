package com.geminiambiental.facturacion.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    
    @Id
    @Column(name = "ID_factura", length = 36)
    private String idFactura;
    
    @Column(name = "DNI_cliente", nullable = false, length = 20)
    private String dniCliente;
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    
    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estado = EstadoFactura.Pendiente;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles = new ArrayList<>();
    
    // Campos adicionales para reportes
    @Transient
    private String nombreCliente;
    
    @Transient
    private String tipoServicio;
    
    public enum EstadoFactura {
        Pendiente, Pagada, Vencida, Anulada
    }
    
    @PrePersist
    public void generarId() {
        if (this.idFactura == null) {
            this.idFactura = java.util.UUID.randomUUID().toString();
        }
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDate.now();
        }
        // Establecer fecha de vencimiento (30 días por defecto)
        if (this.fechaVencimiento == null) {
            this.fechaVencimiento = this.fechaEmision.plusDays(30);
        }
    }
}
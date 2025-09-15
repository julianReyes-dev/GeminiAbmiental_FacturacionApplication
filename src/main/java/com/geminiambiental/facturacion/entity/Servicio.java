package com.geminiambiental.facturacion.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "Servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    
    @Id
    @Column(name = "ID_servicio", length = 36)
    private String idServicio;
    
    @Column(name = "ID_cotizacion", length = 36)
    private String idCotizacion;
    
    @Column(name = "DNI_empleado_asignado", length = 20)
    private String dniEmpleadoAsignado;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "hora", nullable = false)
    private LocalTime hora;
    
    @Column(name = "duracion_estimada", length = 100)
    private String duracionEstimada;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "prioridad", length = 50)
    private String prioridad;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoServicio estado = EstadoServicio.PROGRAMADO;
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ServicioProducto> productos;
    
    // Campos adicionales para reportes
    @Transient
    private String tipoServicio;
    
    @Transient
    private String nombreCliente;
    
    public enum EstadoServicio {
        PROGRAMADO, EN_PROGRESO, COMPLETADO, CANCELADO
    }
}
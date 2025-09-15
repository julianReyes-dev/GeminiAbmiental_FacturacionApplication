package com.geminiambiental.facturacion.repository;

import com.geminiambiental.facturacion.entity.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {
    
    @Query("""
        SELECT f FROM Factura f 
        WHERE (:cliente IS NULL OR LOWER(f.dniCliente) LIKE LOWER(CONCAT('%', :cliente, '%')))
        AND (:estado IS NULL OR f.estado = :estado)
        AND (:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio)
        AND (:fechaFin IS NULL OR f.fechaEmision <= :fechaFin)
        """)
    Page<Factura> findWithFilters(
        @Param("cliente") String cliente,
        @Param("estado") Factura.EstadoFactura estado,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        Pageable pageable);
    
    List<Factura> findByEstado(Factura.EstadoFactura estado);
    
    List<Factura> findByDniCliente(String dniCliente);
    
    @Query("SELECT COUNT(f) FROM Factura f WHERE f.estado = :estado")
    long countByEstado(@Param("estado") Factura.EstadoFactura estado);
    
    @Query("SELECT SUM(f.montoTotal) FROM Factura f WHERE f.estado = :estado")
    java.math.BigDecimal sumMontoByEstado(@Param("estado") Factura.EstadoFactura estado);
    
    @Query("SELECT f FROM Factura f WHERE f.fechaVencimiento < CURRENT_DATE AND f.estado = 'Pendiente'")
    List<Factura> findFacturasVencidas();
    
    // Generar número consecutivo de factura
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(f.idFactura, LOCATE('-', f.idFactura, LOCATE('-', f.idFactura) + 1) + 1) AS int)), 0) FROM Factura f WHERE f.idFactura LIKE CONCAT('F-', YEAR(CURRENT_DATE), '-%')")
    Integer getMaxNumeroFacturaDelAno();
}

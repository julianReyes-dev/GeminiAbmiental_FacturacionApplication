package com.geminiambiental.facturacion.repository;

import com.geminiambiental.facturacion.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, String> {
    
    List<Servicio> findByEstado(Servicio.EstadoServicio estado);
    
    @Query("""
        SELECT s FROM Servicio s 
        LEFT JOIN DetalleFactura df ON s.idServicio = df.id.idServicio 
        WHERE df.id.idServicio IS NULL 
        AND s.estado = 'COMPLETADO'
        """)
    List<Servicio> findServiciosCompletadosSinFacturar();
    
    @Query("""
        SELECT s FROM Servicio s 
        WHERE s.idCotizacion = :idCotizacion 
        AND s.estado = 'COMPLETADO'
        """)
    List<Servicio> findServiciosCompletadosByCotizacion(@Param("idCotizacion") String idCotizacion);
}
package com.geminiambiental.facturacion.controller;

import com.geminiambiental.facturacion.dto.*;
import com.geminiambiental.facturacion.entity.Servicio;
import com.geminiambiental.facturacion.service.FacturacionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/facturacion")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FacturacionController {
    
    private final FacturacionService facturacionService;
    
    @PostMapping("/emitir")
    public ResponseEntity<FacturaDTO> emitirFactura(@Valid @RequestBody EmitirFacturaRequest request) {
        try {
            log.info("Request object: {}", request);
            log.info("IdsServicios field: {}", request != null ? request.getIdsServicios() : "REQUEST IS NULL");
            
            // Validación adicional de entrada
            if (request == null) {
                log.warn("Request nulo recibido para emitir factura");
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getIdsServicios() == null || request.getIdsServicios().isEmpty()) {
                log.warn("Lista de servicios nula o vacía: {}", request.getIdsServicios());
                return ResponseEntity.badRequest().build();
            }
            
            log.info("Recibida petición para emitir factura con servicios: {} y observaciones: {}", 
                    request.getIdsServicios(), request.getObservaciones());
            
            FacturaDTO factura = facturacionService.emitirFactura(
                request.getIdsServicios(), 
                request.getObservaciones()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(factura);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al emitir factura: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al emitir factura", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/{idFactura}/marcar-pagada")
    public ResponseEntity<FacturaDTO> marcarComoPagada(@PathVariable String idFactura) {
        try {
            FacturaDTO factura = facturacionService.marcarComoPagada(idFactura);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            log.warn("Factura no encontrada: {}", idFactura);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Estado inválido para marcar como pagada: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al marcar factura como pagada", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/{idFactura}/anular")
    public ResponseEntity<FacturaDTO> anularFactura(
            @PathVariable String idFactura, 
            @Valid @RequestBody AnularFacturaRequest request) {
        try {
            FacturaDTO factura = facturacionService.anularFactura(idFactura, request.getMotivo());
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            log.warn("Factura no encontrada: {}", idFactura);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Estado inválido para anular: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al anular factura", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<FacturaDTO>> buscarFacturas(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String servicio,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaEmision") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        try {
            FiltrosFacturaDTO filtros = new FiltrosFacturaDTO();
            filtros.setCliente(cliente);
            filtros.setEstado(estado);
            filtros.setServicio(servicio);
            filtros.setPage(page);
            filtros.setSize(size);
            filtros.setSortBy(sortBy);
            filtros.setSortDir(sortDir);
            
            if (fechaInicio != null) {
                filtros.setFechaInicio(java.time.LocalDate.parse(fechaInicio));
            }
            if (fechaFin != null) {
                filtros.setFechaFin(java.time.LocalDate.parse(fechaFin));
            }
            
            Page<FacturaDTO> facturas = facturacionService.buscarFacturas(filtros);
            return ResponseEntity.ok(facturas);
        } catch (Exception e) {
            log.error("Error al buscar facturas", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{idFactura}")
    public ResponseEntity<FacturaDTO> obtenerFactura(@PathVariable String idFactura) {
        try {
            // TODO: Implementar método en el servicio para obtener una factura por ID
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al obtener factura", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasFacturacionDTO> obtenerEstadisticas() {
        try {
            EstadisticasFacturacionDTO estadisticas = facturacionService.obtenerEstadisticas();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error("Error al obtener estadísticas", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/servicios-para-facturar")
    public ResponseEntity<List<ServicioParaFacturarDTO>> obtenerServiciosParaFacturar() {
        try {
            List<ServicioParaFacturarDTO> servicios = facturacionService.obtenerServiciosParaFacturar();
            return ResponseEntity.ok(servicios);
        } catch (Exception e) {
            log.error("Error al obtener servicios para facturar", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/procesar-vencidas")
    public ResponseEntity<Void> procesarFacturasVencidas() {
        try {
            facturacionService.procesarFacturasVencidas();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al procesar facturas vencidas", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
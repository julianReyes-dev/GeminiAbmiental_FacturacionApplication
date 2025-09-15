package com.geminiambiental.facturacion.service;

import com.geminiambiental.facturacion.dto.*;
import com.geminiambiental.facturacion.entity.*;
import com.geminiambiental.facturacion.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturacionService {
    
    private final FacturaRepository facturaRepository;
    private final ServicioRepository servicioRepository;
    
    @Transactional
    public FacturaDTO emitirFactura(List<String> idsServicios, String observaciones) {
        log.info("Iniciando emisión de factura para servicios: {}", idsServicios);
        
        // Validación de entrada más robusta
        if (idsServicios == null || idsServicios.isEmpty()) {
            throw new IllegalArgumentException("La lista de servicios no puede estar vacía");
        }
        
        // Filtrar valores nulos o vacíos
        List<String> idsValidos = idsServicios.stream()
            .filter(id -> id != null && !id.trim().isEmpty())
            .collect(Collectors.toList());
        
        if (idsValidos.isEmpty()) {
            throw new IllegalArgumentException("No se proporcionaron IDs de servicios válidos");
        }
        
        log.info("IDs válidos de servicios: {}", idsValidos);
        
        // Validar que los servicios existan y estén completados
        List<Servicio> servicios = servicioRepository.findAllById(idsValidos);
        
        if (servicios.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron servicios válidos con los IDs proporcionados: " + idsValidos);
        }
        
        if (servicios.size() != idsValidos.size()) {
            List<String> idsEncontrados = servicios.stream()
                .map(Servicio::getIdServicio)
                .collect(Collectors.toList());
            List<String> idsNoEncontrados = idsValidos.stream()
                .filter(id -> !idsEncontrados.contains(id))
                .collect(Collectors.toList());
            throw new IllegalArgumentException("No se encontraron los siguientes servicios: " + idsNoEncontrados);
        }
        
        // Verificar que todos los servicios estén completados
        List<Servicio> serviciosNoCompletados = servicios.stream()
            .filter(s -> s.getEstado() != Servicio.EstadoServicio.COMPLETADO)
            .collect(Collectors.toList());
        
        if (!serviciosNoCompletados.isEmpty()) {
            List<String> idsNoCompletados = serviciosNoCompletados.stream()
                .map(Servicio::getIdServicio)
                .collect(Collectors.toList());
            throw new IllegalArgumentException("Los siguientes servicios deben estar completados para facturar: " + idsNoCompletados);
        }
        
        // Obtener cliente (asumiendo que todos los servicios son del mismo cliente)
        String dniCliente = obtenerClienteDeServicios(servicios);
        
        // Crear factura
        Factura factura = new Factura();
        String numeroFactura = generarNumeroFactura();
        factura.setIdFactura(numeroFactura);
        factura.setDniCliente(dniCliente);
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(30)); // 30 días de vencimiento
        factura.setObservaciones(observaciones);
        factura.setEstado(Factura.EstadoFactura.Pendiente);
        
        // Inicializar la lista de detalles
        factura.setDetalles(new ArrayList<>());
        
        // Crear detalles
        BigDecimal montoTotal = BigDecimal.ZERO;
        for (Servicio servicio : servicios) {
            DetalleFactura detalle = new DetalleFactura();
            
            // Crear el ID compuesto
            DetalleFacturaId detalleId = new DetalleFacturaId();
            detalleId.setIdFactura(factura.getIdFactura());
            detalleId.setIdServicio(servicio.getIdServicio());
            
            detalle.setId(detalleId);
            detalle.setFactura(factura);
            detalle.setServicio(servicio);
            
            // Calcular precio basado en productos utilizados
            BigDecimal precioServicio = calcularPrecioServicio(servicio);
            detalle.setPrecioUnitario(precioServicio);
            detalle.setCantidad(1);
            
            // Calcular subtotal manualmente por si acaso
            detalle.setSubtotal(precioServicio.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            
            factura.getDetalles().add(detalle);
            montoTotal = montoTotal.add(detalle.getSubtotal());
        }
        
        factura.setMontoTotal(montoTotal);
        
        try {
            // Guardar factura
            Factura facturaGuardada = facturaRepository.save(factura);
            log.info("Factura {} emitida exitosamente por valor de {}", factura.getIdFactura(), montoTotal);
            
            return convertirADTO(facturaGuardada);
        } catch (Exception e) {
            log.error("Error al guardar la factura: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar la factura: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public FacturaDTO marcarComoPagada(String idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
            .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
        
        if (factura.getEstado() != Factura.EstadoFactura.Pendiente) {
            throw new IllegalStateException("Solo se pueden marcar como pagadas las facturas pendientes");
        }
        
        factura.setEstado(Factura.EstadoFactura.Pagada);
        factura.setFechaPago(LocalDateTime.now());
        
        Factura facturaActualizada = facturaRepository.save(factura);
        log.info("Factura {} marcada como pagada", idFactura);
        
        return convertirADTO(facturaActualizada);
    }
    
    @Transactional
    public FacturaDTO anularFactura(String idFactura, String motivo) {
        Factura factura = facturaRepository.findById(idFactura)
            .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
        
        if (factura.getEstado() == Factura.EstadoFactura.Pagada) {
            throw new IllegalStateException("No se pueden anular facturas pagadas");
        }
        
        factura.setEstado(Factura.EstadoFactura.Anulada);
        factura.setObservaciones(factura.getObservaciones() + "\nANULADA: " + motivo);
        
        Factura facturaActualizada = facturaRepository.save(factura);
        log.info("Factura {} anulada. Motivo: {}", idFactura, motivo);
        
        return convertirADTO(facturaActualizada);
    }
    
    @Transactional(readOnly = true)
    public Page<FacturaDTO> buscarFacturas(FiltrosFacturaDTO filtros) {
        Sort sort = Sort.by(Sort.Direction.fromString(filtros.getSortDir()), filtros.getSortBy());
        PageRequest pageRequest = PageRequest.of(filtros.getPage(), filtros.getSize(), sort);
        
        Factura.EstadoFactura estado = null;
        if (filtros.getEstado() != null && !filtros.getEstado().equals("TODOS")) {
            estado = Factura.EstadoFactura.valueOf(filtros.getEstado());
        }
        
        Page<Factura> facturas = facturaRepository.findWithFilters(
            filtros.getCliente(),
            estado,
            filtros.getFechaInicio(),
            filtros.getFechaFin(),
            pageRequest
        );
        
        return facturas.map(this::convertirADTO);
    }
    
    @Transactional(readOnly = true)
    public EstadisticasFacturacionDTO obtenerEstadisticas() {
        EstadisticasFacturacionDTO stats = new EstadisticasFacturacionDTO();
        
        stats.setFacturasPendientes(facturaRepository.countByEstado(Factura.EstadoFactura.Pendiente));
        stats.setFacturasVencidas(facturaRepository.findFacturasVencidas().size());
        stats.setFacturasPagadas(facturaRepository.countByEstado(Factura.EstadoFactura.Pagada));
        stats.setFacturasAnuladas(facturaRepository.countByEstado(Factura.EstadoFactura.Pagada));
        
        stats.setMontoTotalPendiente(facturaRepository.sumMontoByEstado(Factura.EstadoFactura.Pendiente));
        stats.setMontoTotalPagado(facturaRepository.sumMontoByEstado(Factura.EstadoFactura.Pagada));
        
        // Calcular monto vencido
        BigDecimal montoVencido = facturaRepository.findFacturasVencidas().stream()
            .map(Factura::getMontoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMontoTotalVencido(montoVencido);
        
        return stats;
    }
    
    @Transactional
    public void procesarFacturasVencidas() {
        List<Factura> facturasVencidas = facturaRepository.findFacturasVencidas();
        
        for (Factura factura : facturasVencidas) {
            factura.setEstado(Factura.EstadoFactura.Vencida);
        }
        
        if (!facturasVencidas.isEmpty()) {
            facturaRepository.saveAll(facturasVencidas);
            log.info("Procesadas {} facturas vencidas", facturasVencidas.size());
        }
    }
    
    @Transactional(readOnly = true)
    public List<ServicioParaFacturarDTO> obtenerServiciosParaFacturar() {
        List<Servicio> servicios = servicioRepository.findServiciosCompletadosSinFacturar();
        
        return servicios.stream()
            .map(this::convertirServicioAFacturarDTO)
            .collect(Collectors.toList());
    }

    private ServicioParaFacturarDTO convertirServicioAFacturarDTO(Servicio servicio) {
        ServicioParaFacturarDTO dto = new ServicioParaFacturarDTO();
        dto.setIdServicio(servicio.getIdServicio());
        dto.setIdCotizacion(servicio.getIdCotizacion());
        dto.setDniEmpleadoAsignado(servicio.getDniEmpleadoAsignado());
        dto.setFecha(servicio.getFecha());
        dto.setHora(servicio.getHora());
        dto.setDuracionEstimada(servicio.getDuracionEstimada());
        dto.setObservaciones(servicio.getObservaciones());
        dto.setPrioridad(servicio.getPrioridad());
        dto.setEstado(servicio.getEstado().name());
        dto.setTipoServicio(servicio.getTipoServicio());
        dto.setNombreCliente(servicio.getNombreCliente());
        
        // Calcular monto total y productos
        BigDecimal montoTotal = BigDecimal.ZERO;
        List<ServicioParaFacturarDTO.ProductoUtilizadoDTO> productosDto = new ArrayList<>();
        
        if (servicio.getProductos() != null) {
            for (ServicioProducto sp : servicio.getProductos()) {
                // Verificar que el producto no sea un proxy lazy
                Producto producto = sp.getProducto();
                if (producto != null) {
                    ServicioParaFacturarDTO.ProductoUtilizadoDTO productoDto = 
                        new ServicioParaFacturarDTO.ProductoUtilizadoDTO();
                    
                    productoDto.setIdProducto(producto.getIdProducto());
                    productoDto.setNombreProducto(producto.getNombre());
                    productoDto.setCantidad(sp.getCantidad());
                    productoDto.setPrecioUnitario(sp.getPrecioActual());
                    productoDto.setUnidadMedida(producto.getUnidadMedida());
                    
                    BigDecimal subtotal = sp.getPrecioActual()
                        .multiply(BigDecimal.valueOf(sp.getCantidad()));
                    productoDto.setSubtotal(subtotal);
                    
                    productosDto.add(productoDto);
                    montoTotal = montoTotal.add(subtotal);
                }
            }
        }
        
        dto.setProductosUtilizados(productosDto);
        dto.setMontoTotal(montoTotal);
        
        return dto;
    }
    
    // Métodos privados auxiliares
    
    private String generarNumeroFactura() {
        Integer maxNumero = facturaRepository.getMaxNumeroFacturaDelAno();
        int nuevoNumero = (maxNumero != null ? maxNumero : 0) + 1;
        return String.format("F-%d-%03d", Year.now().getValue(), nuevoNumero);
    }
    
    private String obtenerClienteDeServicios(List<Servicio> servicios) {
        // Esta implementación asume que se necesita consultar la cotización o tener el cliente en el servicio
        // Aquí se debería implementar la lógica para obtener el DNI del cliente desde el servicio
        // Por ahora retorno un valor de ejemplo xd
        return "12345678"; // TODO: Implementar lógica real
    }
    
    private BigDecimal calcularPrecioServicio(Servicio servicio) {
        // Calcular precio basado en productos utilizados
        return servicio.getProductos().stream()
            .map(sp -> sp.getPrecioActual().multiply(BigDecimal.valueOf(sp.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private FacturaDTO convertirADTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setIdFactura(factura.getIdFactura());
        dto.setDniCliente(factura.getDniCliente());
        dto.setNombreCliente(factura.getNombreCliente()); // Campo transient
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setFechaVencimiento(factura.getFechaVencimiento());
        dto.setFechaPago(factura.getFechaPago());
        dto.setMontoTotal(factura.getMontoTotal());
        dto.setEstado(factura.getEstado().name());
        dto.setObservaciones(factura.getObservaciones());
        dto.setTipoServicio(factura.getTipoServicio()); // Campo transient
        
        if (factura.getDetalles() != null) {
            dto.setDetalles(factura.getDetalles().stream()
                .map(this::convertirDetalleADTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private DetalleFacturaDTO convertirDetalleADTO(DetalleFactura detalle) {
        DetalleFacturaDTO dto = new DetalleFacturaDTO();
        dto.setIdServicio(detalle.getId().getIdServicio());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubtotal(detalle.getSubtotal());
        // dto.setDescripcionServicio() - Se puede agregar consultando el servicio
        return dto;
    }
}

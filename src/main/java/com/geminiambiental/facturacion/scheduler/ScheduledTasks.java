package com.geminiambiental.facturacion.scheduler;

import com.geminiambiental.facturacion.service.FacturacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    
    private final FacturacionService facturacionService;
    
    // Ejecutar todos los días a las 2:00 AM para procesar facturas vencidas
    @Scheduled(cron = "0 0 2 * * ?")
    public void procesarFacturasVencidas() {
        log.info("Iniciando proceso automático de facturas vencidas");
        try {
            facturacionService.procesarFacturasVencidas();
            log.info("Proceso de facturas vencidas completado");
        } catch (Exception e) {
            log.error("Error en proceso automático de facturas vencidas", e);
        }
    }
}
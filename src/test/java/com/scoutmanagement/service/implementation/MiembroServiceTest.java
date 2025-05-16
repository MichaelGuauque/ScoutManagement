package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.MiembroRamaDTO;
import com.scoutmanagement.persistence.model.Cargo;
import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IObtencionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MiembroServiceTest {

    @InjectMocks
    private MiembroService miembroService;

    @Mock
    private IObtencionService obtencionService;

    @Mock
    private PersonaRepository personaRepository;

    @Test
    void testMostrarMiembrosRama() {
        // Arrange
        // Asegúrate de que esta clase exista
        Persona personaSesion = new Persona();
        personaSesion.setRama(Rama.CLAN);

        Persona miembro1 = new Persona();
        miembro1.setPrimerNombre("Juan");
        miembro1.setSegundoNombre("Maria");
        miembro1.setPrimerApellido("Pérez");
        miembro1.setSegundoApellido("Soto");
        miembro1.setCargo(Cargo.JEFE_COMUNIDAD); // Ajusta si el cargo es un enum
        miembro1.setRama(Rama.CLAN);

        List<Persona> miembrosMock = List.of(miembro1);

        Obtencion obtencion1 = new Obtencion();
        Obtencion obtencion2 = new Obtencion();
        List<Obtencion> insigniasMock = List.of(obtencion1, obtencion2);

        when(personaRepository.findByUserEntityActivoTrueAndRama(Rama.CLAN)).thenReturn(miembrosMock);
        when(obtencionService.ultimasObtenciones(miembro1)).thenReturn(insigniasMock);

        // Act
        List<MiembroRamaDTO> resultado = miembroService.mostrarMiembrosRama(personaSesion);

        // Assert
        assertEquals(1, resultado.size());
        MiembroRamaDTO dto = resultado.get(0);
        assertEquals("Juan Maria Pérez Soto", dto.getNombreCompleto());
        assertEquals("Jefe de Comunidad", dto.getCargo()); // O ajusta según cómo estés usando el enum
        assertEquals(insigniasMock, dto.getInsignias());
    }
}

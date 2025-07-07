package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
class ActividadServiceTest {

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private ActividadRepository actividadRepository;

    @BeforeEach
    void setUp() {
        actividadRepository.deleteAll();
    }

    private Actividad crearActividad(Long id, LocalDate fecha) {
        Actividad actividad = new Actividad();
        actividad.setId(id);
        actividad.setFecha(fecha);
        actividad.setRama(Rama.MANADA);
        return actividad;
    }

    private Actividad crearActividad(Long id, LocalDate fecha, Rama rama) {
        Actividad actividad = new Actividad();
        actividad.setId(id);
        actividad.setFecha(fecha);
        actividad.setRama(rama);
        return actividad;
    }

    @Test
    void testCrearEliminarActividad() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Invierno",
                "Un campamento para todos los miembros",
                Rama.MANADA,
                LocalDate.now().plusDays(7),
                "Cabaña en la montaña"
        );

        actividadService.crearActividad(actividadDTO);

        List<Actividad> actividadesGuardadas = actividadService.findAllActividad();
        List<Actividad> ordenadas = actividadService.findAllActividadesOrdenadas();

        assertFalse(actividadesGuardadas.isEmpty(), "No se encontraron actividades guardadas.");

        Actividad actividadGuardada = ordenadas.get(0);

        assertEquals("Campamento de Invierno", actividadGuardada.getNombre(), "El nombre no coincide.");
        assertEquals("Un campamento para todos los miembros", actividadGuardada.getDescripcion(), "La descripción no coincide.");
        assertEquals("Cabaña en la montaña", actividadGuardada.getUbicacion(), "La ubicación no coincide.");
        assertEquals(Rama.MANADA, actividadGuardada.getRama(), "La rama no coincide.");

        actividadService.eliminarActividad(actividadGuardada.getId());
        Optional<Actividad> actividadEliminada = actividadService.findById(actividadGuardada.getId());
        assertFalse(actividadEliminada.isPresent(), "La actividad no fue eliminada correctamente.");

    }

    @Test
    void testCrearActividadFallo() {

        ActividadDTO actividadDTO = new ActividadDTO(null,null,null,null,null);

        assertThrows(ServiceException.class, () -> {
            actividadService.crearActividad(actividadDTO);
        });
    }

    @Test
    void testModificarActividadFallo() {
        Actividad actividad = new Actividad();
        actividad.setId(123L);

        assertThrows(IllegalArgumentException.class, () -> {
            actividadService.modificarActividad(actividad);
        });
    }

    @Test
    void buscarActividadPorNombre() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Verano",
                "Un campamento para tropa",
                Rama.TROPA,
                LocalDate.now().plusDays(7),
                "Cabaña en la laguna"
        );

        actividadService.crearActividad(actividadDTO);

        Optional<Actividad> actividadEncontrada = actividadService.findByNombre("Campamento de Verano");

        assertFalse(actividadEncontrada.isEmpty(), "No se encontraron actividades con ese nombre.");
        assertEquals("Campamento de Verano", actividadEncontrada.get().getNombre(), "El nombre de la actividad encontrada no es correcto.");
        assertEquals(Rama.TROPA, actividadEncontrada.get().getRama(), "La rama de la actividad encontrada no es correcta.");

        List<Actividad> actividadesPorRama = actividadService.findAllByRama(Rama.TROPA);

        assertFalse(actividadesPorRama.isEmpty(), "No se encontraron actividades por rama.");
        assertTrue(actividadesPorRama.stream().anyMatch(a -> a.getNombre().equals("Campamento de Verano")),
                "No se encontró la actividad en la lista por rama.");

    }

    @Test
    void testModificarActividad() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Otoño",
                "Un campamento para todos los miembros",
                Rama.CLAN,
                LocalDate.now().plusDays(7),
                "Cabaña en la montaña"
        );

        actividadService.crearActividad(actividadDTO);

        Optional<Actividad> actividadCreada = actividadService.findByNombre("Campamento de Otoño");
        assertTrue(actividadCreada.isPresent(), "La actividad no fue creada correctamente.");

        Actividad actividad = actividadCreada.get();
        actividad.setNombre("Campamento de Verano");
        actividad.setDescripcion("Un campamento para jóvenes");
        actividad.setUbicacion("Playa");
        actividad.setRama(Rama.COMUNIDAD);

        Actividad actividadModificada = actividadService.modificarActividad(actividad);

        assertNotNull(actividadModificada, "La actividad modificada no debe ser nula.");
        assertEquals("Campamento de Verano", actividadModificada.getNombre(), "El nombre no se modificó correctamente.");
        assertEquals("Un campamento para jóvenes", actividadModificada.getDescripcion(), "La descripción no se modificó correctamente.");
        assertEquals("Playa", actividadModificada.getUbicacion(), "La ubicación no se modificó correctamente.");
        assertEquals(Rama.COMUNIDAD, actividadModificada.getRama(), "La rama no se modificó correctamente.");
    }

    @Test
    void testEncontrarActividadMasProxima_CuandoHayProxima() {
        List<Actividad> actividades = List.of(
                crearActividad(1L, LocalDate.now().plusDays(5)),
                crearActividad(2L, LocalDate.now().plusDays(1)),
                crearActividad(3L, LocalDate.now().plusDays(3))
        );

        Map<Long, Boolean> resultado = actividadService.encontrarActividadMasProxima(actividades, 0, "proximas");

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey(2L));
    }

    @Test
    void testEncontrarActividadMasProxima_NoMarcaSiNoEsPrimeraPagina() {
        List<Actividad> actividades = List.of(
                crearActividad(1L, LocalDate.now().plusDays(1))
        );

        Map<Long, Boolean> resultado = actividadService.encontrarActividadMasProxima(actividades, 1, "proximas");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testPaginarActividades() {
        List<Actividad> actividades = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> crearActividad((long) i, LocalDate.now()))
                .collect(Collectors.toList());

        List<Actividad> resultado = actividadService.paginarActividades(actividades, 1, 3);

        assertEquals(3, resultado.size());
        assertEquals(4L, resultado.get(0).getId());
    }

    @Test
    void testFiltrarYOrdenarActividadesPorTab_ProximasSinRama() {
        LocalDate hoy = LocalDate.now();
        List<Actividad> actividades = List.of(
                crearActividad(1L, hoy.plusDays(1)),
                crearActividad(2L, hoy.plusDays(5)),
                crearActividad(3L, hoy.minusDays(1))
        );

        List<Actividad> resultado = actividadService.filtrarYOrdenarActividadesPorTab(actividades, null, "proximas", hoy,null);

        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getFecha().isAfter(hoy));
    }

    @Test
    void testFiltrarYOrdenarActividadesPorTab_PasadasConRama() {
        LocalDate hoy = LocalDate.now();
        List<Actividad> actividades = List.of(
                crearActividad(1L, hoy.minusDays(3), Rama.MANADA),
                crearActividad(2L, hoy.minusDays(5), Rama.TROPA),
                crearActividad(3L, hoy.minusDays(1), Rama.MANADA)
        );

        List<Actividad> resultado = actividadService.filtrarYOrdenarActividadesPorTab(actividades, Rama.MANADA, "pasadas", hoy, null);

        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getFecha().isAfter(resultado.get(1).getFecha()));
    }

    @Test
    void testEncontrarActividadMasProxima_ListaVacia() {
        List<Actividad> actividades = new ArrayList<>();

        Map<Long, Boolean> resultado = actividadService.encontrarActividadMasProxima(actividades, 0, "proximas");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testEncontrarActividadMasProxima_NoEsPaginaCero() {
        LocalDate hoy = LocalDate.now();
        List<Actividad> actividades = List.of(
                crearActividad(1L, hoy.plusDays(1)),
                crearActividad(2L, hoy.plusDays(2))
        );

        Map<Long, Boolean> resultado = actividadService.encontrarActividadMasProxima(actividades, 1, "proximas");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFiltrarYOrdenarActividadesPorTab_ConFechaFiltro() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaFiltro = hoy.plusDays(2);
        List<Actividad> actividades = List.of(
                crearActividad(1L, hoy.plusDays(1)),
                crearActividad(2L, fechaFiltro),
                crearActividad(3L, hoy.plusDays(3))
        );

        List<Actividad> resultado = actividadService.filtrarYOrdenarActividadesPorTab(actividades, null, "proximas", hoy, fechaFiltro);

        assertEquals(1, resultado.size());
        assertEquals(fechaFiltro, resultado.get(0).getFecha());
    }

    @Test
    void testFiltrarYOrdenarActividadesPorTab_ProximasConRama() {
        LocalDate hoy = LocalDate.now();
        List<Actividad> actividades = List.of(
                crearActividad(1L, hoy.plusDays(2), Rama.MANADA),
                crearActividad(2L, hoy.plusDays(3), Rama.TROPA),
                crearActividad(3L, hoy.plusDays(4), Rama.MANADA)
        );

        List<Actividad> resultado = actividadService.filtrarYOrdenarActividadesPorTab(actividades, Rama.MANADA, "proximas", hoy, null);


        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(a -> a.getRama() == Rama.MANADA));
    }

    @Test
    void testContarActividadesPendientes_SinActividades() {
        Long resultado = actividadService.contarActividadesPendientes();

        assertEquals(0L, resultado, "Debería retornar 0 cuando no hay actividades pendientes");
    }

    @Test
    void testContarActividadesPendientes_ConActividades() {
        ActividadDTO actividad1 = new ActividadDTO(
                "Actividad Futura 1",
                "Descripción actividad 1",
                Rama.MANADA,
                LocalDate.now().plusDays(5),
                "Ubicación 1"
        );

        ActividadDTO actividad2 = new ActividadDTO(
                "Actividad Futura 2",
                "Descripción actividad 2",
                Rama.TROPA,
                LocalDate.now().plusDays(10),
                "Ubicación 2"
        );

        ActividadDTO actividadPasada = new ActividadDTO(
                "Actividad Pasada",
                "Descripción actividad pasada",
                Rama.CLAN,
                LocalDate.now().minusDays(3),
                "Ubicación pasada"
        );

        actividadService.crearActividad(actividad1);
        actividadService.crearActividad(actividad2);
        actividadService.crearActividad(actividadPasada);

        Long resultado = actividadService.contarActividadesPendientes();

        assertEquals(2L, resultado, "Debería contar solo las actividades futuras/pendientes");
    }

    @Test
    void testContarActividadesEstaSemana_SinActividades() {

        Long resultado = actividadService.contarActividadesEstaSemana();

        assertEquals(0L, resultado, "Debería retornar 0 cuando no hay actividades esta semana");
    }

    @Test
    void testContarActividadesEstaSemana_ConActividadesEstaSemana() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        ActividadDTO actividadEstaSemana1 = new ActividadDTO(
                "Actividad Lunes",
                "Actividad del lunes",
                Rama.MANADA,
                inicioSemana,
                "Ubicación 1"
        );

        ActividadDTO actividadEstaSemana2 = new ActividadDTO(
                "Actividad Miércoles",
                "Actividad del miércoles",
                Rama.TROPA,
                inicioSemana.plusDays(2),
                "Ubicación 2"
        );

        ActividadDTO actividadEstaSemana3 = new ActividadDTO(
                "Actividad Domingo",
                "Actividad del domingo",
                Rama.CLAN,
                finSemana,
                "Ubicación 3"
        );

        ActividadDTO actividadSemanaAnterior = new ActividadDTO(
                "Actividad Semana Pasada",
                "Actividad de la semana pasada",
                Rama.COMUNIDAD,
                inicioSemana.minusDays(7),
                "Ubicación pasada"
        );

        ActividadDTO actividadSemanaProxima = new ActividadDTO(
                "Actividad Semana Próxima",
                "Actividad de la semana próxima",
                Rama.MANADA,
                finSemana.plusDays(1),
                "Ubicación futura"
        );

        actividadService.crearActividad(actividadEstaSemana1);
        actividadService.crearActividad(actividadEstaSemana2);
        actividadService.crearActividad(actividadEstaSemana3);
        actividadService.crearActividad(actividadSemanaAnterior);
        actividadService.crearActividad(actividadSemanaProxima);

        Long resultado = actividadService.contarActividadesEstaSemana();

        assertEquals(3L, resultado, "Debería contar solo las actividades de esta semana");
    }

    @Test
    void testContarActividadesEstaSemana_SoloDomingo() {
        LocalDate domingo = LocalDate.now().with(DayOfWeek.SUNDAY);

        ActividadDTO actividadDomingo = new ActividadDTO(
                "Actividad Solo Domingo",
                "Actividad únicamente el domingo",
                Rama.MANADA,
                domingo,
                "Ubicación domingo"
        );

        actividadService.crearActividad(actividadDomingo);
        Long resultado = actividadService.contarActividadesEstaSemana();

        assertEquals(1L, resultado, "Debería contar la actividad del domingo");
    }

    @Test
    void testContarActividadesEstaSemana_MismaFechaVariasActividades() {
        LocalDate fechaEnSemana = LocalDate.now().with(DayOfWeek.WEDNESDAY);

        ActividadDTO actividad1 = new ActividadDTO(
                "Actividad 1 Miércoles",
                "Primera actividad del miércoles",
                Rama.MANADA,
                fechaEnSemana,
                "Ubicación 1"
        );

        ActividadDTO actividad2 = new ActividadDTO(
                "Actividad 2 Miércoles",
                "Segunda actividad del miércoles",
                Rama.TROPA,
                fechaEnSemana,
                "Ubicación 2"
        );

        ActividadDTO actividad3 = new ActividadDTO(
                "Actividad 3 Miércoles",
                "Tercera actividad del miércoles",
                Rama.CLAN,
                fechaEnSemana,
                "Ubicación 3"
        );

        actividadService.crearActividad(actividad1);
        actividadService.crearActividad(actividad2);
        actividadService.crearActividad(actividad3);

        Long resultado = actividadService.contarActividadesEstaSemana();

        assertEquals(3L, resultado, "Debería contar todas las actividades del mismo día");
    }

    @Test
    void testContarActividadesPendientes_ConActividadesHoy() {
        ActividadDTO actividadHoy = new ActividadDTO(
                "Actividad Hoy",
                "Actividad programada para hoy",
                Rama.MANADA,
                LocalDate.now(),
                "Ubicación hoy"
        );

        ActividadDTO actividadFutura = new ActividadDTO(
                "Actividad Futura",
                "Actividad futura",
                Rama.TROPA,
                LocalDate.now().plusDays(1),
                "Ubicación futura"
        );

        actividadService.crearActividad(actividadHoy);
        actividadService.crearActividad(actividadFutura);

        Long resultado = actividadService.contarActividadesPendientes();

        assertTrue(resultado >= 1L, "Debería contar al menos la actividad futura");
    }

}
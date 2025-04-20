package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonaServiceTest {

    @Test
    void testSave() {

        PersonaRegistroDTO dto = new PersonaRegistroDTO();
        dto.setPrimerNombre("Juan");
        dto.setSegundoNombre("Carlos");
        dto.setPrimerApellido("Pérez");
        dto.setSegundoApellido("Gómez");
        dto.setNumeroDeDocumento(12345678L);
        dto.setTipoDeDocumento(TipoDeDocumento.CC);
        dto.setGenero(Genero.MASCULINO);
        dto.setRama(Rama.MANADA);
        dto.setCargo(Cargo.LOBATO);


        UserEntity user = new UserEntity();
        user.setUsername("correo@correo.com");
        user.setPassword("1234");
        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setAccountNoLocked(true);
        user.setCredentialNoExpired(true);

        RoleEntity role = new RoleEntity();
        role.setRole(Rol.JOVEN); // Aquí usas directamente tu enum Rol
        user.getRoles().add(role);

        dto.setUserEntity(user);


        PersonaService personaService = new PersonaService();
        Persona persona = personaService.cambiarRegistroPersonaRegistroDTO(dto);


        assertEquals("Juan", persona.getPrimerNombre());
        assertEquals("Carlos", persona.getSegundoNombre());
        assertEquals("Pérez", persona.getPrimerApellido());
        assertEquals("Gómez", persona.getSegundoApellido());
        assertEquals(12345678L, persona.getNumeroDeDocumento());
        assertEquals(TipoDeDocumento.CC, persona.getTipoDeDocumento());
        assertEquals(Genero.MASCULINO, persona.getGenero());
        assertEquals(Rama.MANADA, persona.getRama());
        assertEquals(Cargo.LOBATO, persona.getCargo());

        UserEntity userEntity = persona.getUserEntity();
        assertNotNull(userEntity);
        assertEquals("correo@correo.com", userEntity.getUsername());
        assertEquals("1234", userEntity.getPassword());
        assertTrue(userEntity.isEnabled());
        assertTrue(userEntity.isAccountNoExpired());
        assertTrue(userEntity.isAccountNoLocked());
        assertTrue(userEntity.isCredentialNoExpired());

        System.out.println("Persona mapeada:");
        System.out.println("Nombre completo: " + persona.getPrimerNombre() + " " + persona.getSegundoNombre() + " " +
                persona.getPrimerApellido() + " " + persona.getSegundoApellido());
        System.out.println("Documento: " + persona.getTipoDeDocumento() + " " + persona.getNumeroDeDocumento());
        System.out.println("Género: " + persona.getGenero());
        System.out.println("Rama: " + persona.getRama());
        System.out.println("Cargo: " + persona.getCargo());

        UserEntity u = persona.getUserEntity();
        System.out.println("Usuario: " + u.getUsername());
        System.out.println("Password: " + u.getPassword());
        System.out.println("Habilitado: " + u.isEnabled());
        System.out.println("Cuenta no expirada: " + u.isAccountNoExpired());
        System.out.println("Cuenta no bloqueada: " + u.isAccountNoLocked());
        System.out.println("Credencial no expirada: " + u.isCredentialNoExpired());
        u.getRoles().forEach(rol -> System.out.println("Rol: " + rol.getRole()));

    }


}

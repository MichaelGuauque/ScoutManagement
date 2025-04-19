package com.scoutmanagement;

import com.scoutmanagement.persistence.model.PermissionEntity;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.persistence.model.RoleEntity;
import com.scoutmanagement.persistence.model.UserEntity;
import com.scoutmanagement.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class ScoutManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoutManagementApplication.class, args);
    }
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            //crear permisos
            PermissionEntity createPermission = PermissionEntity.builder()
                    .name("CREATE")
                    .build();
            PermissionEntity readPermission = PermissionEntity.builder()
                    .name("READ")
                    .build();
            PermissionEntity updatePermission = PermissionEntity.builder()
                    .name("UPDATE")
                    .build();
            PermissionEntity deletePermission = PermissionEntity.builder()
                    .name("DELETE")
                    .build();

            //crear roles
            RoleEntity adminRole = RoleEntity.builder()
                    .role(Rol.ADULTO)
                    .permissions(Set.of(createPermission, readPermission, updatePermission, deletePermission))
                    .build();
            RoleEntity userRole = RoleEntity.builder()
                    .role(Rol.JOVEN)
                    .permissions(Set.of(createPermission, readPermission))
                    .build();

            RoleEntity publicRole = RoleEntity.builder()
                    .role(Rol.PUBLICO)  // Nuevo rol PUBLIC
                    .permissions(Set.of(readPermission))
                    .build();

            //Crear usuarios
            UserEntity userAdmin = UserEntity.builder()
                        .username("admin@gmail.com")
                    .password("$2a$10$eeTu3yyhB9G8J1ZzFTEF8ORHLLh4XV9iKq0nhOHSPP5gt2zOi42dy")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(adminRole))
                    .build();

            UserEntity userMaigu = UserEntity.builder()
                    .username("pruebita123@gmail.com")
                    .password("$2a$10$eeTu3yyhB9G8J1ZzFTEF8ORHLLh4XV9iKq0nhOHSPP5gt2zOi42dy")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(userRole))
                    .build();

            UserEntity userPublic = UserEntity.builder()
                    .username("publicUser@gmail.com")
                    .password("$2a$10$eeTu3yyhB9G8J1ZzFTEF8ORHLLh4XV9iKq0nhOHSPP5gt2zOi42dy")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(publicRole))
                    .build();
            userRepository.saveAll(List.of(userAdmin, userMaigu,userPublic));
        };

    }
}

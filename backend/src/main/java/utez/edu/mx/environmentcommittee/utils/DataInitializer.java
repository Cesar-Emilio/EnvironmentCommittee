package utez.edu.mx.environmentcommittee.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import utez.edu.mx.environmentcommittee.modules.event.EventRepository;
import utez.edu.mx.environmentcommittee.modules.group.GroupRepository;
import utez.edu.mx.environmentcommittee.modules.role.Role;
import utez.edu.mx.environmentcommittee.modules.role.RoleRepository;
import utez.edu.mx.environmentcommittee.modules.type.TypeRepository;
import utez.edu.mx.environmentcommittee.modules.user.User;
import utez.edu.mx.environmentcommittee.modules.user.UserRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, GroupRepository groupRepository, EventRepository eventRepository, TypeRepository typeRepository) {
        return args -> {
            // Crear roles
            /*
             * ADMIN: Administrador del sistema
             * ADMINGROUP: Administrador de grupo
             * MEMBER: Miembro de grupo
             */
            if (roleRepository.findByName("ADMIN") == null) {
                roleRepository.save(new Role("ADMIN"));
            }
            if (roleRepository.findByName("ADMINGROUP") == null) {
                roleRepository.save(new Role("ADMINGROUP"));
            }
            if (roleRepository.findByName("MEMBER") == null) {
                roleRepository.save(new Role("MEMBER"));
            }

            // Crear administrador del sistema con rol ADMIN
            if (!userRepository.findByEmailOrUsername("admin@admin.com").isPresent()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setLastname("Admin");
                admin.setPhone("1234567890");
                admin.setEmail("admin@admin.com");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(roleRepository.findByName("ADMIN"));
                userRepository.save(admin);
            }
        };
    }
}
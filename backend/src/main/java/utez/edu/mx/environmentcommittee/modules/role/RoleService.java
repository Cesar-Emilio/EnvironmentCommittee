package utez.edu.mx.environmentcommittee.modules.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;

import java.sql.SQLException;
import java.util.List;

@Service
public class RoleService {
        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private CustomResponseEntity customResponseEntity;

        // BRING ALL ROLES
        @Transactional(readOnly = true)
        public ResponseEntity<?> findAll() {
            List<Role> roles = roleRepository.findAll();
            if (roles.isEmpty()) {
                return customResponseEntity.getOkResponse("No se encontraron roles", "OK", 200, null);
            }
            return customResponseEntity.getOkResponse("Roles encontrados", "OK", 200, roles);
        }

        // BRING ROLE BY ID
        @Transactional(readOnly = true)
        public ResponseEntity<?> findById(long id) {
            Role role = roleRepository.findById(id);
            if (role == null) {
                return customResponseEntity.get404Response();
            }
            return customResponseEntity.getOkResponse("Rol encontrado", "OK", 200, role);
        }

        // FIND ROLE BY NAME
        @Transactional(readOnly = true)
        public ResponseEntity<?> findByName(String name) {
            Role role = roleRepository.findByName(name);
            if (role == null) {
                return customResponseEntity.getOkResponse("No se encontró el rol con el nombre dado", "OK", 200, null);
            }
            return customResponseEntity.getOkResponse("Role found", "OK", 200, role);
        }

        // SAVE ROLE
        @Transactional(rollbackFor = {SQLException.class, Exception.class})
        public ResponseEntity<?> save(Role role) {
            try {
                roleRepository.save(role);
                return customResponseEntity.getOkResponse("Rol registrado correctamente", "CREATED", 201, null);
            } catch (Exception e) {
                e.printStackTrace();
                return customResponseEntity.get400Response();
            }
        }

        // UPDATE ROLE
        @Transactional(rollbackFor = {SQLException.class, Exception.class})
        public ResponseEntity<?> update(Role role) {
            Role existingRole = roleRepository.findById(role.getId());
            if (existingRole == null) {
                return customResponseEntity.get404Response();
            }
            try {
                roleRepository.save(role);
                return customResponseEntity.getOkResponse("Rol actualizado correctamente", "OK", 200, null);
            } catch (Exception e) {
                e.printStackTrace();
                return customResponseEntity.get400Response();
            }
        }

        // DELETE ROLE
        @Transactional(rollbackFor = {SQLException.class, Exception.class})
        public ResponseEntity<?> deleteById(long id) {
            Role role = roleRepository.findById(id);
            if (role == null) {
                return customResponseEntity.get404Response();
            }
            try {
                roleRepository.deleteById(id);
                return customResponseEntity.getOkResponse("Rol eliminado correctamente", "OK", 200, null);
            } catch (Exception e) {
                e.printStackTrace();
                return customResponseEntity.get400Response();
            }
        }
    }

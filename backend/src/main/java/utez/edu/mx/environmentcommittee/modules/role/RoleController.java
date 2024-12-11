package utez.edu.mx.environmentcommittee.modules.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
@CrossOrigin(origins = {"*"})
public class RoleController {
        @Autowired
        private RoleService roleService;

        // BRING ALL ROLES
        @GetMapping
        public ResponseEntity<?> findAll() {
            return roleService.findAll();
        }

        // BRING ROLE BY ID
        @GetMapping("/{id}")
        public ResponseEntity<?> findById(@PathVariable("id") long id) {
            return roleService.findById(id);
        }

        // FIND ROLE BY NAME
        @GetMapping("/name/{name}")
        public ResponseEntity<?> findByName(@PathVariable("name") String name) {
            return roleService.findByName(name);
        }

        // SAVE ROLE
        @PostMapping
        public ResponseEntity<?> save(@RequestBody Role role) {
            return roleService.save(role);
        }

        // UPDATE ROLE
        @PutMapping
        public ResponseEntity<?> update(@RequestBody Role role) {
            return roleService.update(role);
        }

        // DELETE ROLE
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
            return roleService.deleteById(id);
        }
    }

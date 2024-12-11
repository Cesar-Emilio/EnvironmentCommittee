package utez.edu.mx.environmentcommittee.modules.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"*"})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return userService.findAll();
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> findByRoleId(@PathVariable("roleId") long roleId) {
        return userService.findByRoleId(roleId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        return userService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
        return userService.deleteById(id);
    }
}

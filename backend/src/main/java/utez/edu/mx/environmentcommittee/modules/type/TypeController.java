package utez.edu.mx.environmentcommittee.modules.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/type")
@CrossOrigin(origins = {"*"})
public class TypeController {
    @Autowired
    private TypeService typeService;

    // BRING ALL TYPES
    @GetMapping
    public ResponseEntity<?> findAll() {
        return typeService.findAll();
    }

    // BRING TYPE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        return typeService.findById(id);
    }

    // SAVE TYPE
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Type type) {
        return typeService.save(type);
    }

    // UPDATE TYPE
    @PutMapping
    public ResponseEntity<?> update(@RequestBody Type type) {
        return typeService.update(type);
    }

    // DELETE TYPE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
        return typeService.deleteById(id);
    }
}

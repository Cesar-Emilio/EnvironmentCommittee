package utez.edu.mx.environmentcommittee.modules.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
@CrossOrigin(origins = {"*"})
public class GroupController {
    @Autowired
    private GroupService groupService;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return groupService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        return groupService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody GroupDTO group) {
        return groupService.save(group);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody GroupDTO group, @PathVariable("id") Long id) {
        return groupService.update(group, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
        return groupService.deleteById(id);
    }
}

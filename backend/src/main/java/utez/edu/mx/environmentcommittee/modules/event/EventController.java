package utez.edu.mx.environmentcommittee.modules.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/event")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
public class EventController {
    @Autowired
    private EventService eventService;

    // BRING ALL EVENTS
    @GetMapping
    public ResponseEntity<?> findAll() {
        return eventService.findAll();
    }

    // BRING EVENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") long id) {
        return eventService.findById(id);
    }

    // FIND EVENTS BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> findByStatus(@PathVariable("status") String status) {
        return eventService.findByStatus(status);
    }

    // SAVE EVENT
    @PostMapping
    public ResponseEntity<?> save(@RequestBody EventDTO dto) {
        return eventService.save(dto);
    }

    // UPDATE EVENT
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody EventDTO dto, @PathVariable("id") Long id) {
        return eventService.update(dto, id);
    }

    // DELETE EVENT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
        return eventService.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable("id") long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return eventService.updateStatus(id, status);
    }

}

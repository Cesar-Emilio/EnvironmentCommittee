package utez.edu.mx.environmentcommittee.modules.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;

import java.sql.SQLException;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CustomResponseEntity customResponseEntity;

    // BRING ALL EVENTS
    public ResponseEntity<?> findAll() {
        List<Event> events = eventRepository.findAll();
        return customResponseEntity.getOkResponse("Eventos encontrados", "OK", 200, events);
    }

    // BRING EVENT BY ID
    public ResponseEntity<?> findById(long id) {
        Event event = eventRepository.findById(id).orElse(null);
        return event == null
                ? customResponseEntity.get404Response()
                : customResponseEntity.getOkResponse("Evento encontrado", "OK", 200, event);
    }

    // FIND EVENTS BY STATUS
    public ResponseEntity<?> findByStatus(String status) {
        List<Event> events = eventRepository.findByStatus(status);
        return customResponseEntity.getOkResponse("Eventos encontrados", "OK", 200, events);
    }

    // SAVE EVENT
    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> save(Event event) {
        try {
            // Si el estado no se envía, asignar "Próximamente" como predeterminado
            if (event.getStatus() == null || event.getStatus().isEmpty()) {
                event.setStatus("Próximamente");
            }
            eventRepository.save(event);
            return customResponseEntity.getOkResponse("Evento registrado correctamente", "CREATED", 201, null);
        } catch (Exception e) {
            e.printStackTrace();
            return customResponseEntity.get400Response();
        }
    }


    // UPDATE EVENT
    public ResponseEntity<?> update(Event event) {
        if (!eventRepository.existsById(event.getId())) {
            return customResponseEntity.get404Response();
        }
        eventRepository.save(event);
        return customResponseEntity.getOkResponse("Evento actualizado correctamente", "OK", 200, null);
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> updateStatus(long id, String status) {
        Event existingEvent = eventRepository.findById(id).orElse(null);
        if (existingEvent == null) {
            return customResponseEntity.get404Response();
        }
        try {
            existingEvent.setStatus(status);
            eventRepository.save(existingEvent);
            return customResponseEntity.getOkResponse("Evento del estado actualizado correctamente", "OK", 200, null);
        } catch (Exception e) {
            e.printStackTrace();
            return customResponseEntity.get400Response();
        }
    }

    // DELETE EVENT
    public ResponseEntity<?> deleteById(long id) {
        if (!eventRepository.existsById(id)) {
            return customResponseEntity.get404Response();
        }
        eventRepository.deleteById(id);
        return customResponseEntity.getOkResponse("Evento eliminado correctamente", "OK", 200, null);
    }
}
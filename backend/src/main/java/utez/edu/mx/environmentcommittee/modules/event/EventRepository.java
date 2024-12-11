package utez.edu.mx.environmentcommittee.modules.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // FIND EVENTS BY STATUS
    List<Event> findByStatus(String status);
}

package utez.edu.mx.environmentcommittee.modules.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    // SPRING JPA PROPORCIONA AUTOMÁTICAMENTE:
    // - findAll()
    // - findById(Long id)
    // - save(Type type)
    // - deleteById(Long id)

    // MÉTODO ADICIONAL PARA ENCONTRAR TYPE POR NAME
    Type findByName(String name);
}

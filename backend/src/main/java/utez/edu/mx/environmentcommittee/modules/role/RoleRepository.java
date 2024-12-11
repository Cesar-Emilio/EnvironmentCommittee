package utez.edu.mx.environmentcommittee.modules.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // BRING ALL ROLES
    List<Role> findAll();

    // BRING ROLE BY ID
    Role findById(long id);

    // SAVE/UPDATE ROLE
    Role save(Role role);

    // DELETE ROLE
    @Modifying
    @Query(value = "DELETE FROM role WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") long id);

    // FIND ROLE BY NAME
    @Query(value = "SELECT * FROM role WHERE name = :name", nativeQuery = true)
    Role findByName(@Param("name") String name);
}

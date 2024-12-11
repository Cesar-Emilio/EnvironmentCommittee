package utez.edu.mx.environmentcommittee.modules.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.admin")
    List<Group> findAllWithAdmin();

    @Query("SELECT g FROM Group g WHERE g.admin.id = :adminId")
    Optional<Group> findByAdminId(@Param("adminId") long adminId);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.admin WHERE g.id = :id")
    Group findByIdWithAdmin(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM Group g WHERE g.id = :id")
    void deleteById(@Param("id") long id);

    @Query(value = "SELECT * FROM groupss WHERE municipality = :municipality", nativeQuery = true)
    List<Group> findByMunicipality(@Param("municipality") String municipality);
}

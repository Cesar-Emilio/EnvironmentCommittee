package utez.edu.mx.environmentcommittee.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.group LEFT JOIN FETCH u.role")
    List<User> findAllWithGroupAndRole();

    // BRING ALL USERS
    List<User> findAll();

    // BRING USER BY ID
    User findById(long id);

    // SAVE/UPDATE USER
    User save(User user);

    // DELETE USER
    @Modifying
    @Query(value = "DELETE FROM user WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") long id);

    // FIND USER BY ROLE ID
    @Query(value = "SELECT * FROM user WHERE role_id = :roleId", nativeQuery = true)
    List<User> findByRoleId(@Param("roleId") long roleId);

    // SEARCH USER BY EMAIL OR USERNAME AND PASSWORD
    @Query(value = "SELECT * FROM user WHERE password = :password AND (email = :username OR username = :username)", nativeQuery = true)
    User findByPasswordAndEmailOrUsername(
            @Param("password") String password,
            @Param("username") String username
    );

    // FIND USER BY USERNAME
    User findByUsername(String username);

    // FIND USER BY EMAIL OR USERNAME
    @Query("SELECT u FROM User u WHERE u.email = :user OR u.username = :user")
    Optional<User> findByEmailOrUsername(@Param("user") String username);
}

package com.example.system.Repositories;

import com.example.system.Entities.User;
import com.example.system.Projections.UserStatisticsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String userName);
    boolean existsByUsername(String username);
    List<User> findAllByEnabled(boolean enabled);
    User findByEmail(String email);
    User findByResetPasswordToken(String token);

    // security
    @Modifying
    @Query(value = "UPDATE `users` SET `reset_password_token`= null, `token_expiration_date` = null WHERE `token_expiration_date` < CURRENT_TIMESTAMP()", nativeQuery = true)
    void expireTokens();

    // Statistics

    @Query(value = "SELECT * from (SELECT u1.id as disabled_users_id from `users` as u1 WHERE u1.enabled = 0) as u1 JOIN (SELECT u1.id as enabled_users_id from `users` as u1 WHERE u1.enabled = 1) as u2 JOIN (SELECT u3.id as new_users_id FROM `users` as u3  WHERE EXTRACT(YEAR FROM u3.registration_date) = YEAR(CURRENT_DATE()) and EXTRACT(MONTH FROM u3.registration_date) = MONTH(CURRENT_DATE())) as u3", nativeQuery = true)
    List<UserStatisticsView> getUserStatistics();


}

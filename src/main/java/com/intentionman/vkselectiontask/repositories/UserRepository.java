package com.intentionman.vkselectiontask.repositories;


import com.intentionman.vkselectiontask.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
//    @Query("SELECT user from UserEntity user where user.login = ?1")
    Optional<UserEntity> findByUsername(String username);

}

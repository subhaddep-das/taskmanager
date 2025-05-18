package com.subhadeep_das.taskmanager.repository;

import com.subhadeep_das.taskmanager.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}

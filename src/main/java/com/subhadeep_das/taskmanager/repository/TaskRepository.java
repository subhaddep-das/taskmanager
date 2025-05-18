package com.subhadeep_das.taskmanager.repository;

import com.subhadeep_das.taskmanager.entity.Task;
import com.subhadeep_das.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    Optional<Task> findByIdAndUser(Long taskId, User user);
    Page<Task> findByUser(User user, Pageable pageable);
    List<Task> findByUserAndStatus(User user, String status);
    List<Task> findByUserAndPriority(User user, String priority);
    List<Task> findByUserAndDueDateBefore(User user, Date dueDate);
}

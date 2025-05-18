package com.subhadeep_das.taskmanager.service;

import com.subhadeep_das.taskmanager.entity.Task;
import com.subhadeep_das.taskmanager.entity.User;
import com.subhadeep_das.taskmanager.exception.TaskNotFoundException;
import com.subhadeep_das.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getTasksForUser(User user, int page, int size) {
        logger.info("Fetching tasks for user: {} | page: {}, size: {}", user.getUsername(), page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByUser(user, pageable);
        return taskPage.getContent();
    }

    public Page<Task> getTasksForUserWithPagination(User user, int page, int size) {
        logger.info("Fetching paginated tasks for user: {} | page: {}, size: {}", user.getUsername(), page, size);
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUser(user, pageable);
    }

    public Task createTask(Task task, User user) {
        task.setUser(user);
        logger.info("Creating task for user: {} | Title: {}", user.getUsername(), task.getTitle());
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task taskDetails, User user) {
        logger.info("Attempting to update task ID: {} for user: {}", taskId, user.getUsername());
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> {
                    logger.warn("Task not found for update | ID: {}, User: {}", taskId, user.getUsername());
                    return new TaskNotFoundException("Task not found for the given user");
                });

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setPriority(taskDetails.getPriority());
        task.setStatus(taskDetails.getStatus());

        logger.info("Task updated successfully | ID: {}, User: {}", taskId, user.getUsername());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, User user) {
        logger.info("Attempting to delete task ID: {} for user: {}", taskId, user.getUsername());
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> {
                    logger.warn("Task not found for deletion | ID: {}, User: {}", taskId, user.getUsername());
                    return new TaskNotFoundException("Task not found for the given user");
                });

        taskRepository.delete(task);
        logger.info("Task deleted successfully | ID: {}, User: {}", taskId, user.getUsername());
    }

    public List<Task> filterTasksByStatus(User user, String status) {
        logger.info("Filtering tasks by status: {} for user: {}", status, user.getUsername());
        return taskRepository.findByUserAndStatus(user, status);
    }

    public List<Task> filterTasksByPriority(User user, String priority) {
        logger.info("Filtering tasks by priority: {} for user: {}", priority, user.getUsername());
        return taskRepository.findByUserAndPriority(user, priority);
    }

    public List<Task> filterTasksByDueDate(User user, Date dueDate) {
        logger.info("Filtering tasks due before {} for user: {}", dueDate, user.getUsername());
        return taskRepository.findByUserAndDueDateBefore(user, dueDate);
    }
}

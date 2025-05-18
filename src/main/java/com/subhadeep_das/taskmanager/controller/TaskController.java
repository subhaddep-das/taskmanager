package com.subhadeep_das.taskmanager.controller;

import com.subhadeep_das.taskmanager.entity.Task;
import com.subhadeep_das.taskmanager.entity.User;
import com.subhadeep_das.taskmanager.exception.UserNotFoundException;
import com.subhadeep_das.taskmanager.repository.UserRepository;
import com.subhadeep_das.taskmanager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/view-tasks")
    public List<Task> getTasks(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        User user = getAuthenticatedUser();
        logger.info("Fetching tasks for user: {} | page: {}, size: {}", user.getUsername(), page, size);
        return taskService.getTasksForUser(user, page, size);
    }

    @PostMapping("/create-task")
    public Task createTask(@RequestBody Task task) {
        User user = getAuthenticatedUser();
        logger.info("Creating task for user: {} | Title: {}", user.getUsername(), task.getTitle());
        return taskService.createTask(task, user);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        User user = getAuthenticatedUser();
        logger.info("Updating task ID: {} for user: {}", taskId, user.getUsername());
        return taskService.updateTask(taskId, taskDetails, user);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        User user = getAuthenticatedUser();
        logger.info("Deleting task ID: {} for user: {}", taskId, user.getUsername());
        taskService.deleteTask(taskId, user);
    }

    @GetMapping("/status/{status}")
    public List<Task> getTasksByStatus(@PathVariable String status) {
        User user = getAuthenticatedUser();
        logger.info("Filtering tasks by status '{}' for user: {}", status, user.getUsername());
        return taskService.filterTasksByStatus(user, status);
    }

    @GetMapping("/priority/{priority}")
    public List<Task> getTasksByPriority(@PathVariable String priority) {
        User user = getAuthenticatedUser();
        logger.info("Filtering tasks by priority '{}' for user: {}", priority, user.getUsername());
        return taskService.filterTasksByPriority(user, priority);
    }

    @GetMapping("/due-date/{dueDate}")
    public List<Task> getTasksByDueDate(@PathVariable Date dueDate) {
        User user = getAuthenticatedUser();
        logger.info("Filtering tasks by due date '{}' for user: {}", dueDate, user.getUsername());
        return taskService.filterTasksByDueDate(user, dueDate);
    }

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    logger.error("User not found for username/email: {}", username);
                    return new UserNotFoundException("Authenticated user not found");
                });
    }
}

package com.polp.polp.Controller;

import com.polp.polp.Model.Task;
import com.polp.polp.Model.User;
import com.polp.polp.Repo.TaskRepository;
import com.polp.polp.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/tasks")
    public String viewAllTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "admin-tasks";
    }

    @GetMapping("/tasks/create")
    public String showCreateForm() {
        return "task-create";
    }

    @PostMapping("/tasks/create")
    public String createTask(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                             @RequestParam String assignedUsername) {
        User assignedUser = userRepository.findByUsername(assignedUsername).orElseThrow();
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setAssignedTo(assignedUser);
        taskRepository.save(task);
        return "redirect:/admin/tasks";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }
}

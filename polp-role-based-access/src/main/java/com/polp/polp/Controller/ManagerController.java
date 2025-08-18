package com.polp.polp.Controller;


import com.polp.polp.Repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/team-tasks")
    public String viewTeamTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "manager-team-tasks";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("tasks", taskRepository.findAll().stream()
                .filter(task -> task.getReport() != null && !task.getReport().isBlank())
                .toList());
        return "manager-reports";
    }

}

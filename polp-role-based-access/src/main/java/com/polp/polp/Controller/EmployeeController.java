package com.polp.polp.Controller;

import com.polp.polp.Model.Task;
import com.polp.polp.Model.User;
import com.polp.polp.Repo.TaskRepository;
import com.polp.polp.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/my-tasks")
    public String myTasks(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("tasks", taskRepository.findByAssignedTo(user));
        return "employee-my-tasks";
    }
    @PostMapping("/submit-report")
    public String submitReport(@RequestParam Long taskId,
                               @RequestParam String report,
                               Principal principal) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        // Optional: Security check - make sure the logged-in user is assigned to this task
        if (!task.getAssignedTo().getUsername().equals(principal.getName())) {
            return "error/403"; // Or redirect back with error
        }

        task.setReport(report);
        taskRepository.save(task);
        return "redirect:/employee/my-tasks";
    }



    @GetMapping("/reports")
    public String viewReports(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("tasks", taskRepository.findByAssignedTo(user));
        return "employee-reports";
    }

//    @GetMapping("/report/submit")
//    public String showReportForm(@RequestParam Long taskId, Model model, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
//        Task task = taskRepository.findById(taskId).orElseThrow();
//
//
//        if (!task.getAssignedTo().getId().equals(user.getId())) {
//            return "error/403";
//        }
//
//        model.addAttribute("task", task);
//        return "employee-submit-report";
//    }


}

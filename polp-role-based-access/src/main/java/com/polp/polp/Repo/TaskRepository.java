package com.polp.polp.Repo;

import com.polp.polp.Model.Task;
import com.polp.polp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
}

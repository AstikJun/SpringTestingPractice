package com.example.springrestpractice.repos;

import com.example.springrestpractice.records.Task;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository {
List<Task> findAll();
void save (Task task);
Optional<Task> findById(UUID id);
}

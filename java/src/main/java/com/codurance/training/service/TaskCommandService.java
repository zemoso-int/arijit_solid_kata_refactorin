package com.codurance.training.service;

import com.codurance.training.models.ProjectTask;

public interface TaskCommandService {
    void add(String commandLine);
    void addTask(ProjectTask projectTask);
    void addProject(String name);
}

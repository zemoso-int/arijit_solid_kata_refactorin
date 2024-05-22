package com.codurance.training.service;

import com.codurance.training.models.ProjectTask;

public interface TaskActionService {
    void add(String commandLine);
    void addTask(ProjectTask projectTask);
    void show();
    void addProject(String name);
    void check(String idString);
    void uncheck(String idString);
    void setDone(String idString, boolean done);
    void help();
    void error(String command);
}

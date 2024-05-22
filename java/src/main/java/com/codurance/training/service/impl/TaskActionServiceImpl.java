package com.codurance.training.service.impl;

import com.codurance.training.models.ProjectTask;
import com.codurance.training.service.TaskActionService;
import com.codurance.training.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class TaskActionServiceImpl implements TaskActionService {

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private long lastId =0;
    @Override
    public void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            addProject(subcommandRest[1]);
        } else if (subcommand.equals("task")) {
            String[] projectTask = subcommandRest[1].split(" ", 2);
            ProjectTask projTask = new ProjectTask();
            projTask.setProject(projectTask[0]);
            projTask.setTaskDescription(projectTask[1]);

            addTask(projTask);
        }

    }

    @Override
    public void addTask(ProjectTask projectTask) {
        List<Task> projectTasks = tasks.get(projectTask.getProject());
        if (projectTasks == null) {
            out.printf("Could not find a project with the name \"%s\".", projectTask.getProject());
            out.println();
            return;
        }
        projectTasks.add(new Task(nextId(), projectTask.getTaskDescription(), false));

    }

    @Override
    public void show() {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }

    }

    @Override
    public void addProject(String name) {
        tasks.put(name, new ArrayList<Task>());
    }

    @Override
    public void check(String idString) {
        setDone(idString, true);
    }

    @Override
    public void uncheck(String idString) {
        setDone(idString, false);
    }

    @Override
    public void setDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    task.setDone(done);
                    return;
                }
            }
        }
        out.printf("Could not find a task with an ID of %d.", id);
        out.println();
    }

    @Override
    public void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println();
    }

    @Override
    public void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }
    public long nextId() {
        return ++lastId;
    }
}

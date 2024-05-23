package com.codurance.training.service.impl;

import com.codurance.training.models.ProjectTask;
import com.codurance.training.service.TaskCommandService;
import com.codurance.training.service.TaskDisplayService;
import com.codurance.training.service.TaskHelpService;
import com.codurance.training.service.TaskStatusService;
import com.codurance.training.tasks.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class TaskActionServiceImpl implements TaskHelpService, TaskCommandService, TaskDisplayService, TaskStatusService {

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
    public void show(String command) {
        String[] subcommandRest = command.split(" ", 3);
        if(subcommandRest[1].equals("project")) {
            for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
                out.println(project.getKey());
                for (Task task : project.getValue()) {
                    System.out.println(task.isDone() ? 'x' : " " + task.getId() + " " + task.getDescription() + " " + task.getDeadline() + " " + task.getCustomizableId());

                }
                out.println();
            }
            out.println();
        }else if(subcommandRest[1].equals("deadline")){
            showByDeadline();
        } else if (subcommandRest[1].equals("date")) {
            showByDate(subcommandRest[2]);
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

    @Override
    public void deadline(String command) {
        try {
            String[] subCommand = command.split(" ", 2);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date deadlineDate = sdf.parse(subCommand[1]);
            int id = Integer.parseInt(subCommand[0]);
            for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
                for (Task task : project.getValue()) {
                    if (task.getId() == id) {
                        task.setDeadline(deadlineDate);
                        return;
                    }
                }
            }
        }catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void today() {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (DateUtils.isSameDay(new Date(),task.getDeadline())) {
                    System.out.println(task.isDone()?'x':" "+task.getId()+" "+task.getDescription()+" "+task.getDeadline());
                }
            }
            out.println();
        }

    }

    @Override
    public void delete(String command) {
        int id = Integer.parseInt(command);
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            List<Task> taskList = project.getValue();
            taskList.removeIf(task -> task.getId() == id);
        }
    }

    @Override
    public void showByDeadline() {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if(task.getDeadline()!=null && DateUtils.isSameDay(new Date(), task.getDeadline())) {
                        System.out.println(task.isDone() ? 'x' : " " + task.getId() + " " + task.getDescription() + " " + task.getDeadline() + " " + task.getCustomizableId());
                    }

            }
            out.println();
        }
    }

    @Override
    public void showByDate(String s) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(s);
            for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
                for (Task task : project.getValue()) {
                    if (!date.after(task.getDeadline() )) {
                        System.out.println(task.isDone() ? 'x' : " " + task.getId() + " " + task.getDescription() + " " + task.getDeadline()+" "+task.getCustomizableId());
                    }
                }
                out.println();
            }
        }catch (ParseException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void customize(String s) {
        String[] subCommand = s.split(" ", 2);
        Pattern p = Pattern.compile(
                "[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        int id = Integer.parseInt(subCommand[0]);
        if((!p.matcher(subCommand[1]).find())&& !StringUtils.containsWhitespace(subCommand[1])) {
            for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
                for (Task task : project.getValue()) {
                    if (task.getId() == id) {
                        task.setCustomizableId(subCommand[1]);
                        return;
                    }
                }
            }
        }

    }

    public long nextId() {
        return ++lastId;
    }
}

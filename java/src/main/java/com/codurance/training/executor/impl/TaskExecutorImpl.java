package com.codurance.training.executor.impl;

import com.codurance.training.executor.TaskExecutor;
import com.codurance.training.service.impl.TaskActionServiceImpl;

public class TaskExecutorImpl implements TaskExecutor {
    @Override
    public void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        TaskActionServiceImpl taskActionService = new TaskActionServiceImpl();
        String command = commandRest[0];
        switch (command) {
            case "show":
                taskActionService.show();
                break;
            case "add":
                taskActionService.add(commandRest[1]);
                break;
            case "check":
                taskActionService.check(commandRest[1]);
                break;
            case "uncheck":
                taskActionService.uncheck(commandRest[1]);
                break;
            case "help":
                taskActionService.help();
                break;
            default:
                taskActionService.error(command);
                break;
        }
    }
}

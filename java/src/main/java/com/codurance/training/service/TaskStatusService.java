package com.codurance.training.service;

public interface TaskStatusService {
    void check(String idString);
    void uncheck(String idString);
    void setDone(String idString, boolean done);
}

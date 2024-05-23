package com.codurance.training.service;

public interface TaskDisplayService {
    void show();
    void show (String command);
    void showByDeadline();
    void showByDate(String s);
    void today();
}

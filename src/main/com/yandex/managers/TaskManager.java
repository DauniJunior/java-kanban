package com.yandex.managers;

import com.yandex.models.Epic;
import com.yandex.models.Subtask;
import com.yandex.models.Task;
import java.util.List;
import com.yandex.models.*;

public interface TaskManager {
    // Методы для задач
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    int addTask(Task task);

    void updateTask(Task updatedTask);

    void deleteTaskById(int id);

    // Методы для эпиков
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    int addEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void deleteEpicById(int id);

    // Методы для подзадач
    List<Subtask> getAllSubTasks();

    void deleteAllSubTasks();

    Subtask getSubTaskById(int id);

    int addSubTask(Subtask subtask);

    void updateSubTask(Subtask subtask);

    void deleteSubTaskById(int id);

    // Дополнительные методы
    List<Subtask> getSubTasksByEpicId(int epicId);

    List<Task> getHistory();
}

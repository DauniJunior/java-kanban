package com.yandex.managers;

import com.yandex.models.Task;
import java.util.List;
import com.yandex.models.*;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
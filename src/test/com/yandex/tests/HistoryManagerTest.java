package com.yandex.tests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yandex.managers.HistoryManager;
import com.yandex.managers.InMemoryHistoryManager;
import com.yandex.models.Epic;
import com.yandex.models.Task;
import java.util.List;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddAndRetrieveTasks() {
        Task task = new Task("Test", "Description");
        task.setId(1);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        Assertions.assertEquals(1, history.get(0).getId());
    }

    @Test
    void shouldRemoveDuplicates() {
        Task task = new Task("Test", "Description");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task); // Дубликат
        historyManager.add(task); // Дубликат

        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task("Test", "Description");
        task.setId(1);

        historyManager.add(task);
        historyManager.remove(1);

        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldMaintainInsertionOrder() {
        Task task1 = new Task("Task1", "Desc");
        Task task2 = new Task("Task2", "Desc");
        task1.setId(1); task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.get(0).getId());
        Assertions.assertEquals(2, history.get(1).getId());
    }

    @Test
    void shouldHandleMixedTaskTypes() {
        Task task = new Task("java.yandex.models.Task", "Desc");
        Epic epic = new Epic("java.yandex.models.Epic", "Desc");
        task.setId(1); epic.setId(2);

        historyManager.add(task);
        historyManager.add(epic);

        Assertions.assertEquals(2, historyManager.getHistory().size());
    }
}
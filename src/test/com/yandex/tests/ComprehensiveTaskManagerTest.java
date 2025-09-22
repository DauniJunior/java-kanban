package com.yandex.tests;

import com.yandex.managers.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.yandex.models.Epic;
import com.yandex.models.Subtask;
import com.yandex.models.Task;
import java.util.List;

class ComprehensiveTaskManagerTest {
    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
    }

    // Проверка равенства java.yandex.models.Task по id
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("java.yandex.models.Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("java.yandex.models.Task 2", "Description 2");
        task2.setId(1);

        Assertions.assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    // Проверка равенства наследников
    @Test
    void taskSubclassesWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("java.yandex.models.Epic 1", "Description 1");
        epic1.setId(1);
        Epic epic2 = new Epic("java.yandex.models.Epic 2", "Description 2");
        epic2.setId(1);

        Subtask subtask1 = new Subtask("java.yandex.models.Subtask 1", "Description 1", 1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("java.yandex.models.Subtask 2", "Description 2", 1);
        subtask2.setId(2);

        Assertions.assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
        Assertions.assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    // java.yandex.models.Epic не может быть подзадачей самого себя
    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        Epic epic = new Epic("java.yandex.models.Epic", "Description");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("java.yandex.models.Subtask", "Description", epicId);
        subtask.setId(epicId);

        Assertions.assertEquals(-1, manager.addSubTask(subtask),
                "Эпик не должен быть добавлен в качестве своей подзадачи");
    }

    // java.yandex.models.Subtask не может быть своим эпиком
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("java.yandex.models.Epic", "Description");
        int epicId = manager.addEpic(epic);
        assertNotEquals(-1, epicId, "Эпик не был добавлен");

        // Добавляем нормальную подзадачу
        Subtask subtask = new Subtask("java.yandex.models.Subtask", "Description", epicId);
        int subtaskId = manager.addSubTask(subtask);
        assertNotEquals(-1, subtaskId, "Подзадача не была добавлена");

        // Пытаемся сделать её своим эпиком
        Subtask invalidSubtask = new Subtask("Invalid", "Description", subtaskId);
        invalidSubtask.setId(subtaskId);

        assertThrows(IllegalArgumentException.class,
                () -> manager.updateSubTask(invalidSubtask),
                "Подзадача не должна быть своим эпиком");
    }

    // Проверка утилитного класса
    @Test
    void managersReturnInitializedInstances() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }

    // Добавление разных типов задач
    @Test
    void shouldAddAllTaskTypes() {
        // Добавляем эпик (обязательное условие для подзадачи)
        Epic epic = new Epic("java.yandex.models.Epic", "Description");
        int epicId = manager.addEpic(epic);
        assertNotEquals(-1, epicId, "Эпик не был добавлен");

        // Добавляем обычную задачу
        Task task = new Task("java.yandex.models.Task", "Description");
        int taskId = manager.addTask(task);
        assertNotEquals(-1, taskId, "Задача не была добавлена");

        // Добавляем подзадачу
        Subtask subtask = new Subtask("java.yandex.models.Subtask", "Description", epicId);
        int subtaskId = manager.addSubTask(subtask);
        assertNotEquals(-1, subtaskId, "Подзадача не была добавлена");

        // Проверяем, что все задачи доступны через менеджер
        Assertions.assertNotNull(manager.getTaskById(taskId), "Задача не найдена");
        Assertions.assertNotNull(manager.getEpicById(epicId), "Эпик не найден");
        Assertions.assertNotNull(manager.getSubTaskById(subtaskId), "Подзадача не найдена");
    }

    // Проверка конфликтов ID
    @Test
    void tasksWithAssignedAndGeneratedIdsDoNotConflict() {

        Task task1 = new Task("java.yandex.models.Task 1", "Description");
        task1.setId(100);
        manager.addTask(task1);

        Task task2 = new Task("java.yandex.models.Task 2", "Description");
        int task2Id = manager.addTask(task2);

        Assertions.assertNotEquals(task1.getId(), task2Id, "ID задач не должны конфликтовать");
        Assertions.assertNotNull(manager.getTaskById(task1.getId()), "Задача с заданным id не найдена");
        Assertions.assertNotNull(manager.getTaskById(task2Id), "Задача с сгенерированным id не найдена");
    }

    // Неизменность задач при добавлении
    @Test
    void taskShouldRemainUnchangedWhenAddedToManager() {
        Task originalTask = new Task("Original", "Description", Task.Status.IN_PROGRESS);
        originalTask.setId(1);

        // Создаем копию перед добавлением
        Task taskToAdd = originalTask.copy();
        int taskId = manager.addTask(taskToAdd);

        Task retrievedTask = manager.getTaskById(taskId);

        Assertions.assertEquals(originalTask.getName(), retrievedTask.getName(), "Имя задачи изменилось");
        Assertions.assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Описание задачи изменилось");
        Assertions.assertEquals(originalTask.getStatus(), retrievedTask.getStatus(), "Статус задачи изменился");
        Assertions.assertEquals(originalTask.getId(), retrievedTask.getId(), "ID задачи изменился");
    }

    // История сохраняет состояние
    @Test
    void historyShouldPreserveTaskState() {
        Task task = new Task("java.yandex.models.Task", "Desc", Task.Status.NEW);
        task.setId(1);
        historyManager.add(task);
        task.setName("Modified");
        Assertions.assertEquals("java.yandex.models.Task", historyManager.getHistory().get(0).getName());
    }

    @Test
    void historyShouldNotContainDuplicates() {
        Task task = new Task("java.yandex.models.Task", "Desc");
        int taskId = manager.addTask(task);

        manager.getTaskById(taskId);
        manager.getTaskById(taskId); // Дубликат
        manager.getTaskById(taskId); // Дубликат

        Assertions.assertEquals(1, manager.getHistory().size());
    }

    @Test
    void historyShouldMaintainOrder() {
        Task task1 = new Task("Task1", "Desc");
        Task task2 = new Task("Task2", "Desc");
        int id1 = manager.addTask(task1);
        int id2 = manager.addTask(task2);

        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id1); // Повторный просмотр

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        Assertions.assertEquals(id2, history.get(0).getId()); // Первый просмотр
        Assertions.assertEquals(id1, history.get(1).getId()); // Последний просмотр
    }

    @Test
    void historyShouldBeCleanedOnTaskDeletion() {
        Task task = new Task("java.yandex.models.Task", "Desc");
        int taskId = manager.addTask(task);

        manager.getTaskById(taskId);
        manager.deleteTaskById(taskId);

        Assertions.assertTrue(manager.getHistory().isEmpty());
    }

    // Тесты для целостности данных
    @Test
    void shouldRemoveSubtasksWhenEpicDeleted() {
        Epic epic = new Epic("java.yandex.models.Epic", "Desc");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", epicId);
        int subId = manager.addSubTask(subtask);

        manager.deleteEpicById(epicId);

        assertNull(manager.getSubTaskById(subId));
        Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
    }

    @Test
    void shouldHandleInvalidEpicIdInSubtask() {
        // Пытаемся создать подзадачу с несуществующим эпиком
        Subtask invalidSubtask = new Subtask("Invalid", "Desc", 999);

        Assertions.assertEquals(-1, manager.addSubTask(invalidSubtask));
    }

    @Test
    void shouldDeleteAllTasks() {
        // Добавляем задачи
        Task task1 = new Task("java.yandex.models.Task 1", "Description 1");
        Task task2 = new Task("java.yandex.models.Task 2", "Description 2");
        manager.addTask(task1);
        manager.addTask(task2);

        // Проверяем, что задачи добавлены
        Assertions.assertEquals(2, manager.getAllTasks().size());

        // Удаляем все задачи
        manager.deleteAllTasks();

        // Проверяем, что список задач пуст
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteAllEpics() {
        // Добавляем эпики
        Epic epic1 = new Epic("java.yandex.models.Epic 1", "Description 1");
        Epic epic2 = new Epic("java.yandex.models.Epic 2", "Description 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // Проверяем, что эпики добавлены
        Assertions.assertEquals(2, manager.getAllEpics().size());

        // Удаляем все эпики
        manager.deleteAllEpics();

        // Проверяем, что список эпиков пуст
        Assertions.assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void shouldDeleteAllSubtasks() {
        // Сначала создаем эпик
        Epic epic = new Epic("java.yandex.models.Epic", "Description");
        int epicId = manager.addEpic(epic);

        // Добавляем подзадачи
        Subtask subtask1 = new Subtask("java.yandex.models.Subtask 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("java.yandex.models.Subtask 2", "Description 2", epicId);
        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);

        // Проверяем, что подзадачи добавлены
        Assertions.assertEquals(2, manager.getAllSubTasks().size());

        // Удаляем все подзадачи
        manager.deleteAllSubTasks();

        // Проверяем, что список подзадач пуст
        Assertions.assertTrue(manager.getAllSubTasks().isEmpty());

        // Проверяем, что эпик остался
        assertNotNull(manager.getEpicById(epicId));
        // Проверяем, что у эпика нет подзадач
        Assertions.assertTrue(manager.getSubTasksByEpicId(epicId).isEmpty());
    }

    @Test
    void shouldDeleteAllEpicsWithSubtasks() {
        // Создаем эпик с подзадачами
        Epic epic = new Epic("java.yandex.models.Epic", "Description");
        int epicId = manager.addEpic(epic);

        Subtask subtask1 = new Subtask("java.yandex.models.Subtask 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("java.yandex.models.Subtask 2", "Description 2", epicId);
        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);

        // Проверяем, что всё добавлено
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(2, manager.getAllSubTasks().size());

        // Удаляем все эпики (должны удалиться и подзадачи)
        manager.deleteAllEpics();

        // Проверяем, что эпики и подзадачи удалены
        Assertions.assertTrue(manager.getAllEpics().isEmpty());
        Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
    }
}

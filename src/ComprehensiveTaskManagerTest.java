import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ComprehensiveTaskManagerTest {
    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
    }

    // Проверка равенства Task по id
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    // Проверка равенства наследников
    @Test
    void taskSubclassesWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Description 2");
        epic2.setId(1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1);
        subtask2.setId(2);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    // Epic не может быть подзадачей самого себя
    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        subtask.setId(epicId);

        assertEquals(-1, manager.addSubTask(subtask),
                "Эпик не должен быть добавлен в качестве своей подзадачи");
    }

    // Subtask не может быть своим эпиком
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.addEpic(epic);
        assertNotEquals(-1, epicId, "Эпик не был добавлен");

        // Добавляем нормальную подзадачу
        Subtask subtask = new Subtask("Subtask", "Description", epicId);
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
        Epic epic = new Epic("Epic", "Description");
        int epicId = manager.addEpic(epic);
        assertNotEquals(-1, epicId, "Эпик не был добавлен");

        // Добавляем обычную задачу
        Task task = new Task("Task", "Description");
        int taskId = manager.addTask(task);
        assertNotEquals(-1, taskId, "Задача не была добавлена");

        // Добавляем подзадачу
        Subtask subtask = new Subtask("Subtask", "Description", epicId);
        int subtaskId = manager.addSubTask(subtask);
        assertNotEquals(-1, subtaskId, "Подзадача не была добавлена");

        // Проверяем, что все задачи доступны через менеджер
        assertNotNull(manager.getTaskById(taskId), "Задача не найдена");
        assertNotNull(manager.getEpicById(epicId), "Эпик не найден");
        assertNotNull(manager.getSubTaskById(subtaskId), "Подзадача не найдена");
    }

    // Проверка конфликтов ID
    @Test
    void tasksWithAssignedAndGeneratedIdsDoNotConflict() {

        Task task1 = new Task("Task 1", "Description");
        task1.setId(100);
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Description");
        int task2Id = manager.addTask(task2);

        assertNotEquals(task1.getId(), task2Id, "ID задач не должны конфликтовать");
        assertNotNull(manager.getTaskById(task1.getId()), "Задача с заданным id не найдена");
        assertNotNull(manager.getTaskById(task2Id), "Задача с сгенерированным id не найдена");
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

        assertEquals(originalTask.getName(), retrievedTask.getName(), "Имя задачи изменилось");
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Описание задачи изменилось");
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus(), "Статус задачи изменился");
        assertEquals(originalTask.getId(), retrievedTask.getId(), "ID задачи изменился");
    }

    // История сохраняет состояние
    @Test
    void historyShouldPreserveTaskState() {
        Task task = new Task("Task", "Desc", Task.Status.NEW);
        task.setId(1);
        historyManager.add(task);
        task.setName("Modified");
        assertEquals("Task", historyManager.getHistory().get(0).getName());
    }
}

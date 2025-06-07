import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private Integer nextId = 1;
    //private List<Task> history = new ArrayList<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public int addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Методы для эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic != null) {
            existingEpic.setName(updatedEpic.getName());
            existingEpic.setDescription(updatedEpic.getDescription());
            updateEpicStatus(existingEpic.getId());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            List<Integer> subtaskIdsToRemove = epics.get(id).getSubtaskIds();
            for (Integer subtaskId : subtaskIdsToRemove) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    // Методы для подзадач
    @Override
    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public int addSubTask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.epicId)) {
            return -1;
        }
        if (subtask.getId() != null && subtask.getId() == subtask.getEpicId()) {
            return -1;
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.epicId).addSubtaskId(subtask.getId());
        updateEpicStatus(subtask.epicId);
        return subtask.getId();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача не существует");
        }
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим эпиком");
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Указанный эпик не существует");
        }

        Subtask existingSubtask = subtasks.get(subtask.getId());
        existingSubtask.setName(subtask.getName());
        existingSubtask.setDescription(subtask.getDescription());
        existingSubtask.status = subtask.status;
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteSubTaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            epics.get(subtask.epicId).removeSubtaskId(id);
            updateEpicStatus(subtask.epicId);
        }
    }

    @Override
    public List<Subtask> getSubTasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }


    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = getSubTasksByEpicId(epicId);
        if (epicSubtasks.isEmpty()) {
            epic.status = Task.Status.NEW;
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Subtask subtask : epicSubtasks) {
            if (subtask.status != Task.Status.DONE) {
                allDone = false;
            }
            if (subtask.status != Task.Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.status = Task.Status.DONE;
        } else if (allNew) {
            epic.status = Task.Status.NEW;
        } else {
            epic.status = Task.Status.IN_PROGRESS;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}

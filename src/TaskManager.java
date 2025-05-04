import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;


    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public int addTask(Task task) {
        task.id = nextId++;
        tasks.put(task.id, task);
        return task.id;
    }

    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.id)) {
            tasks.put(updatedTask.id, updatedTask);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Методы для эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public int addEpic(Epic epic) {
        epic.id = nextId++;
        epics.put(epic.id, epic);
        return epic.id;
    }

    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.id);
        if (existingEpic != null) {
            existingEpic.name = updatedEpic.name;
            existingEpic.description = updatedEpic.description;
            updateEpicStatus(existingEpic.id);
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            List<Integer> subtaskIdsToRemove = new ArrayList<>();
            for (Subtask subtask : subtasks.values()) {
                if (subtask.epicId == id) {
                    subtaskIdsToRemove.add(subtask.id);
                }
            }

            for (Integer subtaskId : subtaskIdsToRemove) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    // Методы для подзадач
    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubTasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.id);
        }
    }

    public Subtask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    public int addSubTask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.epicId)) {
            return -1;
        }
        subtask.id = nextId++;
        subtasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.epicId);
        return subtask.id;
    }

    public void updateSubTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.id)) {
            subtasks.put(subtask.id, subtask);
            updateEpicStatus(subtask.epicId);
        }
    }

    public void deleteSubTaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            updateEpicStatus(subtask.epicId);
        }
    }


    public List<Subtask> getSubTasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.epicId == epicId) {
                result.add(subtask);
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
}

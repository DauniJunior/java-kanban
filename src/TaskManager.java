import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private Integer nextId = 1;


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
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
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
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic != null) {
            existingEpic.setName(updatedEpic.getName());
            existingEpic.setDescription(updatedEpic.getDescription());
            updateEpicStatus(existingEpic.getId());
        }
    }

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
    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubTasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    public Subtask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    public int addSubTask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.epicId)) {
            return -1;
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.epicId).addSubtaskId(subtask.getId());
        updateEpicStatus(subtask.epicId);
        return subtask.getId();
    }

    public void updateSubTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask existingSubtask = subtasks.get(subtask.getId());
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            existingSubtask.status = subtask.status;
            updateEpicStatus(subtask.epicId);
        }
    }

    public void deleteSubTaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            epics.get(subtask.epicId).removeSubtaskId(id);
            updateEpicStatus(subtask.epicId);
        }
    }


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
}

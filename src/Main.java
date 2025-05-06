public class Main {


    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);
        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.toString());
        }

        Task task3 = new Task("Новая задача 2", "Новое описание задачи 2", Task.Status.IN_PROGRESS);
        task2.name = task3.name;
        task2.description = task3.description;
        task2.status = task3.status;
        manager.updateTask(task2);
        System.out.println("\nОбновленный список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.toString());
        }

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        System.out.println("\nСписок эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.toString());
        }

        Epic epic3 = new Epic("Новый эпик 2", "Новое описание эпика 2", task1.getId());
        epic2.name = epic3.name;
        epic2.description = epic3.description;
        manager.updateEpic(epic2);
        System.out.println("\nНовый список эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.toString());
        }

        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", epic2.getId());
        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);
        manager.addSubTask(subtask3);
        System.out.println("\nСписок подзадач:");
        for (Subtask subtask : manager.getAllSubTasks()) {
            System.out.println(subtask.toString());
        }
        System.out.println();
        System.out.println("Эпик 1 статус: " + manager.getEpicById(epic1.getId()).getStatus());
        System.out.println("Эпик 2 статус: " + manager.getEpicById(epic2.getId()).getStatus());

        Subtask subtask1_2 = new Subtask("Новая подзадача 1.1", "Новое описание подзадачи 1.1", Task.Status.DONE, epic1.getId());
        Subtask subtask2_2 = new Subtask("Новая подзадача 1.2", "Новое описание подзадачи 1.2", Task.Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3_2 = new Subtask("Новая подзадача 2.1", "Новое описание подзадачи 2.1", Task.Status.DONE, epic2.getId());
        subtask1_2.setId(subtask1.getId());
        subtask2_2.setId(subtask2.getId());
        subtask3_2.setId(subtask3.getId());
        manager.updateSubTask(subtask1_2);
        manager.updateSubTask(subtask2_2);
        manager.updateSubTask(subtask3_2);
        System.out.println("\nОбновленный список подзадач:");
        for (Subtask subtask : manager.getAllSubTasks()) {
            System.out.println(subtask.toString());
        }


        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());
        manager.deleteSubTaskById(subtask3.getId());

        System.out.println("\nПосле удаления:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("\nСписок эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.toString());
        }

        System.out.println("\nСписок подзадач:");
        for (Subtask subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }
    }
}

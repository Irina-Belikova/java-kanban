import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager tm = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(tm);
    Gson gson = server.getGson();

    @BeforeEach
    public void beforeEach() {
        tm.clearTasks();
        tm.clearSubtasks();
        tm.clearEpics();
        server.start();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    //проверка работы сервера с задачами
    @Test
    public void testServerForTask() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            Task task = new Task("задача-1", "описание зд-1",
                    LocalDateTime.of(2025, 1, 15, 12, 0), Duration.ofMinutes(30));
            String taskJson = gson.toJson(task);

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response1.statusCode(), "Код ответа не соответствует ожидаемому.");

            List<Task> tasks = tm.getAllTasks();

            assertNotNull(tasks, "Задачи не возвращаются");
            assertEquals(1, tasks.size(), "Некорректное количество задач");
            assertEquals("задача-1", tasks.getFirst().getName(), "Некорректное имя задачи");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response2.statusCode(), "Код ответа не соответствует ожидаемому.");
            assertNotNull(response2.body(), "Возвращается пустое тело ответа.");

            Task upTask = new Task(1, "задача-1", "новое описание зд-1", TaskStatus.DONE,
                    LocalDateTime.of(2025, 1, 15, 12, 0), Duration.ofMinutes(30));
            String upTaskJson = gson.toJson(upTask);
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/1"))
                    .POST(HttpRequest.BodyPublishers.ofString(upTaskJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response3.statusCode(), "Код ответа не соответствует ожидаемому.");

            Task updatedTask = tm.getTaskById(1);

            assertEquals("новое описание зд-1", updatedTask.getDescription(),
                    "Описание задачи в менеджере не обновилось.");

            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response4.statusCode(), "Код ответа не соответствует ожидаемому.");

            List<Task> tasks1 = tm.getAllTasks();

            assertEquals(0, tasks1.size(), "Задача из менеджера не удалилась.");
        }
    }

    //проверка работы сервера с подзадачами и эпиками на добавление и удаление и получение
    @Test
    public void testServerForEpicAndSubtask() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {

            Epic epic = new Epic("эпик-1", "описание эпика-1");
            String epicJson = gson.toJson(epic);
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response1.statusCode(), "Код ответа не соответствует ожидаемому.");

            Subtask subtask = new Subtask("подзадача-1", "описание пзд-1",
                    LocalDateTime.of(2025, 1, 1, 14, 0, 0, 0), Duration.ofMinutes(30), 1);
            String subtaskJson = gson.toJson(subtask);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response2.statusCode(), "Код ответа не соответствует ожидаемому.");

            List<Epic> epics = tm.getAllEpics();
            List<Subtask> subtasks = tm.getAllSubtasks();

            assertNotNull(epics, "Эпики не возвращаются");
            assertNotNull(subtasks, "Подзадачи не возвращаются");
            assertEquals(1, epics.size(), "Некорректное количество эпиков");
            assertEquals(1, subtasks.size(), "Некорректное количество подзадач");

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response3.statusCode(), "Код ответа не соответствует ожидаемому.");
            assertNotNull(response3.body(), "Возвращается пустое тело ответа.");

            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response4.statusCode(), "Код ответа не соответствует ожидаемому.");
            assertNotNull(response4.body(), "Возвращается пустое тело ответа.");

            HttpRequest request5 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response5.statusCode(), "Код ответа не соответствует ожидаемому.");

            List<Epic> epics1 = tm.getAllEpics();
            List<Subtask> subtasks1 = tm.getAllSubtasks();

            assertEquals(0, epics1.size(), "Эпики из менеджера не удалились");
            assertEquals(0, subtasks1.size(), "Подзадачи из менеджера не удалились");
        }
    }

    //проверка работы сервера с получением истории и порядка приоритета
    @Test
    public void testServerHistoryAndPrioritized() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            Task task = new Task("задача-1", "описание зд-1",
                    LocalDateTime.of(2025, 1, 15, 12, 0), Duration.ofMinutes(30));
            String taskJson = gson.toJson(task);
            HttpRequest request0 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response0 = client.send(request0, HttpResponse.BodyHandlers.ofString());

            Epic epic = new Epic("эпик-1", "описание эпика-1");
            String epicJson = gson.toJson(epic);
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

            Subtask subtask = new Subtask("подзадача-1", "описание пзд-1",
                    LocalDateTime.of(2025, 1, 1, 14, 0, 0, 0), Duration.ofMinutes(30), 1);
            String subtaskJson = gson.toJson(subtask);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/history"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response3.statusCode(), "Код ответа не соответствует ожидаемому.");
            assertNotNull(response3.body(), "Возвращается пустое тело ответа.");

            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/prioritized"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response4.statusCode(), "Код ответа не соответствует ожидаемому.");
            assertNotNull(response4.body(), "Возвращается пустое тело ответа.");
        }
    }
}

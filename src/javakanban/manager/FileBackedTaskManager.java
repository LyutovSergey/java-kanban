package javakanban.manager;

import javakanban.exceptions.*;
import javakanban.model.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String dataFileName;
    private final DateTimeFormatter formatterForCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FileBackedTaskManager() {
        this.dataFileName = "dataTaskManager.csv";
        loadFromFile();
    }

    public FileBackedTaskManager(boolean loadDataAtStartup) {
        this.dataFileName = "dataTaskManager.csv";
        if (loadDataAtStartup) {
            loadFromFile();
        }
    }

    public FileBackedTaskManager(String dataFileName, boolean loadDataAtStartup) {
        this.dataFileName = dataFileName;
        if (loadDataAtStartup) {
            loadFromFile();
        }
    }

    @Override
    public Task addTask(Task task) {
        Task res = super.addTask(task);
        save();
        return res;
    }

    @Override
    public Task updateTask(Task task) {
        Task res = super.updateTask(task);
        save();
        return res;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic res = super.addEpic(epic);
        save();
        return res;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic res = super.updateEpic(epic);
        save();
        return res;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask res = super.addSubtask(subtask);
        save();
        return res;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask res = super.updateSubtask(subtask);
        save();
        return res;
    }

    @Override
    public void delTasks() {
        super.delTasks();
        save();
    }

    @Override
    public void delEpics() {
        super.delEpics();
        save();
    }

    @Override
    public void delSubtasks() {
        super.delSubtasks();
        save();
    }

    @Override
    public void delTaskById(int id) {
        super.delTaskById(id);
        save();
    }

    @Override
    public void delSubtaskById(int id) {
        super.delSubtaskById(id);
        save();
    }

    @Override
    public void delEpicById(int id) {
        super.delEpicById(id);
        save();
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFileName, StandardCharsets.UTF_8))) {
            for (String linesCSV : fromTaskManagerToCSV()) {
                writer.write(linesCSV + "\n");
            }
        } catch (IOException e) {
            throw new ManagerFileSaveException("Не удалось сохранить данные в файл", e);
        }
    }

    protected List<String> fromTaskManagerToCSV() {
        List<String> linesCSV = new ArrayList<>(); // Формат записи: "id,TypeTask,name,Status,description,startTime,duration,epicId"
        linesCSV.add("id,type,name,status,description,epic");
        String lineCSV; // Формат записи: "id,TypeTask,name,Status,description,epicId,startTime,duration"

        for (Task task : getTasks()) {
            lineCSV = String.join(",", task.getId().toString(),
                    TypeTask.TASK.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    "",
                    task.getStartTime().isPresent() ? task.getStartTime().get().format(formatterForCSV) : "",
                    task.getDuration().isPresent() ? String.valueOf(task.getDuration().get().toMinutes()): ""
            );
            linesCSV.add(lineCSV);
        }

        for (Epic epic : getEpics()) {
            lineCSV = String.join(",", epic.getId().toString(),
                    TypeTask.EPIC.toString(),
                    epic.getName(),
                    epic.getStatus().toString(),
                    epic.getDescription(),
                    "",
                    "",
                    "");
            linesCSV.add(lineCSV);
        }

        for (Subtask subtask : getSubtasks()) {
            lineCSV = String.join(",", subtask.getId().toString(),
                    TypeTask.SUBTASK.toString(),
                    subtask.getName(),
                    subtask.getStatus().toString(),
                    subtask.getDescription(),
                    subtask.getEpicId().toString(),
                    subtask.getStartTime().isPresent() ? subtask.getStartTime().get().format(formatterForCSV) : "",
                    subtask.getDuration().isPresent() ? String.valueOf(subtask.getDuration().get().toMinutes()) : ""

            );
            linesCSV.add(lineCSV);
        }
        return linesCSV;
    }


    protected void loadFromFile() {
        try {
            Path path = Paths.get(dataFileName);
            List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
            fromCSVToTaskManager(allLines);
        } catch (Exception e) {
            throw new ManagerFileLoadException("Произошла ошибка во время загрузки данных из файла: " + dataFileName, e);
        }
    }

    protected void fromCSVToTaskManager(List<String> lines) {
        Task task;
        Subtask subtask;

        for (String line : lines) { // Формат записи: "id,TypeTask,name,Status,description,epicId,startTime,duration"
            String[] dataInLine = line.split(",");
            switch (dataInLine[1]) {
                case "TASK":
                    if (dataInLine.length == 8) {
                         task = new Task(Integer.parseInt(dataInLine[0]),
                                dataInLine[2],
                                dataInLine[4],
                                Status.valueOf(dataInLine[3]),
                                LocalDateTime.parse(dataInLine[6], formatterForCSV),
                                Duration.ofMinutes(Long.parseLong(dataInLine[7]))
                        );
                    }
                    else {
                        task = new Task(Integer.parseInt(dataInLine[0]),
                                dataInLine[2],
                                dataInLine[4],
                                Status.valueOf(dataInLine[3]));
                    }
                    putTask(task);
                    break;
                case "EPIC":
                    Epic epic = new Epic(Integer.parseInt(dataInLine[0]),
                            dataInLine[2],
                            dataInLine[4],
                            Status.valueOf(dataInLine[3]));
                    putEpic(epic);
                    break;
                case "SUBTASK":
                    if (dataInLine.length == 8) {
                        subtask = new Subtask(Integer.parseInt(dataInLine[0]),
                                dataInLine[2],
                                dataInLine[4],
                                Status.valueOf(dataInLine[3]),
                                Integer.parseInt(dataInLine[5]),
                                LocalDateTime.parse(dataInLine[6], formatterForCSV),
                                Duration.ofMinutes(Long.parseLong(dataInLine[7]))
                        );
                    } else {
                        subtask = new Subtask(Integer.parseInt(dataInLine[0]),
                                dataInLine[2],
                                dataInLine[4],
                                Status.valueOf(dataInLine[3]),
                                Integer.parseInt(dataInLine[5])
                        );
                    }
                    putSubtask(subtask);
                    break;
            }
        }
    }
}

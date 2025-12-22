import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.StatusTask;
import com.course.loadmonitorstudents.service.PdfReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateTaskReport_CreatesFile() throws IOException {
        TaskWithStudentDTO task = new TaskWithStudentDTO(1L, "Тестовая задача", "Описание задачи для тестирования",
                StatusTask.IN_PROGRESS, LocalDateTime.now().plusDays(3), LocalDateTime.now(), LocalDateTime.now().plusDays(2), "Иван", "Иванов",100L);

        List<TaskWithStudentDTO> tasks = Arrays.asList(task);
        Path outputFile = tempDir.resolve("test_report.pdf");

        PdfReportGenerator.generateTaskReport(tasks, "по статусу", false, outputFile.toString());

        assertTrue(Files.exists(outputFile));
        assertTrue(Files.size(outputFile) > 0);
    }

    @Test
    void testGenerateTaskReport_EmptyList_CreatesFile() throws IOException {
        List<TaskWithStudentDTO> tasks = Arrays.asList();
        Path outputFile = tempDir.resolve("empty_report.pdf");
        PdfReportGenerator.generateTaskReport(tasks, null, true, outputFile.toString());
        assertTrue(Files.exists(outputFile));
    }

    @Test
    void testGenerateTaskReport_MultipleTasks_CreatesFile() throws IOException {
        TaskWithStudentDTO task1 = new TaskWithStudentDTO(1L, "Тестовая задача 1", "Описание задачи 1 для тестирования",
                StatusTask.IN_PROGRESS, LocalDateTime.now().plusDays(3), LocalDateTime.now(), LocalDateTime.now().plusDays(2), "Иван", "Иванов",100L);


        TaskWithStudentDTO task2 = new TaskWithStudentDTO(2L, "Тестовая задача 2", "Описание задачи 2 для тестирования",
                StatusTask.IN_PROGRESS, LocalDateTime.now().plusDays(3), LocalDateTime.now(), LocalDateTime.now().plusDays(2), "Иван", "Иванов",100L);


        List<TaskWithStudentDTO> tasks = Arrays.asList(task1, task2);
        Path outputFile = tempDir.resolve("multi_report.pdf");

        PdfReportGenerator.generateTaskReport(tasks, "по дате", true, outputFile.toString());

        assertTrue(Files.exists(outputFile));
    }
}
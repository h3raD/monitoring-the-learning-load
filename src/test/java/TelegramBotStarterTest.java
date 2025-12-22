import com.course.loadmonitorstudents.service.TelegramBotStarter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramBotStarterTest {

    @Test
    void testBotStarter_CanBeCreated() {
        assertDoesNotThrow(() -> {
            TelegramBotStarter starter = new TelegramBotStarter();
        });
    }

    @Test
    void testBotStarter_StopMethodExists() {
        TelegramBotStarter botStarter = new TelegramBotStarter();

        assertDoesNotThrow(() -> {
            TelegramBotStarter.class.getMethod("stopBot").invoke(null);
        });
    }
}
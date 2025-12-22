import com.course.loadmonitorstudents.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testHashPassword_ValidPassword_ReturnsHashed() {
        String password = "SecurePass123!";
        String hashed = PasswordUtil.hashPassword(password);
        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$"));
        assertNotEquals(password, hashed);
    }

    @Test
    void testHashPassword_NullPassword_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(null);
        });
    }

    @Test
    void testHashPassword_EmptyPassword_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword("   ");
        });
    }

    @Test
    void testVerifyPassword_CorrectPassword_ReturnsTrue() {
        String password = "TestPassword";
        String hashed = PasswordUtil.hashPassword(password);
        boolean result = PasswordUtil.verifyPassword(password, hashed);
        assertTrue(result);
    }

    @Test
    void testVerifyPassword_WrongPassword_ReturnsFalse() {
        String originalPassword = "TestPassword";
        String wrongPassword = "WrongPassword";
        String hashed = PasswordUtil.hashPassword(originalPassword);
        boolean result = PasswordUtil.verifyPassword(wrongPassword, hashed);
        assertFalse(result);
    }

    @Test
    void testVerifyPassword_NullPassword_ReturnsFalse() {
        String hashed = PasswordUtil.hashPassword("somePassword");
        boolean result = PasswordUtil.verifyPassword(null, hashed);
        assertFalse(result);
    }

    @Test
    void testVerifyPassword_NullHash_ReturnsFalse() {
        boolean result = PasswordUtil.verifyPassword("password", null);
        assertFalse(result);
    }
}
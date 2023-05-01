import nl.kiipdevelopment.sklectern.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TestCommands {
    private static final Path TEMP_PATH = Path.of("_temp.lsk");
    private static final Path TEMP_RESULT_PATH = Path.of("_temp.l.sk");
    private static final String CONTENT = """
    on join:
        message!("Hello", player)
        message!("Hi", player)
    
    macro message!(message, target):
        send $message to $target
    """;
    private static final String EXPECTED = """
    on join:
    	send "Hello" to player
    	send "Hi" to player
    """;

    @Test
    public void testSingle() throws IOException {
        Files.writeString(TEMP_PATH, CONTENT);
        Assertions.assertEquals(CONTENT.trim(), Files.readString(TEMP_PATH).trim());
        Main.main(new String[] {"build", "--file=" + TEMP_PATH});
        Assertions.assertEquals(EXPECTED.trim(), Files.readString(TEMP_RESULT_PATH).trim());
        Assertions.assertTrue(Files.deleteIfExists(TEMP_PATH));
        Assertions.assertTrue(Files.deleteIfExists(TEMP_RESULT_PATH));
    }
}

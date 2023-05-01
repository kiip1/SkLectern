import nl.kiipdevelopment.sklectern.Script;
import org.junit.jupiter.api.Test;

final class TestOptions {
    @Test
    public void testOptions() {
        final Script script = new Script("options", """
        options:
            z: y
            y: x
        
        on join:
            send "{@a}" to player
            send "{@{@z}}" to player{@z}
        """);

        System.out.println(script.parse());
        System.out.println(script.transform());
    }
}

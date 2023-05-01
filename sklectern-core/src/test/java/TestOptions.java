import nl.kiipdevelopment.sklectern.Script;
import org.junit.jupiter.api.Test;

final class TestOptions {
    @Test
    public void testOptions() {
        final Script script = new Script("options", """
        options:
            a: "{@b}"
            b: {@a}
        
        on join:
            send {@a} to player
        """);

        System.out.println(script.parse());
        System.out.println(script.transform());
    }
}

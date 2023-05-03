import nl.kiipdevelopment.sklectern.Script;
import org.junit.jupiter.api.Test;

final class TestShake {
    @Test
    public void testShake() {
        final Script script = new Script("shake", """
        on join:
            send "hi"
        
        on disconnect:
        on disconnect:
        on disconnect:
        on join:
            send "hi"
        on disconnect:
        on disconnect:
        
        on join:
            send "hi"
        """);

        System.out.println(script.parse());
        System.out.println(script.transform());
    }
}

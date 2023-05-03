import nl.kiipdevelopment.sklectern.Script;
import org.junit.jupiter.api.Test;

final class TestArithmetic {
    @Test
    public void testArithmetic() {
        final Script script = new Script("arithmetic", """
        on join:
            send (2 + 3) ** vector(3, 3, 3) to player
            send 2 + 3 ** vector(3, 3, 3) to player
            send vector(0, 1, 0) ++ vector(1, 2 * 10 - 5, 3) * -1 to player
            send {_a} ++ vector(0, 3, 0) to player
            send (2 + {_a}) ** vector(3, 3, 3 * {_b}) - 3 + 2 to player
            send (2 + 3) ** vector(3, 3, 3 + {_b}) * 3 + 2 to player
            send (2 + 3) ** vector(3, 3, 3 * 3) - 3 + 2 to player
            send (-2 + 3) ** vector(-3, 3, 3 * 3) - 3 + 2 to player
            send (2 + 3) ** -vector(3, 3, 3 * 3) - 3 + 2 to player
            send (2 + 3) ** -vector(3, 3, 3 * 3) + 0.100009 to player
        """);

        System.out.println(script.parse());
        System.out.println(script.transform());
    }
}

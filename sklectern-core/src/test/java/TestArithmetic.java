import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import org.junit.jupiter.api.Test;

final class TestArithmetic {
    @Test
    public void testArithmetic() {
        final Script script = new Script("arithmetic", """
        on join:
            send 2 ++ 3 to player
            send vector(0, 1, 0) ++ vector(1, 2, 3) ** -1 to player
            send {_a} ++ vector(0, 3, 0) to player
        """);

        ScriptLexer.of(script.source()).instance().iterator().forEachRemaining(System.out::println);
        System.out.println(script.parse());
        System.out.println(script.transform());
    }
}

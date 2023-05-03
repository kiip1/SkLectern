import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class TestVariables {
    @Test
    public void testOptions() {
        final Script script = new Script("options", """
        options:
            a: "HI"
            b: "{@a}"
            c: "{@b}"
            d: "{@c}"
        
        on join:
            send "{@a}" to player
            send {@b} to player
            send {@c} to player
            send {@d} to player
        """);

        Assertions.assertEquals("""
        on join:
        	send ""\"HI""\" to player
        	send ""\"HI""\" to player
        	send ""\"""\""HI""\"""\"" to player
        	send ""\"""\"""\"""\"""\"HI""\"""\"""\"""\"""\" to player
        """.trim(), script.transform().trim());
    }

    @Test
    public void testVariables() {
        final Script script = new Script("variables", """
        on chat:
            send {_a} + 5 to player
            send {l} - 3 to player
        """);

        Assertions.assertEquals("""
        on chat:
        	send {_a} + 5 to player
        	send {l} - 3 to player
        """.trim(), script.transform().trim());

        Assertions.assertThrows(ParseException.class, () -> {
            new Script("variables", """
            on chat:
                send {} to player
            """).transform();
        });
    }
}

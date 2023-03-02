import nl.kiipdevelopment.sklectern.Script;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class TestMacro {
    @Test
    public void testMacro() {
        final Script script = new Script("macro", """
        function hello(p: player):
            title!({_p}, "Hello")
        
        macro title!(receivers, message):
            send title $message to $receivers
        """);

        Assertions.assertEquals("""
        function hello(p: player):
        	send title "Hello" to {_p}
        """, script.transform());
    }
}

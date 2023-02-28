import be.seeseemelk.mockbukkit.MockBukkit;
import nl.kiipdevelopment.sklectern.BukkitSkLectern;
import nl.kiipdevelopment.sklectern.Script;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

final class TestTransform {
    @BeforeAll
    public static void load() throws URISyntaxException, IOException {
        MockBukkit.mock();

        ResourceUtils.extractResource("scripts", MockBukkit.getMock()
                .getPluginsFolder()
                .toPath()
                .resolve("SkLectern-1.0.0")
                .resolve("scripts"));

        MockBukkit.load(BukkitSkLectern.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

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

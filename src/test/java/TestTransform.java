import be.seeseemelk.mockbukkit.MockBukkit;
import nl.kiipdevelopment.sklectern.BukkitSkLectern;
import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.SkLectern;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void testTransform() throws IOException {
        final Path distributionFolder = SkLectern.instance().config().distributionFolder();
        SkLectern.instance().scriptManager().transformAll();

        System.out.println(new Script(SkLectern.instance().config().scriptFolder().resolve("test.lsk")).parse());
        System.out.println(Files.readString(distributionFolder.resolve("test.l.sk")));
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

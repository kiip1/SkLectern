import be.seeseemelk.mockbukkit.MockBukkit;
import nl.kiipdevelopment.sklectern.BukkitSkLectern;
import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.SkLectern;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;

import java.io.IOException;
import java.net.URISyntaxException;

final class Test {
    public static void main(String[] args) throws IOException, URISyntaxException {
        MockBukkit.mock();

        ResourceUtils.extractResource("scripts", MockBukkit.getMock()
                .getPluginsFolder()
                .toPath()
                .resolve("SkLectern-1.0.0")
                .resolve("scripts"));

        MockBukkit.load(BukkitSkLectern.class);

        final Script script = new Script(SkLectern.instance()
                .config()
                .scriptFolder()
                .resolve("test.lsk"));

        ScriptLexer.of(script.source())
                .instance()
                .iterator()
                .forEachRemaining(System.out::println);

        System.out.println(script.parse());
        System.out.println(script.transform());

        MockBukkit.unmock();
    }
}

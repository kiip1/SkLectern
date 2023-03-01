import be.seeseemelk.mockbukkit.MockBukkit;
import nl.kiipdevelopment.sklectern.BukkitSkLectern;
import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.SkLectern;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

final class Test {
    public static void main(String[] args) throws IOException, URISyntaxException {
        MockBukkit.mock();

        ResourceUtils.extractResource("scripts", MockBukkit.getMock()
                .getPluginsFolder()
                .toPath()
                .resolve("SkLectern-1.0.0")
                .resolve("scripts"));

        MockBukkit.load(BukkitSkLectern.class);

        ScriptLexer.of(new Script(SkLectern.instance().config().scriptFolder().resolve("arithmetic.lsk")).source()).instance().iterator().forEachRemaining(System.out::println);

        final Path distributionFolder = SkLectern.instance().config().distributionFolder();
        SkLectern.instance().scriptManager().transformAll();
        System.out.println(new Script(SkLectern.instance().config().scriptFolder().resolve("arithmetic.lsk")).parse());
        System.out.println(Files.readString(distributionFolder.resolve("arithmetic.l.sk")));

        MockBukkit.unmock();
    }
}

import be.seeseemelk.mockbukkit.MockBukkit;
import nl.kiipdevelopment.sklectern.BukkitSkLectern;
import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.SkLectern;

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

        final Path distributionFolder = SkLectern.instance().config().distributionFolder();
        SkLectern.instance().scriptManager().transformAll();

        System.out.println(new Script(SkLectern.instance().config().scriptFolder().resolve("test.lsk")).parse());
        System.out.println(Files.readString(distributionFolder.resolve("test.l.sk")));

        MockBukkit.unmock();
    }
}

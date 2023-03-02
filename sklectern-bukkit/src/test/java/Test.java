import be.seeseemelk.mockbukkit.MockBukkit;
import nl.kiipdevelopment.sklectern.BukkitSkLectern;
import nl.kiipdevelopment.sklectern.SkLectern;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

final class Test {
    public static void main(String[] args) throws IOException, URISyntaxException {
        MockBukkit.mock();

        ResourceUtils.extractResource("scripts", MockBukkit.getMock()
                .getPluginsFolder()
                .toPath()
                .resolve("SkLectern-1.0.2-alpha")
                .resolve("scripts"));

        MockBukkit.load(BukkitSkLectern.class);

//        final Script script = new Script(SkLectern.instance()
//                .config()
//                .scriptFolder()
//                .resolve("capture.lsk"));

//        ScriptLexer.of(script.source())
//                .instance()
//                .iterator()
//                .forEachRemaining(System.out::println);

//        System.out.println(script.parse());
//        System.out.println(script.transform());

        try (
                Stream<Path> stream = Files.list(SkLectern.instance().config().distributionFolder());
                BufferedWriter writer = Files.newBufferedWriter(Path.of("test-output.txt"))
        ) {
            for (Path path : stream.toList())
                writer.write("Script " + path + ":\n" + Files.readString(path) + "\n");

            writer.flush();
        }

        MockBukkit.unmock();
    }
}

package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.parser.ParseException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static com.google.common.io.Files.getNameWithoutExtension;
import static nl.kiipdevelopment.sklectern.Main.Build;
import static nl.kiipdevelopment.sklectern.Main.Transform;

@Command(subcommands = { Transform.class, Build.class })
public final class Main {
    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }

    @Command(name = "transform", description = "Transforms the given file and outputs the result")
    static final class Transform implements Callable<Integer> {
        @Option(names = { "-f", "--file" }, required = true)
        public Path file;

        @Override
        public Integer call() {
            try {
                final Script script = new Script(file);

                try {
                    System.out.println(script.transform());
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                    return 1;
                }
            } catch (NoSuchFileException e) {
                System.err.println("File not found");
                return 1;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            return 0;
        }
    }

    @Command(name = "build", description = "Transforms the given file and writes the result")
    static final class Build implements Callable<Integer> {
        @Option(names = { "-f", "--file" }, required = true)
        public Path file;

        @Override
        public Integer call() {
            try {
                final Script script = new Script(file);

                try {
                    Files.writeString(
                            file.resolveSibling(getNameWithoutExtension(script.name()) + ".l.sk"),
                            script.transform()
                    );
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                    return 1;
                }
            } catch (NoSuchFileException e) {
                System.err.println("File not found");
                return 1;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            return 0;
        }
    }
}

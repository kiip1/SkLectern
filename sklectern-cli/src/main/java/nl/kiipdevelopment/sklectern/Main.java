package nl.kiipdevelopment.sklectern;

import com.google.common.base.Strings;

import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;

public final class Main {
    public static void main(String[] args) {
        Path file = null;
        Path folder = null;
        for (String arg : Arrays.stream(args).sorted().toList()) {
            if (arg.equalsIgnoreCase("h") || arg.equalsIgnoreCase("help") || arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help")) {
                help();
                return;
            } else if (arg.equalsIgnoreCase("b") || arg.equalsIgnoreCase("build")) {
                if (folder != null) {
                    try {
                        SkLectern.instance().scriptManager().transformAll(folder);
                    } catch (UncheckedIOException e) {
                        if (e.getCause() instanceof NoSuchFileException noSuchFileException)
                            System.err.println("Couldn't find folder " + noSuchFileException.getFile());
                        else throw e;
                    }

                    return;
                }

                if (file == null) {
                    System.err.println("Missing --file flag.");
                    return;
                }

                try {
                    SkLectern.instance().scriptManager().transform(file);
                } catch (UncheckedIOException e) {
                    if (e.getCause() instanceof NoSuchFileException noSuchFileException)
                        System.err.println("Couldn't find file " + noSuchFileException.getFile());
                    else throw e;
                }

                return;
            } else if (arg.startsWith("-f=") || arg.startsWith("--file=")) {
                file = Path.of(arg.split("=")[1]);
            } else if (arg.startsWith("-d=") || arg.startsWith("--dir=") || arg.startsWith("--folder=")) {
                folder = Path.of(arg.split("=")[1]);
            } else {
                System.out.println("Unrecognised flag '" + arg + "'. Use -h or --help for help.");
                return;
            }
        }

        help();
    }

    private static void help() {
        System.out.println("All commands:");
        System.out.println(pad("build, b") + "Transforms either a single file or a folder." +
                "\n" + pad() + "(Requires either --file or --folder to be set)");
        System.out.println();
        System.out.println("All arguments:");
        System.out.println(pad("-f, --file") + "Sets the file to transform.");
        System.out.println(pad("-d, --dir, --folder") + "Sets the folder to transform.");
    }

    private static String pad() {
        return " ".repeat(20);
    }

    private static String pad(String input) {
        return Strings.padEnd(input, 20, ' ');
    }
}

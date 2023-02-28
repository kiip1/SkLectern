package nl.kiipdevelopment.sklectern;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import static nl.kiipdevelopment.sklectern.command.SkLecternCommand.*;

public final class Main {
    private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();
    private static final Audience CONSOLE = new Audience() {
        @Override
        public void sendMessage(@NotNull Identity source, @NotNull Component component, @NotNull MessageType type) {
            final String message = SERIALIZER.serialize(component);
            if (source == STDOUT) System.out.println(message);
            else if (source == STDERR) System.err.println(message);
            else throw new IllegalArgumentException("Unknown source: " + source);
        }
    };

    public static void main(String[] args) {
        COMMAND.execute(CONSOLE, "sklectern " + String.join(" ", args));
    }
}

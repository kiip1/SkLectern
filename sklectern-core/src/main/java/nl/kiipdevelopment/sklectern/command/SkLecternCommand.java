package nl.kiipdevelopment.sklectern.command;

import mx.kenzie.centurion.Command;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import nl.kiipdevelopment.sklectern.Script;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;

import static mx.kenzie.centurion.CommandResult.*;
import static nl.kiipdevelopment.sklectern.command.PathArgument.PATH;

@ApiStatus.Internal
public final class SkLecternCommand extends Command<Audience> {
    public static final SkLecternCommand COMMAND = new SkLecternCommand();
    // These identities are for terminal compatibility
    public static final Identity STDOUT = () -> new UUID(0, 0);
    public static final Identity STDERR = () -> new UUID(0, 1);

    private SkLecternCommand() {}

    @Override
    public Command<Audience>.Behaviour create() {
        return command("sklectern")
                .arg("parse", PATH, (sender, arguments) -> {
                    try {
                        final Path file = arguments.get(0);
                        final Script script = new Script(file);

                        try {
                            sender.sendMessage(STDOUT, Component.text(script.parse().toString()));
                        } catch (ParseException e) {
                            sender.sendMessage(STDERR, Component.text(e.getMessage()));
                            return FAILED_UNKNOWN;
                        }
                    } catch (NoSuchFileException e) {
                        sender.sendMessage(STDERR, Component.text("File not found"));
                        return WRONG_INPUT;
                    } catch (IOException e) {
                        sender.sendMessage(STDERR, Component.text(e.getMessage()));
                        return FAILED_EXCEPTION;
                    }

                    return PASSED;
                })
                .arg("build", PATH, (sender, arguments) -> {
                    try {
                        final Path file = arguments.get(0);
                        final Script script = new Script(file);

                        try {
                            sender.sendMessage(STDOUT, Component.text(script.transform()));
                        } catch (ParseException e) {
                            sender.sendMessage(STDERR, Component.text(e.getMessage()));
                            return FAILED_UNKNOWN;
                        }
                    } catch (NoSuchFileException e) {
                        sender.sendMessage(STDERR, Component.text("File not found"));
                        return WRONG_INPUT;
                    } catch (IOException e) {
                        sender.sendMessage(STDERR, Component.text(e.getMessage()));
                        return FAILED_EXCEPTION;
                    }

                    return PASSED;
                });
    }
}

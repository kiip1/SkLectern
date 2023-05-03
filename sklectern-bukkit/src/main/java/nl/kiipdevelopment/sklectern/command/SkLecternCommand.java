package nl.kiipdevelopment.sklectern.command;

import mx.kenzie.centurion.Command;
import mx.kenzie.centurion.MinecraftCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.kiipdevelopment.sklectern.SkLectern;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static com.google.common.io.MoreFiles.getNameWithoutExtension;
import static mx.kenzie.centurion.CommandResult.*;

@ApiStatus.Internal
public final class SkLecternCommand extends MinecraftCommand {
    public static final SkLecternCommand COMMAND = new SkLecternCommand();

    private SkLecternCommand() {
        super("Provides SkLectern commands", "/sklectern help",
                "sklectern.admin", Component.text("Insufficient permissions", NamedTextColor.RED));
    }

    @Override
    public Command<CommandSender>.Behaviour create() {
        return command("sklectern")
                .arg("build", (sender, arguments) -> {
                    try {
                        SkLectern.instance().scriptManager().transformAll();
                        sender.sendMessage(Component.text("Success", NamedTextColor.GREEN));
                    } catch (ParseException e) {
                        sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                        return FAILED_UNKNOWN;
                    } catch (UncheckedIOException e) {
                        if (e.getCause() instanceof NoSuchFileException) {
                            sender.sendMessage(Component.text("File not found", NamedTextColor.RED));
                            return WRONG_INPUT;
                        }

                        sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                        return FAILED_EXCEPTION;
                    }

                    return PASSED;
                }).arg("build", PathArgument.path(SkLectern.instance().config().scriptFolder()), (sender, arguments) -> {
                    try {
                        final Path file = arguments.get(0);
                        SkLectern.instance().scriptManager().transform(file, file.resolveSibling(getNameWithoutExtension(file.getFileName()) + ".l.sk"));
                        sender.sendMessage(Component.text("Success", NamedTextColor.GREEN));
                    } catch (ParseException e) {
                        sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                        return FAILED_UNKNOWN;
                    } catch (UncheckedIOException e) {
                        if (e.getCause() instanceof NoSuchFileException) {
                            sender.sendMessage(Component.text("File not found", NamedTextColor.RED));
                            return WRONG_INPUT;
                        }

                        sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                        return FAILED_EXCEPTION;
                    }

                    return PASSED;
                });
    }
}

package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.command.SkLecternCommand;
import nl.kiipdevelopment.sklectern.context.Config;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@ApiStatus.Experimental
public final class BukkitSkLectern extends JavaPlugin implements SkLectern {
    private final Config config;
    private ScriptManager scriptManager;

    public BukkitSkLectern(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description,
                           @NotNull File dataFolder, @NotNull File file) {

        super(loader, description, dataFolder, file);
        SkLectern.setInstance(this);
        config = new Config(
                getDataFolder().toPath().resolve("scripts"),
                getDataFolder().toPath().resolve("dist"),
                true
        );
    }

    public BukkitSkLectern() {
        super();
        SkLectern.setInstance(this);
        config = new Config(
                getDataFolder().toPath().resolve("scripts"),
                getDataFolder().toPath().resolve("dist"),
                false
        );
    }

    @Override
    public void onLoad() {
        scriptManager = new ScriptManager(config);
    }

    @Override
    public void onEnable() {
        try {
            try {
                Files.createDirectories(config.scriptFolder());
                Files.createDirectories(config.distributionFolder());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SkLecternCommand.COMMAND.register(this);

            scriptManager.transformAll();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull Config config() {
        return config;
    }

    @Override
    public @NotNull ScriptManager scriptManager() {
        return scriptManager;
    }
}

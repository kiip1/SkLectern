package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.context.Config;
import org.jetbrains.annotations.NotNull;

public interface SkLectern {
    static SkLectern instance() {
        return BukkitSkLectern.instance();
    }

    @NotNull Config config();

    @NotNull ScriptManager scriptManager();
}

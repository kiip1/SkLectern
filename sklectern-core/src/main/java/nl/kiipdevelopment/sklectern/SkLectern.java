package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.context.Config;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface SkLectern {
    static SkLectern instance() {
        return SkLecternImpl.instance();
    }

    static void setInstance(SkLectern instance) {
        SkLecternImpl.instance = instance;
    }

    @NotNull Config config();

    @NotNull ScriptManager scriptManager();
}

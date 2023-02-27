package nl.kiipdevelopment.sklectern.context;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public record Config(Path scriptFolder, Path distributionFolder, boolean testing) {}

package nl.kiipdevelopment.sklectern.context;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

/**
 * The configuration SkLectern uses
 *
 * @param scriptFolder The folder where the scripts are in
 * @param distributionFolder The folder for the build output
 * @param testing Whether we are in a testing environment
 */
@ApiStatus.Internal
public record Config(Path scriptFolder, Path distributionFolder, boolean testing) {}

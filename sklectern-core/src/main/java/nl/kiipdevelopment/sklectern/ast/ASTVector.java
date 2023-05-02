package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ApiStatus.Internal
public record ASTVector(Vector3D value) implements ASTLiteral<ASTVector.Vector3D> {
    public ASTVector(BigDecimal x, BigDecimal y, BigDecimal z) {
        this(new Vector3D(x, y, z));
    }

    @Override
    public void check(@NotNull Context context) {}

    @Override
    public @NotNull String visit() {
        return value.toString();
    }

    public record Vector3D(@NotNull BigDecimal x, @NotNull BigDecimal y, @NotNull BigDecimal z) {
        @Contract("_ -> new")
        public @NotNull Vector3D add(@NotNull Vector3D other) {
            return new Vector3D(x.add(other.x), y.add(other.y), z.add(other.z));
        }

        @Contract("_ -> new")
        public @NotNull Vector3D subtract(@NotNull Vector3D other) {
            return new Vector3D(x.subtract(other.x), y.subtract(other.y), z.subtract(other.z));
        }

        @Contract("_ -> new")
        public @NotNull Vector3D multiply(@NotNull Vector3D other) {
            return new Vector3D(x.multiply(other.x), y.multiply(other.y), z.multiply(other.z));
        }

        @Contract("_ -> new")
        public @NotNull Vector3D divide(@NotNull Vector3D other) {
            return new Vector3D(x.divide(other.x, RoundingMode.HALF_UP), y.divide(other.y, RoundingMode.HALF_UP),
                    z.divide(other.z, RoundingMode.HALF_UP));
        }

        @Override
        public String toString() {
            return "vector(" + x + "," + y + "," + z + ")";
        }
    }
}

package nl.kiipdevelopment.sklectern.ast.value;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.ASTNodeList;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.BinaryOperator;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTVector extends ASTValue<ASTLiteralVector.Vector3D> {
    record Vector3D(@NotNull ASTNode x, @NotNull ASTNode y, @NotNull ASTNode z) {
        public Vector3D(@NotNull BigDecimal x, @NotNull BigDecimal y, @NotNull BigDecimal z) {
            this(new ASTNumber(x), new ASTNumber(y), new ASTNumber(z));
        }

        public Vector3D(@NotNull BigDecimal value) {
            this(value, value, value);
        }

        @Contract("_, _ -> new")
        public @NotNull Vector3D add(@NotNull Context context, @NotNull Vector3D other) {
            return vectorOperation(context, BigDecimal::add, "+", this, other);
        }

        @Contract("_, _ -> new")
        public @NotNull Vector3D subtract(@NotNull Context context, @NotNull Vector3D other) {
            return vectorOperation(context, BigDecimal::subtract, "-", this, other);
        }

        @Contract("_, _ -> new")
        public @NotNull Vector3D multiply(@NotNull Context context, @NotNull Vector3D other) {
            return vectorOperation(context, BigDecimal::multiply, "*", this, other);
        }

        @Contract("_, _ -> new")
        public @NotNull Vector3D divide(@NotNull Context context, @NotNull Vector3D other) {
            return vectorOperation(context, (a, b) -> a.divide(b, RoundingMode.HALF_UP), "/", this, other);
        }

        @Override
        public String toString() {
            return "vector(" + x.visit(Context.of()) + "," + y.visit(Context.of()) + "," + z.visit(Context.of()) + ")";
        }

        private static @Nullable BigDecimal from(@NotNull Context context, @NotNull ASTNode node) {
            try {
                return new BigDecimal(node.visit(context).trim());
            } catch (Exception e) {
                return null;
            }
        }

        private static @NotNull ASTNode operation(
                @NotNull Context context, @NotNull BinaryOperator<BigDecimal> operation,
                @NotNull String operator, @NotNull ASTNode a, @NotNull ASTNode b
        ) {
            final BigDecimal decimalA = from(context, a);
            final BigDecimal decimalB = from(context, b);
            return (decimalA != null && decimalB != null)
                    ? new ASTNumber(operation.apply(decimalA, decimalB))
                    : new ASTNodeList(List.of(a, new ASTString(operator), b));
        }

        private static @NotNull Vector3D vectorOperation(
                @NotNull Context context, @NotNull BinaryOperator<BigDecimal> operation,
                @NotNull String operator, @NotNull Vector3D a, @NotNull Vector3D b
        ) {
            return new Vector3D(
                    operation(context, operation, operator, a.x, b.x),
                    operation(context, operation, operator, a.y, b.y),
                    operation(context, operation, operator, a.z, b.z)
            );
        }
    }
}

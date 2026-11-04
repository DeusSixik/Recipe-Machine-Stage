package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.overgeared;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Provides the common access gate used by Overgeared recipe lookup mixins.
 */
public final class OvergearedRecipeSupport {

    /**
     * Prevents construction of this stateless compatibility utility.
     */
    private OvergearedRecipeSupport() {
    }

    /**
     * Retains a recipe candidate only when its owner satisfies the supplied access check.
     *
     * @param candidate the recipe lookup result; must not be {@code null}.
     * @param accessCheck the stage-aware access check applied to a present candidate; must not be {@code null}.
     * @param <T> the type of recipe lookup result.
     * @return the original candidate when access is allowed, otherwise an empty optional; never {@code null}.
     * @throws NullPointerException if {@code candidate} or {@code accessCheck} is {@code null}.
     */
    public static <T> Optional<T> filter(Optional<T> candidate, Predicate<? super T> accessCheck) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(accessCheck, "accessCheck");
        return candidate.filter(accessCheck);
    }
}

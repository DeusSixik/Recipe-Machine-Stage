package net.sdm.recipemachinestage.compat.overgeared;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Provides common filtering operations for Overgeared recipe lookups.
 */
public final class OvergearedRecipeSupport {

    /**
     * Prevents construction of this stateless compatibility utility.
     */
    private OvergearedRecipeSupport() {
    }

    /**
     * Retains an optional recipe only when it satisfies the supplied access check.
     *
     * @param candidate the recipe lookup result; must not be {@code null}.
     * @param accessCheck the stage-aware predicate applied to a present recipe; must not be {@code null}.
     * @param <T> the recipe value type.
     * @return the original recipe when access is allowed, otherwise an empty optional; never {@code null}.
     * @throws NullPointerException if {@code candidate} or {@code accessCheck} is {@code null}.
     */
    public static <T> Optional<T> filter(Optional<T> candidate, Predicate<? super T> accessCheck) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(accessCheck, "accessCheck");
        return candidate.filter(accessCheck);
    }

    /**
     * Copies all recipes satisfying the supplied access check into a new mutable list.
     *
     * @param candidates the recipes returned by the original lookup; must not be {@code null}.
     * @param accessCheck the stage-aware predicate applied to every recipe; must not be {@code null}.
     * @param <T> the recipe value type.
     * @return a new list containing only accessible recipes, preserving their original order; never {@code null}.
     * @throws NullPointerException if {@code candidates} or {@code accessCheck} is {@code null}.
     */
    public static <T> List<T> filter(List<T> candidates, Predicate<? super T> accessCheck) {
        Objects.requireNonNull(candidates, "candidates");
        Objects.requireNonNull(accessCheck, "accessCheck");

        final List<T> accessible = new ArrayList<>(candidates.size());
        for (final T candidate : candidates) {
            if (accessCheck.test(candidate)) {
                accessible.add(candidate);
            }
        }
        return accessible;
    }
}

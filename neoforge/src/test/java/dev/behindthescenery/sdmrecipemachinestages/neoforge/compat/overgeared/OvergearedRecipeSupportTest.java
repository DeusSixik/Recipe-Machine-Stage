package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.overgeared;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the shared access gate used by Overgeared recipe lookup mixins.
 */
class OvergearedRecipeSupportTest {

    /**
     * Verifies that an allowed lookup preserves the original recipe candidate.
     */
    @Test
    void filter_presentAllowedCandidate_returnsCandidate() {
        final Object recipe = new Object();

        final Optional<Object> result = OvergearedRecipeSupport.filter(
                Optional.of(recipe),
                candidate -> true
        );

        assertSame(recipe, result.orElseThrow());
    }

    /**
     * Verifies that a denied lookup cannot expose the recipe candidate.
     */
    @Test
    void filter_presentDeniedCandidate_returnsEmpty() {
        final Optional<String> result = OvergearedRecipeSupport.filter(
                Optional.of("overgeared:forging/example"),
                candidate -> false
        );

        assertTrue(result.isEmpty());
    }

    /**
     * Verifies that an empty recipe lookup remains empty without invoking the access check.
     */
    @Test
    void filter_emptyCandidate_doesNotInvokeAccessCheck() {
        final Optional<String> result = OvergearedRecipeSupport.filter(
                Optional.empty(),
                candidate -> {
                    throw new AssertionError("Access checks must not run without a recipe candidate");
                }
        );

        assertEquals(Optional.empty(), result);
    }
}

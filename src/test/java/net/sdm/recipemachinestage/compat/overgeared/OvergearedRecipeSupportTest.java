package net.sdm.recipemachinestage.compat.overgeared;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the shared access filters used by Overgeared recipe lookup mixins.
 */
class OvergearedRecipeSupportTest {

    /**
     * Verifies that an allowed optional lookup preserves the original recipe candidate.
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
     * Verifies that a denied optional lookup cannot expose the recipe candidate.
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
     * Verifies that an empty optional lookup remains empty without invoking the access check.
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

    /**
     * Verifies that list filtering preserves accessible candidates and their original order.
     */
    @Test
    void filter_mixedCandidateList_returnsAccessibleCandidatesInOrder() {
        final List<String> candidates = new ArrayList<>(List.of("poor", "well", "expert", "master"));

        final List<String> result = OvergearedRecipeSupport.filter(
                candidates,
                candidate -> candidate.length() > 4
        );

        assertEquals(List.of("expert", "master"), result);
        assertNotSame(candidates, result);
    }
}

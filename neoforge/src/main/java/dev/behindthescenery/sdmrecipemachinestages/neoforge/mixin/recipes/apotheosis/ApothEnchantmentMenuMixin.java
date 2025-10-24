package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.apotheosis;

import dev.behindthescenery.sdmrecipemachinestages.mixin.accessor.EnchantmentMenuAccessor;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import dev.shadowsoffire.apothic_enchanting.Ench;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import dev.shadowsoffire.apothic_enchanting.table.infusion.InfusionRecipe;
import dev.shadowsoffire.apothic_enchanting.util.MiscUtil;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(ApothEnchantmentMenu.class)
public abstract class ApothEnchantmentMenuMixin extends EnchantmentMenu {

    @Shadow
    protected EnchantmentTableStats stats;

    @Shadow
    protected abstract List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int enchantSlot, int level);

    public ApothEnchantmentMenuMixin(int i, Inventory arg) {
        super(i, arg);
    }

    /**
     * @author Sixik
     * @reason Optimize and add stage restriction
     */
    @Overwrite
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id < 0 || id >= this.costs.length) {
            Util.logAndPauseIfInIde(player.getName().getString() + " pressed invalid button id: " + id);
            return false;
        }

        final EnchantmentMenuAccessor accessor = (EnchantmentMenuAccessor) this;
        final int levelCost = this.costs[id];
        if (levelCost <= 0) return false;

        final ItemStack toEnchant = accessor.getEnchantSlots().getItem(0);
        if (toEnchant.isEmpty()) return false;

        final ItemStack lapis = this.getSlot(1).getItem();
        final int lapisCost = id + 1;

        final boolean isCreative = player.getAbilities().instabuild;
        final boolean hasEnoughLapis = !lapis.isEmpty() && lapis.getCount() >= lapisCost;
        final boolean hasEnoughLevels = player.experienceLevel >= lapisCost && player.experienceLevel >= levelCost;

        if (!isCreative && !hasEnoughLapis) return false;
        if (!isCreative && !hasEnoughLevels) return false;

        accessor.getAccess().execute((world, pos) -> {
            final float eterna = this.stats.eterna();
            final float quanta = this.stats.quanta();
            final float arcana = this.stats.arcana();
            final boolean stable = this.stats.stable();

            final List<EnchantmentInstance> enchList = getEnchantmentList(toEnchant, id, levelCost);
            if (enchList.isEmpty()) return;

            EnchantmentUtils.chargeExperience(player, MiscUtil.getExpCostForSlot(levelCost, id));
            player.onEnchantmentPerformed(toEnchant, 0);

            final EnchantmentInstance first = enchList.getFirst();

            if (first.enchantment.is(Ench.Enchantments.INFUSION)) {
                final InfusionRecipe match = bts$findMatch(player, world, toEnchant, eterna, quanta, arcana);
                if (match == null) return;
                final ItemStack infused = match.assemble(toEnchant, eterna, quanta, arcana);
                accessor.getEnchantSlots().setItem(0, infused);
            } else {
                final ItemStack enchanted = toEnchant.getItem().applyEnchantments(toEnchant, enchList);
                accessor.getEnchantSlots().setItem(0, enchanted);
            }

            if (!isCreative) {
                lapis.shrink(lapisCost);
                if (lapis.isEmpty()) {
                    accessor.getEnchantSlots().setItem(1, ItemStack.EMPTY);
                }
            }

            player.awardStat(Stats.ENCHANT_ITEM);

            if (player instanceof ServerPlayer sp) {
                final ItemStack resultItem = accessor.getEnchantSlots().getItem(0);
                Ench.Triggers.ENCHANTED.trigger(sp, resultItem, levelCost, eterna, quanta, arcana, stable);
                CriteriaTriggers.ENCHANTED_ITEM.trigger(sp, resultItem, levelCost);
            }

            accessor.getEnchantSlots().setChanged();
            accessor.getEnchantmentSeed().set(player.getEnchantmentSeed());
            this.slotsChanged(accessor.getEnchantSlots());
            world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
        });

        return true;
    }

    @Unique
    private static InfusionRecipe bts$findMatch(Player player, Level level, ItemStack input, float eterna, float quanta, float arcana) {
        final List<RecipeHolder<InfusionRecipe>> recipes = new ArrayList<>();

        if(RMSUtils.hasRestrictionsForType(Ench.RecipeTypes.INFUSION)) {
            recipes.addAll(RMSUtils.filterRecipes(level.getRecipeManager().getAllRecipesFor(Ench.RecipeTypes.INFUSION), player));
        } else recipes.addAll(level.getRecipeManager().getAllRecipesFor(Ench.RecipeTypes.INFUSION));

        return recipes.stream().map(RecipeHolder::value).sorted((r1, r2) -> -Float.compare(r1.getRequirements().eterna(), r2.getRequirements().eterna())).filter((r) -> r.matches(input, eterna, quanta, arcana)).findFirst().orElse(null);
    }
}

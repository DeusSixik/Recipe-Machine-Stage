package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.apotheosis;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvagingMenu;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvagingTableTile;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.QuickMoveHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

@Mixin(SalvagingMenu.class)
public abstract class SalvagingMenuMixin extends BlockEntityMenu<SalvagingTableTile> {


    @Shadow
    @Final
    protected Player player;

    protected SalvagingMenuMixin(MenuType<?> type, int id, Inventory pInv, BlockPos pos) {
        super(type, id, pInv, pos);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/placebo/menu/QuickMoveHandler;registerRule(Ljava/util/function/BiPredicate;II)V", ordinal = 0))
    public void bts$init$registerRule(QuickMoveHandler instance, BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx) {
        instance.registerRule((stack, slot) -> slot >= this.playerInvStart && !bts$findMatch(this.level, stack, player).isEmpty(), 0, 12);
    }

    @Redirect(method = "salvageAll", at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apotheosis/affix/salvaging/SalvagingMenu;getSalvageResults(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    protected List<ItemStack> bts$salvageAll(Level out, ItemStack d) {
        return bts$getSalvageResults(out, d, player);
    }

    @Unique
    private static List<ItemStack> bts$getSalvageResults(Level level, ItemStack stack, Player player) {
        List<ItemStack> outputs = new ArrayList<>();

        for(RecipeHolder<SalvagingRecipe> recipe :  bts$findMatch(level, stack, player)) {
            for(SalvagingRecipe.OutputData d : recipe.value().getOutputs()) {
                ItemStack out = d.stack().copy();
                out.setCount(SalvagingMenu.getSalvageCount(d, stack, level.random));
                outputs.add(out);
            }
        }

        return outputs;
    }

    @Unique
    private static List<ItemStack>  bts$getBestPossibleSalvageResults(Level level, ItemStack stack, Player player) {
        List<ItemStack> outputs = new ArrayList<>();

        for(RecipeHolder<SalvagingRecipe> recipe :  bts$findMatch(level, stack, player)) {
            for(SalvagingRecipe.OutputData d : recipe.value().getOutputs()) {
                ItemStack out = d.stack().copy();
                out.setCount(SalvagingMenu.getSalvageCounts(d, stack)[1]);
                outputs.add(out);
            }
        }

        return outputs;
    }

    @Unique
    private static List<RecipeHolder<SalvagingRecipe>>  bts$findMatch(Level level, ItemStack stack, Player player) {
        if(RMSUtils.hasRestrictionsForType(Apoth.RecipeTypes.SALVAGING)) {
            return RMSUtils.filterRecipes(level.getRecipeManager().getRecipesFor(Apoth.RecipeTypes.SALVAGING, new SingleRecipeInput(stack), level), player);
        }

        return level.getRecipeManager().getRecipesFor(Apoth.RecipeTypes.SALVAGING, new SingleRecipeInput(stack), level);
    }

}

package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.more_red;

import commoble.morered.MoreRed;
import commoble.morered.soldering.SolderingMenu;
import commoble.morered.soldering.SolderingRecipe;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SolderingMenu.class)
public abstract class SolderingMenuMixin extends AbstractContainerMenu {

    @Shadow
    @Final
    private Player player;

    protected SolderingMenuMixin(@Nullable MenuType<?> arg, int i) {
        super(arg, i);
    }

    @Redirect(method = "onPlayerChoseRecipe", at = @At(value = "INVOKE", target = "Lcommoble/morered/soldering/SolderingMenu;getSolderingRecipe(Lnet/minecraft/world/item/crafting/RecipeManager;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public @Nullable RecipeHolder<SolderingRecipe> bts$onPlayerChoseReciper$redirect(RecipeManager manager, ResourceLocation id) {
        final List<RecipeHolder<SolderingRecipe>> recipes = RMSUtils.filterRecipes( manager.getAllRecipesFor(MoreRed.get().solderingRecipeType.get()), RMSUtils.getPlayerId(player));
        for (RecipeHolder<SolderingRecipe> recipe : recipes) {
            if(recipe.id().equals(id)) return recipe;
        }
        return null;
    }
}

package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.apotheosis;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import dev.shadowsoffire.apothic_spawners.ASObjects;
import dev.shadowsoffire.apothic_spawners.block.ApothSpawnerBlock;
import dev.shadowsoffire.apothic_spawners.block.ApothSpawnerTile;
import dev.shadowsoffire.apothic_spawners.modifiers.SpawnerModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ApothSpawnerBlock.class)
public abstract class ApothSpawnerBlockMixin extends SpawnerBlock {

    protected ApothSpawnerBlockMixin(Properties arg) {
        super(arg);
    }

    @Unique
    private Player bts$player;

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apothic_spawners/modifiers/SpawnerModifier;findMatch(Ldev/shadowsoffire/apothic_spawners/block/ApothSpawnerTile;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Ldev/shadowsoffire/apothic_spawners/modifiers/SpawnerModifier;"))
    public void bts$useItemOn$redirectPre(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir) {
        bts$player = player;
    }

    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apothic_spawners/modifiers/SpawnerModifier;findMatch(Ldev/shadowsoffire/apothic_spawners/block/ApothSpawnerTile;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Ldev/shadowsoffire/apothic_spawners/modifiers/SpawnerModifier;"))
    public SpawnerModifier bts$useItemOn$redirect(ApothSpawnerTile tile, ItemStack mainhand, ItemStack offhand) {
        return bts$findMatch(tile, mainhand, offhand, bts$player);
    }

    @Unique
    private static SpawnerModifier bts$findMatch(ApothSpawnerTile tile, ItemStack mainhand, ItemStack offhand, Player player) {
        final List<RecipeHolder<SpawnerModifier>> list = new ArrayList<>();

        final RecipeType<SpawnerModifier> type = ASObjects.SPAWNER_MODIFIER.get();
        if(RMSUtils.hasRestrictionsForType(type)) {
            list.addAll(RMSUtils.filterRecipes(tile.getLevel().getRecipeManager().getAllRecipesFor(type), player));
        } else list.addAll(tile.getLevel().getRecipeManager().getAllRecipesFor(type));

        return list.stream().map(RecipeHolder::value).sorted((r1, r2) -> r1.getOffhandInput() == Ingredient.EMPTY ? (r2.getOffhandInput() == Ingredient.EMPTY ? 0 : 1) : -1).filter((r) -> r.matches(tile, mainhand, offhand)).findFirst().orElse(null);
    }
}

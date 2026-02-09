package dev.behindthescenery.sdmrecipemachinestages.network;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.compat.IRecipeUpdateListener;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public record SyncRecipesAndStagesS2C(ResourceLocation recipeTypeId, List<ResourceLocation> recipe, String stage) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncRecipesAndStagesS2C> TYPE = new Type<>(ResourceLocation.tryBuild(SdmRecipeMachineStages.MOD_ID, "sync_stages_s2c"));
    public static final StreamCodec<ByteBuf, SyncRecipesAndStagesS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, SyncRecipesAndStagesS2C::recipeTypeId,
                    ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncRecipesAndStagesS2C::recipe,
                    ByteBufCodecs.STRING_UTF8, SyncRecipesAndStagesS2C::stage,
                    SyncRecipesAndStagesS2C::new
            );

    public static void handle(SyncRecipesAndStagesS2C packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if(RMSContainer.Instance.isServer) return;

            final RecipeType<?> recipeType = BuiltInRegistries.RECIPE_TYPE.get(packet.recipeTypeId);
            if(recipeType == null)
                throw new RuntimeException("Recipe type with id '" + packet.recipeTypeId + "' not found!");

            RMSContainer.Instance.register(new RecipeBlockType(packet.stage, recipeType, packet.recipe));

            RMSMain.getListeners().forEach(IRecipeUpdateListener::updateRecipe);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

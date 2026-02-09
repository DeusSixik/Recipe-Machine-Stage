package dev.behindthescenery.sdmrecipemachinestages.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RecipeBlockType(String stageId, RecipeType<?> recipeType, List<ResourceLocation> recipes_id) implements BaseRecipeStage {

    public static final Codec<RecipeBlockType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
            Codec.STRING.fieldOf("stage").forGetter(RecipeBlockType::stageId),
            ResourceLocation.CODEC.fieldOf("recipeType").forGetter(s -> BuiltInRegistries.RECIPE_TYPE.getKey(s.recipeType)),
            Codec.list(ResourceLocation.CODEC).fieldOf("recipes").forGetter(RecipeBlockType::recipes_id)
    ).apply(instance, (s1, s2, s3) -> new RecipeBlockType(s1, BuiltInRegistries.RECIPE_TYPE.get(s2), s3)));

    public static final StreamCodec<ByteBuf, RecipeBlockType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RecipeBlockType::stageId,
            ResourceLocation.STREAM_CODEC, s -> BuiltInRegistries.RECIPE_TYPE.getKey(s.recipeType),
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeBlockType::recipes_id,
            (s1, s2, s3) -> new RecipeBlockType(s1, BuiltInRegistries.RECIPE_TYPE.get(s2), s3)
    );

    public boolean contains(ResourceLocation recipe_id) {
        return recipes_id.contains(recipe_id);
    }

    @Override
    public @NotNull String toString() {
        return "RecipeBlockType{" +
                "stageId='" + stageId + '\'' +
                ", recipeTypeId=" + recipeType +
                ", recipes_id=" + recipes_id +
                '}';
    }

}

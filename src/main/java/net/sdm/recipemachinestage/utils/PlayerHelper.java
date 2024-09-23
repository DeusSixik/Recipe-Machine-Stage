package net.sdm.recipemachinestage.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class PlayerHelper {

    @Nullable
    public static ServerPlayer getPlayerByGameProfile(MinecraftServer server, UUID id){
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if(Objects.equals(player.getGameProfile().getId(), id)) return player;
        }
        return null;
    }
}

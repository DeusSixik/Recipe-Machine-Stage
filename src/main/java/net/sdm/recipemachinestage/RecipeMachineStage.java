package net.sdm.recipemachinestage;

import com.mojang.logging.LogUtils;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sdm.recipemachinestage.capability.IOwnerableSupport;
import net.sdm.recipemachinestage.client.ClientEventHandler;
import net.sdm.recipemachinestage.compat.astages.AStagesIntegration;
import net.sdm.recipemachinestage.network.SyncStageForContainerC2S;
import net.sdm.recipemachinestage.network.SyncStageForContainerS2C;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RecipeMachineStage.MODID)
public class RecipeMachineStage {

    public static final String MODID = "recipemachinestage";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RecipeMachineStage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        Network.register();

        SupportBlockData.init();

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            if(ModList.get().isLoaded("gamestages")) {
                MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
            }
        });
    }

    @SubscribeEvent
    public void onRecipeCraft(PlayerEvent.ItemCraftedEvent event){

    }

    @SubscribeEvent
    public void onAddReload(AddReloadListenerEvent event){
        event.addListener(StageContainer.INSTANCE);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartedEvent event){
        PlayerHelper.loadData(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event){
        PlayerHelper.saveData(event.getServer());
    }

    @SubscribeEvent
    public void onLevelSave(LevelEvent.Save event){
        if(event.getLevel().getServer() != null)
            PlayerHelper.saveData(event.getLevel().getServer());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity() instanceof ServerPlayer player)
            PlayerHelper.addPlayer(player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.getEntity() instanceof ServerPlayer player)
            PlayerHelper.onPlayerLeave(player);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        if(event.getEntity() instanceof Player player) {
            BlockPos pos = event.getPos();
            BlockEntity blockEntity = event.getLevel().getBlockEntity(pos);
            if(blockEntity instanceof IOwnerableSupport support) {
                support.recipe_machine_stage$setOwner(player.getGameProfile().getId());
            }
        }

    }

    public static class Network {
        private static final String PROTOCOL_VERSION = "1.0";
        public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(RecipeMachineStage.MODID, "network"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        private static int packetId = 0;

        public static void register() {

            CHANNEL.registerMessage(packetId++,
                    SyncStageForContainerC2S.class,
                    SyncStageForContainerC2S::encode,
                    SyncStageForContainerC2S::new,
                    SyncStageForContainerC2S::handle
            );
            CHANNEL.registerMessage(packetId++,
                    SyncStageForContainerS2C.class,
                    SyncStageForContainerS2C::encode,
                    SyncStageForContainerS2C::new,
                    SyncStageForContainerS2C::handle
            );
        }

        // Отправка пакета клиенту
        public static void sendToClient(Object packet, ServerPlayer player) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }

        // Отправка пакета всем
        public static void sendToAll(Object packet) {
            CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }

        // Отправка пакета серверу
        public static void sendToServer(Object packet) {
            CHANNEL.sendToServer(packet);
        }
    }
}

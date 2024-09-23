package net.sdm.recipemachinestage;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sdm.recipemachinestage.stage.StageContainer;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RecipeMachineStage.MODID)
public class RecipeMachineStage {

    public static final String MODID = "recipemachinestage";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RecipeMachineStage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);



        SupportBlockData.init();
    }

    @SubscribeEvent
    public void onAddReload(AddReloadListenerEvent event){
        event.addListener(StageContainer.INSTANCE);
    }
}

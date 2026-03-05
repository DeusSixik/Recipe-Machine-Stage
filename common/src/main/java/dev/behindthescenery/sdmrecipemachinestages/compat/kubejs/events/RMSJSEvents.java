package dev.behindthescenery.sdmrecipemachinestages.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface RMSJSEvents {

    EventGroup GROUP = EventGroup.of("RMSEvents");
    EventHandler REGISTER = GROUP.server("register", () -> RMSStageKubeEvent.class);

}

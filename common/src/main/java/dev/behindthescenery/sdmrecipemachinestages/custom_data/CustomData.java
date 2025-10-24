package dev.behindthescenery.sdmrecipemachinestages.custom_data;

import net.minecraft.nbt.Tag;

public interface CustomData {

    Tag save();

    void load(Tag tag);

    <T> void putData(String name, T object);

    <T> T getData(String name);
}

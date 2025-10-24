package dev.behindthescenery.sdmrecipemachinestages.custom_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.UUID;

public class BlockOwnerData implements CustomData{

    public static final String OWNER_KEY = "block_owner";
    public static final UUID EMPTY = UUID.fromString("7e849c3d-276e-4e9b-968e-b07e41942850");

    protected UUID ownerId;

    public BlockOwnerData() {
        this(EMPTY);
    }

    public BlockOwnerData(UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public Tag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID(OWNER_KEY, ownerId);
        return nbt;
    }

    @Override
    public void load(Tag tag) {
        if(tag instanceof CompoundTag nbt) {
            ownerId = nbt.getUUID(OWNER_KEY);
            return;
        }

        throw new NullPointerException("Can't find owner on data!");
    }

    @Override
    public <T> void putData(String name, T object) {
        if(object == null) {
            throw new NullPointerException("Can't put data because object is null!");
        }

        this.ownerId = (UUID) object;
    }

    @Override
    public <T> T getData(String name) {
        return (T) ownerId;
    }

    public boolean isEmpty() {
        return this.ownerId.equals(EMPTY);
    }
}

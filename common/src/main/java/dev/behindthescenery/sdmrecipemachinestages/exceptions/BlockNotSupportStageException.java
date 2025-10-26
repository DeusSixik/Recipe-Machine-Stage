package dev.behindthescenery.sdmrecipemachinestages.exceptions;

public class BlockNotSupportStageException extends RuntimeException {

    public BlockNotSupportStageException() {
        super("Stages not supported!");
    }
}

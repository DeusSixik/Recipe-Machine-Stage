package dev.behindthescenery.sdmrecipemachinestages.exceptions;

public class IsNotServerPlayerException extends CraftRestrictionException {
    public IsNotServerPlayerException() {
        super("Player is not Server Player. He maybe local or fake!");
    }
}

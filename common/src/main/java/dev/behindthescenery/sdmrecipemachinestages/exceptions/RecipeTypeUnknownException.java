package dev.behindthescenery.sdmrecipemachinestages.exceptions;

public class RecipeTypeUnknownException extends RuntimeException {

    public RecipeTypeUnknownException(Class<?> original) {
        super(getMessage(original));

    }

    protected static String getMessage(Class<?> clz) {
        return "Can't assigned " + clz.getName() + " to know recipe syntax!";
    }
}

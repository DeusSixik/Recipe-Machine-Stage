package net.sdm.recipemachinestage.mixin.impl;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks constructor injection signatures without initializing Minecraft or starting Forge.
 */
class MixinRecipeManagerTest {

    /**
     * Verifies that each selected constructor accepts the mixin's callback signature.
     *
     * @throws IOException if a compiled class cannot be read
     */
    @Test
    void constructorInjection_minecraftConstructors_hasCompatibleCallback() throws IOException {
        ClassNode mixin = readClass("net/sdm/recipemachinestage/mixin/impl/MixinRecipeManager");
        ClassNode target = readClass("net/minecraft/world/item/crafting/RecipeManager");
        List<MethodNode> constructors = target.methods.stream()
                .filter(method -> method.name.equals("<init>"))
                .toList();
        boolean foundConstructorInjection = false;

        for (MethodNode handler : mixin.methods) {
            if (handler.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode annotation : handler.visibleAnnotations) {
                if (!annotation.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
                    continue;
                }
                int selectorIndex = annotation.values.indexOf("method");
                assertTrue(selectorIndex >= 0, "Injection must specify its target methods");
                @SuppressWarnings("unchecked")
                List<String> selectors = (List<String>) annotation.values.get(selectorIndex + 1);
                for (String selector : selectors) {
                    if (!selector.startsWith("<init>")) {
                        continue;
                    }
                    foundConstructorInjection = true;
                    List<MethodNode> matches = constructors.stream()
                            .filter(constructor -> selector.equals("<init>")
                                    || selector.equals("<init>" + constructor.desc))
                            .toList();
                    assertFalse(matches.isEmpty(), "Constructor selector does not exist: " + selector);
                    for (MethodNode constructor : matches) {
                        String callbackOnly = "(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V";
                        String withArguments = constructor.desc.substring(0, constructor.desc.length() - 2)
                                + callbackOnly.substring(1);
                        assertTrue(handler.desc.equals(callbackOnly) || handler.desc.equals(withArguments),
                                () -> handler.name + handler.desc + " is incompatible with RecipeManager"
                                        + constructor.desc + "; expected " + withArguments);
                    }
                }
            }
        }

        assertTrue(foundConstructorInjection, "Recipe manager registration requires a constructor injection");
    }

    /**
     * Reads compiled bytecode as a resource to avoid Minecraft's runtime initialization.
     *
     * @param internalName slash-separated JVM name of the class to read
     * @return the parsed class, including methods and injection annotations
     * @throws IOException if the class resource cannot be read
     */
    private static ClassNode readClass(String internalName) throws IOException {
        try (InputStream stream = MixinRecipeManagerTest.class.getClassLoader()
                .getResourceAsStream(internalName + ".class")) {
            assertNotNull(stream, "Missing compiled class: " + internalName);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }
}

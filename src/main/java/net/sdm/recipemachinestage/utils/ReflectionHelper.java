package net.sdm.recipemachinestage.utils;

import net.sdm.recipemachinestage.RecipeMachineStage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectionHelper {


    public static boolean canCast(Class<?> obj, Class<?> caster){
        try {
            if(obj == caster) return true;

            List<Class<?>> d1 = getParent(obj);
            if(d1.contains(caster)) return true;

            d1 = getParent(caster);
            if(d1.contains(obj)) return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static List<Class<?>> getParent(Class<?> obj){
        List<Class<?>> classes = Collections.synchronizedList(new ArrayList<>());

        for (Class<?> anInterface : obj.getInterfaces()) {
            classes.add(anInterface);
            classes.addAll(getParent(anInterface));
        }

        if(obj.getSuperclass() != null){
            classes.add(obj.getSuperclass());
            classes.addAll(getParent(obj.getSuperclass()));
        }

        return classes;
    }



}

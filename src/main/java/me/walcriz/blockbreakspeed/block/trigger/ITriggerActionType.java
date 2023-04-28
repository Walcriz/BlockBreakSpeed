package me.walcriz.blockbreakspeed.block.trigger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface ITriggerActionType<T> {
    default T createInstance(Object... args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Constructor<? extends T> constructor = getTypeClass().getConstructor(getConstructorArgs());
        return constructor.newInstance(args);
    }

    Class<? extends T> getTypeClass();
    Class[] getConstructorArgs();
}

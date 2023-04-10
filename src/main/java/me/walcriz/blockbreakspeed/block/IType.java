package me.walcriz.blockbreakspeed.block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface IType<T> {
    default T createInstance(Object... args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Constructor<? extends T> constructor = getTypeClass().getConstructor(getConstructorArgs());
        return constructor.newInstance(args);
    }

    Class<? extends T> getTypeClass();
    Class[] getConstructorArgs();
    Object[] convertSettings(Map<String, String> settingsMap);
}

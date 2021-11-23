package dev.negativekb.playtime.api.option;

@SuppressWarnings("all")
public interface Registry<T> {

    void register(T... types);

}

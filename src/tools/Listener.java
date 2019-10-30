package tools;

public interface Listener {
    default void notify(Speaker speaker){
        return;
    }
}

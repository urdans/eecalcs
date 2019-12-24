package tools;

@FunctionalInterface
public interface Listener {
    void notify(Object /*Speaker*/ speaker);
}

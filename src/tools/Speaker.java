package tools;

import java.util.ArrayList;
import java.util.List;

public interface Speaker {
    void notifyAllListeners();
    void addListener(Listener listener);
    void removeListener(Listener listener);
}

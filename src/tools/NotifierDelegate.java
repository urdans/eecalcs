package tools;

import java.util.ArrayList;
import java.util.List;
//todo javadoc
public class NotifierDelegate /*implements Speaker*/ {
    private List<Listener> listeners = new ArrayList<>();
    private Object owner;
    private boolean enable = true;

    public NotifierDelegate(Object owner){
        this.owner = owner;
    }

    public void notifyAllListeners() {
        if(enable)
            for(Listener listener: listeners)
                listener.notify(owner);
    }

    public void addListener(Listener listener) {
        if(listeners.contains(listener))
            return;
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void enabled(boolean flag){
        enable = flag;
    }

}

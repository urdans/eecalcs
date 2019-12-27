package tools;

import java.util.ArrayList;
import java.util.List;
//todo javadoc
public class NotifierDelegate {
    private List<Listener> listeners = new ArrayList<>();
    private Object sender;
    private boolean enable = true;
    public FieldInfoChangeEvent info =  new FieldInfoChangeEvent();

    public NotifierDelegate(Object sender){
        this.sender = sender;
    }

    public void notifyAllListeners() {
        if(enable)
            for(Listener listener: listeners)
                listener.notify(sender);
            info.clearInfo();
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

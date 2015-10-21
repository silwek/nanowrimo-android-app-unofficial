package nanowrimo.onishinji.model;

import de.greenrobot.event.EventBus;

public class BusManager {
    private static BusManager objLogger;
    private final EventBus mBus;

    private BusManager() {
        mBus = new EventBus();
    }

    public EventBus getBus() {
        return mBus;
    }

    public static BusManager getInstance() {
        if (objLogger == null) {
            objLogger = new BusManager();
        }
        return objLogger;
    }

}
package nanowrimo.onishinji.model;

import com.squareup.otto.Bus;

public class BusManager {
    private static BusManager objLogger;
    private final Bus mBus;

    private BusManager() {

        //ToDo here
        mBus = new Bus();

    }

    public Bus getBus() {
        return mBus;
    }

    public static BusManager getInstance() {
        if (objLogger == null) {
            objLogger = new BusManager();
        }
        return objLogger;
    }

}
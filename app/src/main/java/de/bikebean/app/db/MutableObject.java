package de.bikebean.app.db;

import java.util.List;

public class MutableObject<T extends DatabaseEntity> {

    private DatabaseEntity t;
    private volatile boolean is_set = false;
    private final DatabaseEntity nullT;
    private int position;

    public interface ListGetter {
        List<? extends DatabaseEntity> getList(String sArg, int iArg);
    }

    public MutableObject(T value) {
        nullT = value.getNullType();
        position = 0;
    }

    public MutableObject(T value, int position) {
        nullT = value.getNullType();
        this.position = position;
    }

    public DatabaseEntity getDbEntitySync(ListGetter listGetter, String sArg, int iArg) {
        new Thread(() -> {
            List<? extends DatabaseEntity> stateList = listGetter.getList(sArg, iArg);

            if (stateList.size() > position)
                set(stateList.get(position));
            else
                set(null);
        }).start();

        while (get() == getNullState())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        return get();
    }

    private void set(DatabaseEntity i) {
        this.t = i;
        is_set = true;
    }

    private DatabaseEntity get() {
        if (is_set)
            return t;
        else
            return nullT;
    }

    private DatabaseEntity getNullState() {
        return nullT;
    }
}

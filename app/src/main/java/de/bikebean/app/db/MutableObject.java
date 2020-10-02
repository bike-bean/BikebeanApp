package de.bikebean.app.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MutableObject<T extends DatabaseEntity> {

    private DatabaseEntity t;
    private List<? extends DatabaseEntity> tAll;
    private volatile boolean is_set = false;
    private final DatabaseEntity nullT;

    public interface ListGetter {
        @NonNull List<? extends DatabaseEntity> getList(String sArg, int iArg);
    }

    public interface AllItemsGetter {
        List<? extends DatabaseEntity> getAllItems();
    }

    public interface DeleteChecker {
        List<? extends DatabaseEntity> checkDelete();
    }

    public MutableObject(T value) {
        nullT = value.getNullType();
    }

    void waitForDelete(DeleteChecker deleteChecker) {
        new Thread(() -> {
            while (deleteChecker.checkDelete().size() > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            set(null);
        }).start();

        waitForStateChange();
    }

    public @Nullable DatabaseEntity getDbEntitySync(ListGetter listGetter, String sArg, int iArg) {
        new Thread(() -> {
            @NonNull List<? extends DatabaseEntity> stateList = listGetter.getList(sArg, iArg);

            if (stateList.size() > 0)
                set(stateList.get(0));
            else
                set(null);
        }).start();

        waitForStateChange();
        return get();
    }

    List<? extends DatabaseEntity> getAllItems(AllItemsGetter allItemsGetter) {
        new Thread(() -> {
            List<? extends DatabaseEntity> stateList = allItemsGetter.getAllItems();

            setAll(stateList);
        }).start();

        waitForStateChangeAll();
        return getAll();
    }

    private void waitForStateChange() {
        while (get() == getNullState())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private void waitForStateChangeAll() {
        while (!is_set)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private void set(DatabaseEntity i) {
        this.t = i;
        is_set = true;
    }

    private void setAll(List<? extends DatabaseEntity> i) {
        this.tAll = i;
        is_set = true;
    }

    private @Nullable DatabaseEntity get() {
        if (is_set)
            return t;
        else
            return nullT;
    }

    private List<? extends DatabaseEntity> getAll() {
        if (is_set)
            return tAll;
        else {
            return new ArrayList<>();
        }
    }

    private DatabaseEntity getNullState() {
        return nullT;
    }
}

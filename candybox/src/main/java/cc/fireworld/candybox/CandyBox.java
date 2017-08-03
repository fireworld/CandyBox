package cc.fireworld.candybox;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by cxx on 16-5-31.
 * xx.ch@outlook.com
 */
public class CandyBox {
    private static volatile CandyBox instance;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Collection<Eater> eaters = new ConcurrentLinkedQueue<>();
    private Reference<Pack> last;

    public static boolean register(@NonNull Eater eater) {
        return getInstance().realRegister(eater);
    }

    public static boolean unregister(@NonNull Eater eater) {
        return getInstance().realUnregister(eater);
    }

    public static void put(@NonNull Pack pack) {
        put(pack, false);
    }

    public static void put(@NonNull Pack pack, boolean disposable) {
        getInstance().realPut(pack, disposable);
    }

    public static void destroy() {
        getInstance().realDestroy();
    }

    private static CandyBox getInstance() {
        if (instance == null) {
            synchronized (CandyBox.class) {
                if (instance == null) {
                    instance = new CandyBox();
                }
            }
        }
        return instance;
    }

    private CandyBox() {
    }

    private boolean realRegister(@NonNull Eater eater) {
        boolean result = !eaters.contains(checkNotNull(eater)) && eaters.add(eater);
        if (result) {
            giveLastPack(eater);
        }
        return result;
    }

    private void giveLastPack(@NonNull Eater e) {
        Pack p;
        if (last != null && (p = last.get()) != null) {
            if (isMainThread()) {
                e.onEat(p);
            } else {
                handler.post(new SingleDispatcher(e, p));
            }
        }
    }

    private boolean realUnregister(@NonNull Eater eater) {
        return eaters.remove(checkNotNull(eater));
    }

    private void realPut(@NonNull Pack pack, boolean disposable) {
        last = new SoftReference<>(checkNotNull(pack));
        notifyEaters(disposable);
    }

    private void notifyEaters(boolean disposable) {
        if (isMainThread()) {
            multiDispatch(eaters, last, disposable);
        } else {
            handler.post(new MultiDispatcher(eaters, last, disposable));
        }
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private void realDestroy() {
        eaters.clear();
        eaters = null;
        if (last != null) {
            last.clear();
            last = null;
        }
        handler = null;
        instance = null;
    }

    private static class SingleDispatcher implements Runnable {
        private Eater eater;
        private Pack p;

        private SingleDispatcher(@NonNull Eater eater, @NonNull Pack p) {
            this.eater = eater;
            this.p = p;
        }

        @Override
        public void run() {
            eater.onEat(p);
        }
    }

    private static class MultiDispatcher implements Runnable {
        private Collection<Eater> eaters;
        private Reference<Pack> ref;
        private boolean disposable;

        private MultiDispatcher(@NonNull Collection<Eater> eaters, @NonNull Reference<Pack> ref, boolean disposable) {
            this.eaters = eaters;
            this.ref = ref;
            this.disposable = disposable;
        }

        @Override
        public void run() {
            multiDispatch(eaters, ref, disposable);
        }
    }

    public interface Eater {

        void onEat(@NonNull Pack p);
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException("Can not be null.");
        }
        return t;
    }

    private static void multiDispatch(Collection<Eater> eaters, Reference<Pack> ref, boolean disposable) {
        Pack p;
        if (ref != null && (p = ref.get()) != null) {
            for (Eater e : eaters) {
                e.onEat(p);
            }
            if (disposable) {
                ref.clear();
            }
        }
    }
}

package cc.fireworld.candybox;

import android.support.annotation.NonNull;

/**
 * Created by cxx on 16-5-31.
 * xx.ch@outlook.com
 */
public class Pack {
    @NonNull
    private String type;
    private Object candy;

    private Pack(@NonNull String type, Object candy) {
        this.type = type;
        this.candy = candy;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @SuppressWarnings(value = "unchecked")
    public <T> T getCandy() {
        return (T) candy;
    }

    @Override
    public String toString() {
        return "Pack{" +
                "type='" + type + '\'' +
                ", candy=" + candy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pack)) return false;

        Pack pack = (Pack) o;

        if (!type.equals(pack.type)) return false;
        return candy != null ? candy.equals(pack.candy) : pack.candy == null;

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (candy != null ? candy.hashCode() : 0);
        return result;
    }

    @NonNull
    public static Pack pack(@NonNull String type, @NonNull Object candy) {
        return new Pack(checkNotNull(type), checkNotNull(candy));
    }

    @NonNull
    public static Pack empty(@NonNull String type) {
        return new Pack(checkNotNull(type), null);
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException("Can not be null.");
        }
        return t;
    }
}

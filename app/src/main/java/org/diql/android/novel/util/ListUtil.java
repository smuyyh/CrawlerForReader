package org.diql.android.novel.util;

import java.util.List;

public class ListUtil {

    private ListUtil() {
        // empty.
    }

    public static void main(String[] args) {

    }

    /**
     * Returns the number of elements in this list. If the list is null, return 0.
     * @param list
     * @return the number of elements in this list.
     */
    public static int size(List list) {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static <E> E getSafely(List<E> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
}

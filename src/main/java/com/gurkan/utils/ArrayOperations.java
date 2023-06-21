package com.gurkan.utils;

import java.util.ArrayList;

import com.gurkan.interfaces.IArrayCallback;

public class ArrayOperations {
    public static <T> boolean any(ArrayList<T> arr, IArrayCallback<T> callback) {
        int index = 0;
        for (T element : arr) {
            if (callback.call(element, index)) {
                return true;
            }
            index++;
        }

        return false;
    }
}

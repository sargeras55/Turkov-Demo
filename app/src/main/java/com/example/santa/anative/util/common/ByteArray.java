package com.example.santa.anative.util.common;

import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by santa on 23.03.17.
 * Its the fast
 */

public final class ByteArray {

    private byte[] array;


    public ByteArray() {
        array = new byte[0];
    }


    public ByteArray append(byte b) {
        byte[] result = new byte[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = b;
        array = result;
        return this;
    }


    public ByteArray appendWithSplit(byte split, byte[]... arr) {
        int length = 0;
        int increment;

        for (byte[] anArr : arr) {
            length += anArr.length;
        }

        length = length + arr.length + array.length;
        if (array.length == 0) length--;

        byte[] result = new byte[length];
        System.arraycopy(array, 0, result, 0, array.length);
        increment = array.length;

        for (byte[] arrTemp : arr) {
            if (increment != 0) result[increment++] = split;
            System.arraycopy(arrTemp, 0, result, increment, arrTemp.length);
            increment += arrTemp.length;
        }

        array = result;
        return this;
    }


    public ByteArray append(byte[]... arr) {
        int length = 0;
        int increment;
        length += array.length;
        for (byte[] anArr : arr) length += anArr.length;

        System.out.println(length);
        byte[] result = new byte[length];
        increment = array.length;

        System.arraycopy(array, 0, result, 0, array.length);

        for (byte[] arrTemp : arr) {
            System.arraycopy(arrTemp, 0, result, increment, arrTemp.length);
            increment += arrTemp.length;
        }

        array = result;
        return this;
    }


    public void clear() {
        Arrays.fill(array, (byte) 0);
        array = new byte[0];
    }

    public ByteArray fillFreeRandom(int size) {
        if (size > array.length) {
            byte[] result = Arrays.copyOf(array, size);
            Arrays.fill(result, array.length, size, ByteHelper.SHARP);
            array = result;
        }
        return this;
    }

    public int size() {
        return array.length;
    }


    public byte[] array() {
        return array;
    }


    @Override
    public String toString() {
        return new String(array);
    }
}

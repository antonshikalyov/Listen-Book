package com.example.listenbook.services;

import com.example.listenbook.entities.AudioItem;

import java.util.Comparator;

public class NaturalOrderComparator implements Comparator<AudioItem> {

    // Сравниваем числовые части строк
    int compareRight(String a, String b) {
        int bias = 0;
        int ia = 0;
        int ib = 0;

        while (true) {
            char ca = charAt(a, ia);
            char cb = charAt(b, ib);

            if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
                return bias;
            } else if (!Character.isDigit(ca)) {
                return -1;
            } else if (!Character.isDigit(cb)) {
                return +1;
            } else if (ca < cb) {
                if (bias == 0) {
                    bias = -1;
                }
            } else if (ca > cb) {
                if (bias == 0) {
                    bias = +1;
                }
            } else if (ca == 0 && cb == 0) {
                return bias;
            }
            ia++;
            ib++;
        }
    }

    @Override
    public int compare(AudioItem a, AudioItem b) {
        String titleA = a.trackName;
        String titleB = b.trackName;

        int ia = 0, ib = 0;
        int nza = 0, nzb = 0;
        char ca, cb;
        int result;

        while (true) {
            // count leading zeroes
            nza = nzb = 0;

            ca = charAt(titleA, ia);
            cb = charAt(titleB, ib);

            // skip leading spaces or zeros
            while (Character.isSpaceChar(ca) || ca == '0') {
                if (ca == '0') {
                    nza++;
                } else {
                    nza = 0;
                }
                ca = charAt(titleA, ++ia);
            }

            while (Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') {
                    nzb++;
                } else {
                    nzb = 0;
                }
                cb = charAt(titleB, ++ib);
            }

            // process run of digits
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                if ((result = compareRight(titleA.substring(ia), titleB.substring(ib))) != 0) {
                    return result;
                }
            }

            if (ca == 0 && cb == 0) {
                return nza - nzb;
            }

            if (ca < cb) {
                return -1;
            } else if (ca > cb) {
                return +1;
            }

            ia++;
            ib++;
        }
    }

    // Возвращает символ по индексу или 0, если индекс выходит за пределы строки
    static char charAt(String s, int i) {
        if (i >= s.length()) {
            return 0;
        } else {
            return s.charAt(i);
        }
    }
}

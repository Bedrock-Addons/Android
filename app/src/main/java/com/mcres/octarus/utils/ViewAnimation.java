package com.mcres.octarus.utils;

import android.view.View;

public class ViewAnimation {

    public static void hideBottomBar(View view) {
        int moveX = 2 * view.getHeight();
        view.animate()
                .translationY(moveX)
                .setDuration(300)
                .start();
    }

    public static void showBottomBar(View view) {
        view.animate()
                .translationX(0)
                .setDuration(300)
                .start();
    }

    public static void hideToolbar(View view) {
        int moveX = 2 * view.getHeight();
        view.animate()
                .translationX(-moveX)
                .setDuration(300)
                .start();
    }

    public static void showToolbar(View view) {
        view.animate()
                .translationX(0)
                .setDuration(300)
                .start();
    }
}

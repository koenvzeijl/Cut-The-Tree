package cutthetree;

import javax.swing.*;

/**
 * Created by The lion kings on 17-3-2017.
 */

public class Main {
    public static void main(String[] args) {
        // write your code here

        JFrame window = new PlayFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("CutThaTree");
        window.setResizable(false);
        window.setVisible(true);
    }
}
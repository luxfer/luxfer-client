package com.adamheinrich.luxfer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.util.Locale;

public class LuxferClient {
    private JFrame frame;
    private JLuxfer luxfer;

    public LuxferClient() {
        luxfer = new JLuxfer();

        frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(luxfer);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void run() {
        frame.setVisible(true);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);

    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LuxferClient client = new LuxferClient();
                client.run();
            }
        });
    }
}

package gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class UiDialogOwner {
    private UiDialogOwner() {}

    public static Component resolve(Component parent) {
        if (parent == null) {
            Window applicationWindow = KhungChinh.getActiveWindow();
            return applicationWindow != null && applicationWindow.isShowing()
                    ? applicationWindow : null;
        }

        if (parent instanceof JFrame) {
            Container content = ((JFrame) parent).getContentPane();
            Window embeddedOwner = SwingUtilities.getWindowAncestor(content);
            if (embeddedOwner != null && embeddedOwner != parent && embeddedOwner.isShowing()) {
                return embeddedOwner;
            }
        }

        if (parent instanceof Window && ((Window) parent).isShowing()) {
            return parent;
        }

        Window ancestor = SwingUtilities.getWindowAncestor(parent);
        if (ancestor != null && ancestor.isShowing()) {
            return ancestor;
        }

        Window applicationWindow = KhungChinh.getActiveWindow();
        return applicationWindow != null && applicationWindow.isShowing()
                ? applicationWindow : parent;
    }
}

package gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

final class ButtonTooltips {
    private static final Map<Container, Boolean> INSTALLED_CONTAINERS = new WeakHashMap<>();

    private ButtonTooltips() {
    }

    static void install(Container root) {
        if (root instanceof AbstractButton) {
            apply((AbstractButton) root);
        }

        synchronized (INSTALLED_CONTAINERS) {
            if (INSTALLED_CONTAINERS.put(root, Boolean.TRUE) != null) {
                return;
            }
        }

        for (Component child : root.getComponents()) {
            if (child instanceof Container) {
                install((Container) child);
            } else if (child instanceof AbstractButton) {
                apply((AbstractButton) child);
            }
        }

        root.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent event) {
                Component child = event.getChild();
                if (child instanceof Container) {
                    install((Container) child);
                } else if (child instanceof AbstractButton) {
                    apply((AbstractButton) child);
                }
            }
        });
    }

    static void apply(AbstractButton button) {
        if (button instanceof JMenuItem) {
            return;
        }
        String currentTooltip = button.getToolTipText();
        if (currentTooltip != null && !currentTooltip.trim().isEmpty()) {
            return;
        }

        String label = button.getText();
        if (label == null || label.trim().isEmpty()) {
            label = button.getAccessibleContext().getAccessibleName();
        }
        button.setToolTipText(describe(label));
    }

    private static String describe(String label) {
        if (label == null || label.trim().isEmpty()) {
            return "Thao tác";
        }

        String text = label.trim();
        String actionLabel = text.replaceFirst("^[^\\p{L}\\p{N}]+", "").trim();
        String normalized = text.toLowerCase(Locale.forLanguageTag("vi-VN"));
        if (normalized.contains("🔒")) {
            return "Khóa/mở tài khoản";
        }
        if (normalized.contains("✎") || normalized.contains("✏")) {
            return "Sửa tài khoản";
        }
        if (normalized.contains("🗑") || normalized.equals("▣")) {
            return "Xóa mục";
        }
        if (normalized.contains("in máy in") || normalized.contains("in hóa đơn")) {
            return "In hóa đơn";
        }
        if (normalized.contains("xuất excel")) {
            return "Xuất Excel";
        }
        if (normalized.contains("xuất pdf")) {
            return "Xuất PDF";
        }
        if (normalized.contains("trả đủ tiền")) {
            return "Điền đủ tiền";
        }
        if (normalized.contains("thêm vào hóa đơn")) {
            return "Thêm vào hóa đơn";
        }
        if (normalized.contains("cập nhật số lượng")) {
            return "Cập nhật số lượng";
        }
        if (normalized.contains("xóa sản phẩm")) {
            return "Xóa sản phẩm";
        }
        if (normalized.contains("tìm")) {
            return "Tìm kiếm";
        }
        if (normalized.contains("chi tiết") || normalized.contains("xem")) {
            return "Xem chi tiết";
        }
        if (normalized.contains("sửa") || normalized.contains("cập nhật")) {
            return "Cập nhật mục";
        }
        if (normalized.contains("xóa")) {
            return "Xóa mục";
        }
        if (normalized.contains("làm mới") || normalized.contains("refresh")) {
            return "Làm mới";
        }
        if (normalized.contains("đăng nhập")) {
            return "Đăng nhập";
        }
        if (normalized.contains("đăng xuất")) {
            return "Đăng xuất";
        }
        if (normalized.contains("đổi mật khẩu")) {
            return "Đổi mật khẩu";
        }
        if (normalized.equals("hiện")) {
            return "Hiện/ẩn mật khẩu";
        }
        if (normalized.equals("quản lý") || normalized.equals("nhân viên")) {
            return "Vai trò tài khoản";
        }
        if (normalized.contains("quản lý")) {
            return actionLabel;
        }
        if (normalized.contains("phân công")) {
            return "Phân công ca";
        }
        if (normalized.contains("thêm")) {
            return actionLabel;
        }
        if (normalized.equals("hóa đơn mới")) {
            return "Tạo hóa đơn mới";
        }
        if (normalized.contains("tạo")) {
            return actionLabel;
        }
        if (normalized.contains("lưu")) {
            return actionLabel;
        }
        if (normalized.contains("hủy")) {
            return "Hủy thao tác";
        }
        if (normalized.contains("đóng") || normalized.contains("thoát")
                || normalized.contains("quay lại") || normalized.equals("×")) {
            return "Đóng";
        }
        if (normalized.contains("thu nhỏ")) {
            return "Thu nhỏ";
        }
        if (normalized.equals("⚙")) {
            return "Quản lý danh mục";
        }
        return actionLabel;
    }
}

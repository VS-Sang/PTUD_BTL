package DAO;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public final class VaiTroQuyen_DAO {
    private VaiTroQuyen_DAO() {
    }

    public static Set<String> layMaQuyenTheoVaiTro(String vaiTro) throws SQLException {
        Set<String> permissions = new HashSet<>();
        String sql = "SELECT maQuyen FROM VaiTroQuyen WHERE vaiTro = ?";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vaiTro);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    permissions.add(resultSet.getString("maQuyen"));
                }
            }
        }
        return permissions;
    }
}

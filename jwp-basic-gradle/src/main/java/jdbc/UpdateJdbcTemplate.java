package jdbc;

import dao.UserDao;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateJdbcTemplate {
    public void update(User user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = createQueryForUpdate();
            ps = con.prepareStatement(sql);
            setValuesForUpdate(user, ps);

            ps.executeUpdate();
        } finally{
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }


    private void setValuesForUpdate(User user, PreparedStatement ps) throws SQLException {
        ps.setString(1, user.getPassword());
        ps.setString(2, user.getName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getUserId());
    }

    private   String createQueryForUpdate(){
        return "UPDATE USERS SET password = ?, name = ?, email = ? WHERE userId = ?";
    }
}

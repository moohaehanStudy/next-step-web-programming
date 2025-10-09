package jdbc;

import dao.UserDao;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertJdbcTemplate {
    public void insert(User user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = createQueryForInsert();
            ps = con.prepareStatement(sql);
            setValuesForInsert(user, ps);

            ps.executeUpdate();
        } finally{
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }

    private void setValuesForInsert(User user, PreparedStatement ps) throws SQLException {
        ps.setString(1, user.getUserId());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getName());
        ps.setString(4, user.getEmail());
    }

    private String createQueryForInsert(){
        return "INSERT INTO USERS VALUES (?, ?, ?, ?)";
    }
}

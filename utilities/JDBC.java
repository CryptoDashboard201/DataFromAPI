package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class JDBC {
	public void saveLink(double x, double y) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();;
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "INSERT INTO BitcoinOwnerMetric(x,y) VALUES(?,?)";
		
        try (
        	Connection connection = this.connect();
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(sql)) {
        	pstmt.setDouble(1, x);
        	pstmt.setDouble(2, y);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	private Connection connect() {
		String urlMySQL = "jdbc:mysql://localhost/mazumah?user=root&password=root";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(urlMySQL);
			
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		}
		return connection;
	}
}

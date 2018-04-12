import java.sql.*;

public class Gradation { // 创建类
	static Connection con; // 声明Connection对象
	static Statement sql; // 声明Statement对象
	static ResultSet res; // 声明ResultSet对象
	
	public Connection getConnection() { // 连接数据库方法

		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:jtds:sqlserver://localhost:1433/"
							+ "WoChat_user", "sa", "abc3765787");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con; // 返回Connection对象
	}
	
	public static void main(String[] args) { // 主方法
		Gradation c = new Gradation(); // 创建本类对象
		con = c.getConnection(); // 与数据库建立连接
		try {
			sql = con.createStatement(); // 实例化Statement对象
			// 执行SQL语句，返回结果集
			res = sql.executeQuery("select * from user_name_pass");
			while (res.next()) { // 如果当前语句不是最后一条则进入循环				
				// 获取列名是"name"的字段值
				String name = res.getString("name");
				// 获取列名是"password"的字段值
				String pass = res.getString("password");
				
				System.out.print(" 姓名:" + name);
				System.out.print(" 密码:" + pass+"\n");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

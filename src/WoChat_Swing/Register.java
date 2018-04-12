package WoChat_Swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class Register {

	private JFrame frame;
	private JTextField textField;
	private JPasswordField passFiled_1;
	private JPasswordField passFiled_2;
	public String user_name;
	public String password_1;
	public String password_2;

	static Connection con; // 声明Connection对象
	static PreparedStatement sql; // 声明Statement对象
	static ResultSet res; // 声明ResultSet对象
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Register window = new Register();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Register() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("用户名：");
		label.setBounds(75, 60, 72, 18);
		frame.getContentPane().add(label);
				
		JLabel lblNewLabel = new JLabel("请输入密码");
		lblNewLabel.setBounds(70, 100, 97, 18);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("再次输入密码");
		lblNewLabel_1.setBounds(70, 140, 97, 18);
		frame.getContentPane().add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(179, 56, 150, 24);
		frame.getContentPane().add(textField);
		textField.setColumns(10);	
		
		passFiled_1 = new JPasswordField();
		passFiled_1.setColumns(10);
		passFiled_1.setBounds(178, 98, 150, 24);
		frame.getContentPane().add(passFiled_1);
		
		passFiled_2 = new JPasswordField();
		passFiled_2.setColumns(10);
		passFiled_2.setBounds(179, 135, 150, 24);
		frame.getContentPane().add(passFiled_2);		
		
		JButton button = new JButton("取消");
		button.setBounds(72, 189, 113, 27);
		//button.setActionCommand(actionCommand);
		button.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				//System.exit(0);
				frame.dispose();
			}
		});
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("注册");
		button_1.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				user_name=textField.getText();
				password_1=String.valueOf(passFiled_1.getPassword());
				password_2=String.valueOf(passFiled_2.getPassword());
				if(user_name.trim().length()==0||new String(passFiled_1.getPassword()).trim().length()==0){
					JOptionPane.showMessageDialog(null, "用户名密码不允许为空");
					return;
				}
				if(password_1.equals(password_2)==false){
					JOptionPane.showMessageDialog(null, "密码不同");
				}
				
				Gradation c = new Gradation(); // 创建Gradation对象
				con = c.getConnection(); // 与数据库建立连接
				try {
					sql = con.prepareStatement("select * from user_name_pass where name='"+user_name+"';"); // 查询数据库
					res = sql.executeQuery(); // 执行SQL语句
					if(res.next()){
						JOptionPane.showMessageDialog(null, "用户名已存在");
						return;
					}
					sql = con.prepareStatement("select * from user_name_pass"); // 查询数据库
					res = sql.executeQuery(); // 执行SQL语句

					sql = con.prepareStatement("insert into user_name_pass values(?,?)");
					sql.setString(1, user_name); // 预处理添加数据
					sql.setString(2, password_1);
					sql.executeUpdate();

				} catch (Exception e) {
					e.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, "注册成功");		
				frame.dispose();
			}
		});
		button_1.setBounds(217, 188, 113, 27);
		frame.getContentPane().add(button_1);
		frame.setVisible(true);
		
	}
}

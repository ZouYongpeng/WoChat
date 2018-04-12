package WoChat_Swing;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;

import WoChat_Form.ClientForm;

public class Login extends JFrame{

	private static final long serialVersionUID = 1L;
	String user_name;
	String user_password;
	
	public Login(){		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("WoChat - 登录");
		//窗口定位
//		Toolkit tk=Toolkit.getDefaultToolkit();
//		int width=(int) tk.getScreenSize().getWidth();
//		int height=(int) tk.getScreenSize().getHeight();
//		setBounds(width/4, height/4, width/2, height/2);
		setBounds(400, 300, 600, 400);
		
		Container cp=getContentPane();
		cp.setLayout(null);
		JLabel jl=new JLabel("用户名：");
		jl.setBounds(120, 70, 300, 25);
		final JTextField name=new JTextField();
		name.setBounds(180, 70, 300, 25);
		JLabel jl2=new JLabel("密码：");
		jl2.setBounds(120, 120, 300, 25);
		final JPasswordField password=new JPasswordField();
		password.setBounds(180, 120, 300, 25);
		cp.add(jl);
		cp.add(name);
		cp.add(jl2);
		cp.add(password);
		
		user_name=name.getText();
		user_password=String.valueOf(password.getPassword());
		System.out.println(user_name+"\n"+user_password);
		JButton jb_login=new JButton("登陆");
		jb_login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(name.getText().trim().length()==0||new String(password.getPassword()).trim().length()==0){
					JOptionPane.showMessageDialog(null, "用户名密码不允许为空");
					return;
				}
				Connection con; // 声明Connection对象
				PreparedStatement sql; // 声明PreparedStatement对象
				ResultSet res; // 声明ResultSet对象
				Gradation c = new Gradation(); // 创建Gradation对象
				con = c.getConnection(); // 与数据库建立连接
				try {
					sql = con.prepareStatement("select * from user_name_pass where name='"+name.getText()+"';"); // 查询数据库
					res = sql.executeQuery(); // 执行SQL语句
					if(!res.next()){
						JOptionPane.showMessageDialog(null, "用户名不存在");
						name.setText("");
						password.setText("");
						return;
					}
					sql = con.prepareStatement("select * from user_name_pass where name='"+name.getText()+"' and password ='"+new String(password.getPassword())+"';"); // 查询数据库
					res = sql.executeQuery(); // 执行SQL语句
					if(res.next()){
						JOptionPane.showMessageDialog(null, "登陆成功");
						new ClientForm(name.getText());
						//dispose();
						return;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		jb_login.setBounds(120, 180, 60, 18);
		cp.add(jb_login);

		final JButton jb_reset = new JButton();
		jb_reset.setText("重置");
		jb_reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成方法存根
				name.setText("");
				password.setText("");
			}
		});
		jb_reset.setBounds(210, 180, 60, 18);
		getContentPane().add(jb_reset);
		
		final JButton jb_register = new JButton();
		jb_register.setText("注册");
		jb_register.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成方法存根
				new Register();
				
			}
		});
		jb_register.setBounds(300, 180, 60, 18);
		getContentPane().add(jb_register);
		
		final JButton jb_about = new JButton();
		jb_about.setText("关于");
		jb_about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成方法存根
				JOptionPane.showMessageDialog(null, "WoChat-0.0.1"+"\n@author : 邹永鹏 & 林锡浩"+"\n此版本仅供试用!");
			}
		});
		jb_about.setBounds(390, 180, 60, 18);
		getContentPane().add(jb_about);
		
		setVisible(true);//窗口可见
	}
	
	public static void main(String[] args) {
		new Login();

	}

}

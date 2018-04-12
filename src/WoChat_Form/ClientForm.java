package WoChat_Form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ClientForm extends JFrame implements ActionListener {
	//private static final long serialVersionUID = -467220467619466695L;
	private JList<String> list;//在线列表
    private DefaultListModel<String> lm;
    private JTextArea allMsg;// 聊天消息框
    private JTextField sendMsg;//消息
    private JButton btnSend;
    public int state;//在线状态，在线为1，拒聊为0
    
    public Date date;
    public String time;

    public String user_name;

    private static String HOST;
    //private static String HOST = "192.168.124.1";// 自己机子，服务器的ip地址
    //private static String HOST = InetAddress.getLocalHost().getHostAddress();// 自己机子，服务器的ip地址
    private static int PORT = 9090;// 服务器的端口号
    private Socket clientSocket;
    private PrintWriter pw;
    
    public ClientForm() {
    	
    }

    public ClientForm(final String user_name) {
        super("WoChat - "+user_name);
        this.user_name=user_name;
        this.state=1;//默认在线状态
//    public ClientForm() {    	
//        super("WoChat - text");
        //获取客户端的ip地址
        InetAddress ip;// 创建InetAddress对象
    	try {
    		ip = InetAddress.getLocalHost(); // 实例化对象
    		String localname = ip.getHostName(); // 获取本机名
    		HOST = ip.getHostAddress(); // 获取本IP地址
    		System.out.println("本机名：" + localname);// 将本机名输出
    		System.out.println("本机IP地址：" + HOST); // 将本机IP输出
    	} catch (UnknownHostException e) {
    		e.printStackTrace(); // 输出异常信息
    	}
   
        // 菜单条
        addJMenu();

        // 中间的面板
        JPanel cenP = new JPanel(new BorderLayout());
        this.getContentPane().add(cenP, BorderLayout.CENTER);

        // 在线列表
        lm = new DefaultListModel<String>();
        list = new JList<String>(lm);
        lm.addElement("全部");
        //list.setSelectedIndex(0);// 设置默认显示
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 只能选中一行
        list.setVisibleRowCount(8);//指示要显示的首选行数（不要求滚动）,默认为8        
        
        JScrollPane js = new JScrollPane(list);//包含列表的带滚动条的面板
        Border border = new TitledBorder("在线列表");
        js.setBorder(border);
        Dimension preferredSize = new Dimension(80, cenP.getHeight());
        js.setPreferredSize(preferredSize);
        cenP.add(js, BorderLayout.EAST);

        // 聊天消息框
        allMsg = new JTextArea();
        allMsg.setEditable(false);//设为不可编辑
        cenP.add(new JScrollPane(allMsg), BorderLayout.CENTER);

        // 消息发送面板
        JPanel p3 = new JPanel();
        JLabel jlmes = new JLabel("消息:");
        p3.add(jlmes);
        sendMsg = new JTextField(20);
        p3.add(sendMsg);
        btnSend = new JButton("发送");
        //btnSend.setEnabled(false);
        btnSend.setActionCommand("send");
        btnSend.addActionListener(this);
        p3.add(btnSend);
        this.getContentPane().add(p3, BorderLayout.SOUTH);

        // 右上角的X-关闭按钮-添加事件处理
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (pw == null) {
                    System.exit(0);
                }
                String msg = "exit//全部//null//" + user_name;
                pw.println(msg);
                pw.flush();
                //System.exit(0);
                dispose();
            }
        });

        setBounds(300, 300, 400, 300);
        setVisible(true);
        connecting();// 连接服务器的动作
    }

    private void addJMenu() {
    	JPanel p = new JPanel();
        JButton jb_set=new JButton("设置");
        jb_set.setActionCommand("set");
    	JButton jb_help=new JButton("关于");
    	jb_help.setActionCommand("about");
    	final JButton jb_refuse=new JButton("拒聊");
    	jb_refuse.setActionCommand("refuse");
        JButton jb_exit = new JButton("退出");
        jb_exit.setActionCommand("exit");
        jb_exit.addActionListener(this);
        p.add(jb_set);
        p.add(jb_help);
        p.add(jb_refuse);
    	p.add(jb_exit);
    	getContentPane().add(p, BorderLayout.NORTH);

    	jb_set.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JDialog dlg = new JDialog(ClientForm.this);// 弹出一个界面
                // 不能直接用this
                dlg.setBounds(ClientForm.this.getX()+30, ClientForm.this.getY()+40,350, 150);
                dlg.setLayout(new FlowLayout());
                dlg.add(new JLabel("服务器IP和端口:"));
                
                //显示服务器IP的文本框
                final JTextField tfdHost = new JTextField(10);
                tfdHost.setText(ClientForm.HOST);
                dlg.add(tfdHost);

                dlg.add(new JLabel(":"));
                //显示服务器端口的文本框
                final JTextField tfdPort = new JTextField(5);
                tfdPort.setText(""+ClientForm.PORT);
                dlg.add(tfdPort);
                dlg.setVisible(true);//显示出来

                JButton btnSet = new JButton("设置");
                dlg.add(btnSet);
                btnSet.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String ip = tfdHost.getText();//解析并判断ip是否合法
                        String strs[] = ip.split("\\.");//以‘.’拆分字符串
                        if(strs==null||strs.length!=4){
                            JOptionPane.showMessageDialog(ClientForm.this, "IP类型有误！");
                            return ;
                        }
                        try {
                            for(int i=0;i<4;i++){
                                int num = Integer.parseInt(strs[i]);//强制转换
                                if(num>255||num<0){
                                    JOptionPane.showMessageDialog(ClientForm.this, "IP类型有误！");
                                    return ;
                                }                                
                            }
                        } catch (NumberFormatException e2) {
                            JOptionPane.showMessageDialog(ClientForm.this, "IP类型有误！");
                            return ;
                        }

                        ClientForm.HOST=tfdHost.getText();//更新无误的ip类型

                        try {
                            int port = Integer.parseInt( tfdPort.getText() );
                            if(port<0||port>65535){
                                JOptionPane.showMessageDialog(ClientForm.this, "端口范围有误！");
                                return ;
                            }
                        } catch (NumberFormatException e1) {
                            JOptionPane.showMessageDialog(ClientForm.this, "端口类型有误！");
                            return ;
                        }                        
                        ClientForm.PORT=Integer.parseInt( tfdPort.getText() );//更新无误的端口
                        dlg.dispose();//关闭这个界面
                    }
                });
            }
        });

    	jb_help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(null, "WoChat-0.0.1"+"\n@author : 邹永鹏 "+"\n此版本仅供试用!");
            }
        });
    	jb_refuse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(state==1){
            		state=0;//拒聊状态
                	jb_refuse.setText("拒聊中");
                	return;
            	}
            	if(state==0){
            		state=1;//在线状态
                	jb_refuse.setText("可接收");
                	return;
            	}
            }
    	});
    	jb_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	//先把自己在线的菜单清空
                lm.removeAllElements();//移除列表所有组件，并将它们的大小设置为零。 

                sendExitMsg();// 向服务器发送退出消息
            }
        });

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("send")) {
            if (sendMsg.getText() == null|| sendMsg.getText().trim().length() == 0) {
            	JOptionPane.showMessageDialog(this, "发送消息不能为空");
                return;
            }
            if((state==1)||(state==0 && list.getSelectedValue().equals("全部"))){
            	date=new Date();
                time=String.format("%tT", date);
                String msg = "on//" + list.getSelectedValue() /*好友名字*/+ "//"
                        + sendMsg.getText() /*聊天信息*/+ "//" + user_name/*自己的用户名*/ ;
                pw.println(msg);
                pw.flush();
                allMsg.append("\r\n" +time+"   你对["+list.getSelectedValue()+"]说："+sendMsg.getText());
                sendMsg.setText("");// 将发送消息的文本设为空          
            }
            else{
            	JOptionPane.showMessageDialog(this, "你当前为拒聊状态，请切换为在线状态接受私聊信息");
            }
        } else if (e.getActionCommand().equals("exit")) {
            //先把自己在线的菜单清空
            lm.removeAllElements();//移除列表所有组件，并将它们的大小设置为零。 

            sendExitMsg();// 向服务器发送退出消息
            dispose();
        }
    }

    // 向服务器发送退出消息
    private void sendExitMsg() {
        String msg = "exit//全部//null//" + user_name;
        System.out.println("退出:" + msg);
        pw.println(msg);
        pw.flush();
    }

    private void connecting() {
        try {
            String userName = user_name;

            clientSocket = new Socket(HOST, PORT);// 跟服务器握手
            pw = new PrintWriter(clientSocket.getOutputStream(), true);// 加上自动刷新
            pw.println(userName);// 向服务器报上自己的用户名
            pw.flush();
            this.setTitle("WoChat - " + userName );

            new ClientThread().start();// 接受服务器发来的消息---一直开着的
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接受服务端发过来的
    class ClientThread extends Thread {
        public void run() {
            try {
                Scanner sc = new Scanner(clientSocket.getInputStream());
                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    String msgs[] = str.split("//");
                    System.out.println(user_name + " 说: " + str);
                    if ("msg".equals(msgs[0])) {//以msg开头
                        if ("server".equals(msgs[1])) {// 服务器发送的官方消息
                            str = "[ 通知 ]:" + msgs[2];
                        } else {// 服务器转发的聊天消息
                        	if(state==1)
                        	{
                        		str = "[ " + msgs[1] + " ]说: " + msgs[2];
                        	    allMsg.append("\r\n" +time +"   "+str);
                        	}
                        	if(state==0)
                        		allMsg.append("\r\n" +time +"   你已拒收["+ msgs[1] +"]的一条消息！");                           
                        }
//                        allMsg.append("\r\n" +time +"   "+str);
                    }
                    if ("cmdAdd".equals(msgs[0])) {//以cmdAdd开头
                        boolean eq = false;//判断是否在线
                        for (int i = 0; i < lm.getSize(); i++) {//遍历在线列表
                            if (lm.getElementAt(i).equals(msgs[2])) {
                                eq = true;
                            }
                        }
                        if (!eq) {//
                            lm.addElement(msgs[2]);// 用户上线--添加
                        }
                    }
                    if ("cmdRed".equals(msgs[0])) {//以cmdRed开头
                        lm.removeElement(msgs[2]);// 用户离线了--移除
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        
        new ClientForm();
    }
}
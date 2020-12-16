package com.qst.dms.ui;

import com.qst.dms.entity.*;
import com.qst.dms.net.DmsNetServer;
import com.qst.dms.service.LogRecService;
import com.qst.dms.service.TransportService;
import com.qst.dms.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LoginFrame extends JFrame {
    private JPanel p;
    private JLabel lblName, lblPwd;
    private JTextField txtName;
    private JPasswordField txtPwd;
    private JButton btnLogin, btnReset, btnRegister;

    private UserService userService;

    public LoginFrame() {

        super("用户注册");

        userService = new UserService();

        p = new JPanel(new GridLayout(3, 1));

        lblName = new JLabel("用户名：");
        lblPwd  = new JLabel("密  码：");
        txtName = new JTextField(16);
        txtPwd = new JPasswordField(16);

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");
        btnReset = new JButton("Reset");

        btnLogin.addActionListener(new LoginListener());
        btnRegister.addActionListener(new RegisterListener());
        btnReset.addActionListener(new ResetListener());

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1.add(lblName);
        p1.add(txtName);
        p.add(p1);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2.add(lblPwd);
        p2.add(txtPwd);
        p.add(p2);

        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p3.add(btnLogin);
        p3.add(btnReset);
        p3.add(btnRegister);

        p.add(p3);

        this.add(p);
        this.setSize(310, 200);
        this.setLocation(300, 200);
        // 设置窗体不可改变大小
        this.setResizable(false);

        // 设置窗体初始可见
        this.setVisible(true);
    }

    private class RegisterListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new RegistFrame();
        }
    }

    // 监听类，负责处理确认按钮的业务逻辑
    private class LoginListener implements ActionListener {
        // 重写actionPerFormed()方法，事件处理方法
        public void actionPerformed(ActionEvent e) {
            // 获取用户输入的数据
            String userName = txtName.getText().trim();
            String password = new String(txtPwd.getPassword());
            password = MD5.getMD5(password);
            User tempUser = userService.findUserByName(userName);
            if (tempUser == null) {
                JOptionPane.showMessageDialog(null, "无此用户,建议先注册","Login Failed",JOptionPane.ERROR_MESSAGE);
            } else {
                if (tempUser.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(null,"Login Success！","Welcome",JOptionPane.PLAIN_MESSAGE);
                    new DmsNetServer();
                    new MainFrametest2();
                } else {
                    JOptionPane.showMessageDialog(null,"Wrong Password","Login Failed!",JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    public class ResetListener implements ActionListener {
        // 重写actionPerFormed()方法，重置组件内容事件处理方法
        public void actionPerformed(ActionEvent e) {
            // 清空姓名、密码、确认密码内容
            txtName.setText("");
            txtPwd.setText("");
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }

}

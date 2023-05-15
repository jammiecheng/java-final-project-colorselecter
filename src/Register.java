import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Register extends JFrame {
    public Register() {
        setTitle("final project - 註冊");

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();

        JLabel nameLabel = new JLabel("請輸入使用者名稱：");
        JTextField nameArea = new JTextField(10);
        panel1.add(nameLabel);
        panel1.add(nameArea);

        JLabel passwdLabel = new JLabel("請輸入使用者密碼：");
        JPasswordField passwordField = new JPasswordField(12);
        panel2.add(passwdLabel);
        panel2.add(passwordField);

        JLabel passwdCheckLabel = new JLabel("再輸入一次：");
        JPasswordField passwordCheckField = new JPasswordField(12);
        panel3.add(passwdCheckLabel);
        panel3.add(passwordCheckField);

        JButton btn1 = new JButton("註冊");
        btn1.addActionListener(e -> {
            if (nameArea.getText().isEmpty() || passwordField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "帳號或密碼為空", "錯誤訊息", JOptionPane.WARNING_MESSAGE);
            } else if (Login.collection.find(Filters.eq("username", nameArea.getText())).first() != null) {
                JOptionPane.showMessageDialog(this, "帳號已存在", "錯誤訊息", JOptionPane.WARNING_MESSAGE);
            } else if (!Objects.equals(passwordField.getText(), passwordCheckField.getText())) {
                JOptionPane.showMessageDialog(this, "密碼不一致，請重新輸入", "錯誤訊息", JOptionPane.WARNING_MESSAGE);
            } else {
                Document document = new Document("username", nameArea.getText()).append("password", passwordField.getText());
                Login.collection.insertOne(document);
                JOptionPane.showMessageDialog(this, "註冊成功", "註冊成功", JOptionPane.INFORMATION_MESSAGE);

                new Login();
                setVisible(false);
            }
        });
        JButton btn2 = new JButton("取消");
        btn2.addActionListener(e -> {
            new Login();
            setVisible(false);
        });
        panel4.add(btn1);
        panel4.add(btn2);

        add(panel1);
        add(panel2);
        add(panel3);
        add(panel4);

        setVisible(true);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4,1));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

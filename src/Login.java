import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.miginfocom.swing.MigLayout;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private MongoClient mongoClient;
    private MongoDatabase db;
    static MongoCollection<Document> collection;
    private Document document;
    Bson nameFilter, pwdFilter;
    JLabel nameLabel, passwdLabel;
    static JTextField nameArea;
    static JPasswordField pwdArea;
    JButton btn1, btn2;

    public Login() {
        setTitle("final project - 登入");
        setLayout(new MigLayout());

        connectToDB();

        JPanel panel1 = new JPanel(new MigLayout());
        JPanel panel2 = new JPanel(new MigLayout());
        JPanel panel3 = new JPanel(new MigLayout());

        nameLabel = new JLabel("使用者名稱：");
        nameArea = new JTextField(10);
        panel1.add(nameLabel);
        panel1.add(nameArea, "wrap");

        passwdLabel = new JLabel("使用者密碼：");
        pwdArea = new JPasswordField(12);
        panel2.add(passwdLabel);
        panel2.add(pwdArea, "wrap");

        btn1 = new JButton("登入");
        btn1.addActionListener(e -> {
            if (nameArea.getText().isEmpty() || pwdArea.getText().isEmpty()) { //檢查欄位是否為空
                JOptionPane.showMessageDialog(this, "帳號或密碼為空", "錯誤訊息", JOptionPane.WARNING_MESSAGE);
            } else { //檢查名稱是否註冊過
                nameFilter = Filters.eq("username", nameArea.getText());
                document = collection.find(nameFilter).first();
                if (document == null) { //假如名稱不存在則代表未註冊過
                    JOptionPane.showMessageDialog(this, "帳號不存在，請先註冊", "錯誤訊息", JOptionPane.WARNING_MESSAGE);
                } else { //檢查密碼是否與使用者名稱匹配
                    pwdFilter = Filters.and(Filters.eq("username", nameArea.getText()), Filters.eq("password", pwdArea.getText()));
                    document = collection.find(pwdFilter).first();
                    if (document == null) { //假如使用者名稱與密碼不匹配則代表輸入錯誤
                        JOptionPane.showMessageDialog(this, "帳號或密碼錯誤", "錯誤訊息", JOptionPane.WARNING_MESSAGE);
                    } else {
                        new Homepage();
                        setVisible(false);
                    }
                }
            }
        });
        btn2 = new JButton("註冊");
        btn2.addActionListener(e -> {
            new Register();
            setVisible(false);
        });
        panel3.add(btn1);
        panel3.add(btn2);

        add(panel1, "wrap");
        add(panel2, "wrap");
        add(panel3, "center");

        setVisible(true);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void connectToDB() {
        mongoClient = new MongoClient("localhost", 27017);
        db = mongoClient.getDatabase("test");
        collection = db.getCollection("finalproject");
    }

//    public static void getDocument() {
//        Bson nameFilter = Filters.eq("username", nameArea.getText());
//        document = collection.find(nameFilter).first();
//    }
}

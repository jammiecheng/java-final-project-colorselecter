import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import net.miginfocom.swing.MigLayout;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class Homepage extends JFrame {
    private BufferedImage image;
    Bson pwdFilter = Filters.and(Filters.eq("username", Login.nameArea.getText()), Filters.eq("password", Login.pwdArea.getText()));

    public Homepage() {
        setTitle("final project - 首頁");
        setVisible(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new MigLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setMenu();

        JPanel panel1 = new JPanel(new MigLayout());
        panel1.setPreferredSize(new Dimension(900,100));
        JPanel panel2 = new JPanel(new MigLayout());
        panel2.setPreferredSize(new Dimension(300,400));
        JPanel panel3 = new JPanel(new MigLayout());
        panel3.setPreferredSize(new Dimension(300,400));
        JPanel panel4 = new JPanel(new MigLayout());
        panel4.setPreferredSize(new Dimension(300,400));

        JLabel header = new JLabel("歡迎來到色彩擷取器");
        header.setFont(new Font("Arial", Font.BOLD, 30));
        panel1.add(header);

        JButton btn1 = new JButton("選擇檔案");
        JLabel selected = new JLabel("未選擇檔案");
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(300,300));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        JLabel colorBG = new JLabel("#ffffff", SwingConstants.CENTER);
        colorBG.setOpaque(true);
        colorBG.setPreferredSize(new Dimension(300,300));
        colorBG.setBackground(Color.WHITE);
        colorBG.setFont(new Font("Arial", Font.BOLD, 20));
        JList<Object> colorList = new JList<>();
        btn1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("圖片檔","jpg", "png");
            fileChooser.addChoosableFileFilter(filter);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                selected.setText("已選擇檔案：" + file.getName());
                imageLabel.setIcon(new ImageIcon(getImage(file).getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH)));
                DefaultListModel<Object> listModel = new DefaultListModel<>();
                if (listModel.size() > 5) {
                    colorBG.setText("#ffffff");
                    colorBG.setBackground(Color.WHITE);
                    listModel.removeRange(0,4);
                }
                for (String s : getImageHEX(getImage(file))) {
                    listModel.addElement(s);
                }
                colorList.addListSelectionListener(ex -> {
                    if (colorList.getSelectedIndex() > -1) {
                        int[] colorArr = hexToRGB(getImageHEX(getImage(file)).get(colorList.getSelectedIndex()));
                        colorBG.setText("#" + colorList.getSelectedValue());
                        colorBG.setBackground(new Color(colorArr[0], colorArr[1], colorArr[2]));
                    }
                });
                colorList.setModel(listModel);
            }
            panel3.add(colorList, "center");
        });
        colorList.setFont(new Font("Arial", Font.PLAIN, 15));
        JButton btn2 = new JButton("加入最愛");
        btn2.addActionListener(e -> {
            Bson update = Updates.addToSet("isFavorite", colorBG.getText());
            UpdateOptions options = new UpdateOptions().upsert(true);
            Login.collection.updateOne(pwdFilter, update, options);
        });
        panel2.add(btn1, "split 2");
        panel2.add(selected, "wrap");
        panel2.add(imageLabel);

        JLabel colorTitle = new JLabel("主要顏色");
        colorTitle.setFont(new Font("Arial", Font.BOLD, 20));
        panel3.add(colorTitle, "wrap");

        panel4.add(colorBG, "wrap");
        panel4.add(btn2, "center");

        add(panel1, "north");
        add(panel2);
        add(panel3);
        add(panel4);

    }
    public BufferedImage getImage(File file) {
        try {
            image = ImageIO.read(file);
        } catch (Exception ex) {
            System.out.println("No Image");
        }
        return image;
    }

    public static List<String> getImageHEX(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        Map<Integer, Integer> m = new HashMap<>();
        for (int i = 0; i < width ; i++) {
            for (int j = 0; j < height ; j++) {
                int rgb = image.getRGB(i, j);
                int[] rgbArr = getRGBArr(rgb);
                // Filter out grays....
                if (!isGray(rgbArr)) {
                    Integer counter = m.get(rgb);
                    if (counter == null) {
                        counter = 0;
                    }
                    counter++;
                    m.put(rgb, counter);
                }
            }
        }
        return getMostCommonColor(m);
    }

    public static List<String> getMostCommonColor(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> ((Comparable) ((Map.Entry<?, ?>) (o1)).getValue()).compareTo(((Map.Entry<?, ?>) (o2)).getValue()));
        List<String> rgbList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Map.Entry<Integer, Integer> me = list.get(list.size()-i);
            int[] rgb = getRGBArr(me.getKey());
            String rgbString = String.format("%2s", Integer.toHexString(rgb[0])).replace(" ", "0") + String.format("%2s", Integer.toHexString(rgb[1])).replace(" ", "0") + String.format("%2s", Integer.toHexString(rgb[2])).replace(" ", "0");
            rgbList.add(rgbString);
        }
        return rgbList;
    }

    public static int[] getRGBArr(int pixel) {
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{red, green, blue};

    }

    public static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        return true;
    }

    public int[] hexToRGB(String hexStr) {
        if (hexStr != null && !"".equals(hexStr) && hexStr.length() == 6) {
            int[] rgb = new int[3];
            rgb[0] = Integer.valueOf(hexStr.substring(0, 2), 16);
            rgb[1] = Integer.valueOf(hexStr.substring(2, 4), 16);
            rgb[2] = Integer.valueOf(hexStr.substring(4, 6), 16);
            return rgb;
        }
        return null;
    }

    public void setMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("選單");
        JMenuItem item1, item2, item3;
        item1 = new JMenuItem("查看最愛列表");
        item2 = new JMenuItem("登出");
        item3 = new JMenuItem("結束系統");
        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        item1.addActionListener(e -> {
            JFrame frame = new JFrame("最愛列表");
            frame.setLayout(new MigLayout());
            JPanel panel = new JPanel(new MigLayout());
            panel.setPreferredSize(new Dimension(300,300));
            Document document = Login.collection.find(pwdFilter).first();
            List<String> favorites;
            JButton btn1 = new JButton("移除最愛");
            if (document != null) {
                favorites = (List<String>) document.get("isFavorite");
                if (favorites != null) {
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    for (int i = 0; i < favorites.toArray().length; i++) {
                        listModel.addElement(favorites.get(i));
                    }
                    JList<String> list = new JList<>();
                    list.setModel(listModel);
                    list.setFont(new Font("Arial", Font.PLAIN, 15));
                    btn1.addActionListener(e1 -> {
                        Bson delete = Filters.and(Filters.eq("username", Login.nameArea.getText()), Filters.eq("password", Login.pwdArea.getText()), Filters.eq("isFavorite", list.getSelectedValue()));
                        Bson update = Updates.pull("isFavorite", list.getSelectedValue());
                        Login.collection.updateOne(delete, update);
                        listModel.removeElement(list.getSelectedValue());
                    });
                    panel.add(list, "north");
                }
            }
            panel.add(btn1, "center");
            frame.add(panel);
            frame.setVisible(true);
            frame.setSize(150,150);
            frame.setLocationRelativeTo(null);
        });

        item2.addActionListener(e -> {
            new Login();
            setVisible(false);
        });

        item3.addActionListener(e -> System.exit(0));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CatFrame extends JFrame implements ActionListener {
    private final static int WIDTH = 1280;
    private final static int HEIGHT = 660;

    private HouseWidget houseWidget=new HouseWidget();

    private JLabel lbMouseNumber = new JLabel("0");
    private JLabel lbFloorNumber = new JLabel("0");
    private JLabel lbHighScore = new JLabel("0");
    private JLabel lbMiceCarried = new JLabel("0");
    private JLabel lbMiceRemoved = new JLabel("0");

    private JButton upBtn=new JButton("UP");
    private JButton downBtn=new JButton("DOWN");
    private JButton leftBtn=new JButton("LEFT");
    private JButton rightBtn=new JButton("RIGHT");

    private boolean isRunning = false;

    public CatFrame(String title){
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setResizable(false);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - WIDTH)/2, (d.height - HEIGHT)/2);
        init();
        start();
    }

    private void setHouseInfo() {
        houseWidget.setHouseInfo(CatDogMouse.house, CatDogMouse.houseSize);
    }

    private void init() {
        setLayout(new BorderLayout());
        houseWidget.setImage(' ',new ImageIcon("res/floor.jpg").getImage());
        houseWidget.setImage('|',new ImageIcon("res/wall.jpg").getImage());
        houseWidget.setImage('-',new ImageIcon("res/wall.jpg").getImage());
        houseWidget.setImage('.',new ImageIcon("res/wall.jpg").getImage());
        houseWidget.setImage('C', new ImageIcon("res/cat2.jpg").getImage());
        houseWidget.setImage('D', new ImageIcon("res/dog.jpg").getImage());
        houseWidget.setImage('M', new ImageIcon("res/mouse.jpg").getImage());
        houseWidget.setImage('^', new ImageIcon("res/upstair.jpg").getImage());
        houseWidget.setImage('v', new ImageIcon("res/downstair.png").getImage());
        houseWidget.setImage('H', new ImageIcon("res/helicopter.png").getImage());
        add(houseWidget, BorderLayout.CENTER);
        JPanel info= new JPanel(new BorderLayout()); {
            JPanel infoMap = new JPanel(new GridLayout(5,2,2,2)); {

                infoMap.add(new JLabel("Mouse Number"));
                infoMap.add(lbMouseNumber);
                infoMap.add(new JLabel("Floor Number"));
                infoMap.add(lbFloorNumber);
                infoMap.add(new JLabel("HighScore"));
                infoMap.add(lbHighScore);
                infoMap.add(new JLabel("Mice Carried"));
                infoMap.add(lbMiceCarried);
                infoMap.add(new JLabel("Mice Removed"));
                infoMap.add(lbMiceRemoved);

            } info.add(infoMap,BorderLayout.NORTH);

            JPanel infoScore = new JPanel(new GridLayout(6,2,2,2)); {

                infoScore.add(new JLabel("Cat"));
                infoScore.add(new JLabel(Utils.resizeImageIcon(new ImageIcon("res/cat.jpg"), 50, 50)));
                infoScore.add(new JLabel("Guard Dog"));
                infoScore.add(new JLabel(Utils.resizeImageIcon(new ImageIcon("res/dog.jpg"), 50, 50)));
                infoScore.add(new JLabel("Mouse"));
                infoScore.add(new JLabel(Utils.resizeImageIcon(new ImageIcon("res/mouse.jpg"), 50, 50)));
                infoScore.add(new JLabel("Up Stairs"));
                infoScore.add(new JLabel(Utils.resizeImageIcon(new ImageIcon("res/upstair.jpg"), 50, 50)));
                infoScore.add(new JLabel("Down Stairs"));
                infoScore.add(new JLabel(Utils.resizeImageIcon(new ImageIcon("res/downstair.png"), 50, 50)));
                infoScore.add(new JLabel("Helicopter"));
                infoScore.add(new JLabel(Utils.resizeImageIcon(new ImageIcon("res/helicopter.png"), 50, 50)));

            } info.add(infoScore,BorderLayout.CENTER);

            JPanel controller = new JPanel(new BorderLayout()); {
                controller.add(upBtn,BorderLayout.NORTH);
                controller.add(downBtn,BorderLayout.CENTER);
                controller.add(leftBtn,BorderLayout.WEST);
                controller.add(rightBtn,BorderLayout.EAST);


            } info.add(controller,BorderLayout.SOUTH);
        } add(info, BorderLayout.EAST);
    }

    private void start() {
        newGame();
        upBtn.addActionListener(this);
        downBtn.addActionListener(this);
        leftBtn.addActionListener(this);
        rightBtn.addActionListener(this);
    }

    private void newGame() {
        newGame(1);
    }

    private void newGame(int houseNumber) {
        CatDogMouse.initVariables();
        CatDogMouse.loadHighScore();
        CatDogMouse.startHouseNumber(houseNumber);
        buildNewHouse();
        setHouseInfo();
        setInfo();
    }

    private void buildNewHouse(){
        CatDogMouse.buildHouseWalls();
        CatDogMouse.buildHouseObstacles();
        CatDogMouse.addManyDogs();
        CatDogMouse.addManyMice();
        CatDogMouse.addPlayer();
        CatDogMouse.addExitandStairs();

        isRunning = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!isRunning) return;

        if(e.getSource()==upBtn) move('n');
        else if(e.getSource()==downBtn) move('s');
        else if(e.getSource()==leftBtn) move('w');
        else if(e.getSource()==rightBtn) move('e');

        houseWidget.repaint();
    }

    private void setInfo() {
        lbMouseNumber.setText(String.valueOf(CatDogMouse.numMice));
        lbFloorNumber.setText(String.valueOf(CatDogMouse.floorNum));
        lbHighScore.setText(String.valueOf(CatDogMouse.highScore));
        lbMiceCarried.setText(String.valueOf(CatDogMouse.miceCarried));
        lbMiceRemoved.setText(String.valueOf(CatDogMouse.miceRemoved));
    }

    private void move(char dir) {
        if (CatDogMouse.houseNumber>=3) CatDogMouse.moveAllMice();
        CatDogMouse.movePlayer(dir);

        CatDogMouse.collectMouse();
        CatDogMouse.moveAllDogs();
        CatDogMouse.moveAllMice();

        if (CatDogMouse.upStairsLocation(CatDogMouse.player))
        {
            CatDogMouse.climbUpStairs();
            buildNewHouse();
        } else if (CatDogMouse.downStairsLocation(CatDogMouse.player))
        {
            CatDogMouse.climbDownStairs();
            buildNewHouse();
        } else if (CatDogMouse.houseFinished())
        {
            isRunning = false;

            int ans = JOptionPane.showOptionDialog(this,
                    "Congratulations! You won!",
                    "CatDogMouse",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] {
                            "Retry", "Exit"
                    },
                    "Retry");
            if(ans == 0) newGame(CatDogMouse.houseNumber + 1);
            else System.exit(0);
        } else if (CatDogMouse.gameLost())
        {
            isRunning = false;

            int ans = JOptionPane.showOptionDialog(this,
                    "Oops! You lost!",
                    "CatDogMouse",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] {
                            "Retry", "Exit"
                    },
                    "Retry");
            if(ans == 0) newGame(CatDogMouse.houseNumber + 1);
            else System.exit(0);
        }

        setInfo();
    }
}
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HouseWidget extends JPanel {
    private char[][] house;
    private int houseSize;

    private int pixelPerCell = -1;
    private int leftMargin;
    private int topMargin;

    private Map<Character, Image> imageMap = new HashMap<>();

    public HouseWidget() {
        this(null, 0);
    }

    public HouseWidget(char[][] house, int houseSize) {
        this.house = house;
        this.houseSize = houseSize;
    }

    public void setHouseInfo(char[][] house, int houseSize) {
        this.house = house;
        this.houseSize = houseSize;
        invalidate();
    }

    public void setImage(char mapChar, Image image) {
        imageMap.put(mapChar, image);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(pixelPerCell == -1) {
            if(houseSize != 0) {
                int w = getWidth() / houseSize;
                int h = getHeight() / houseSize;
                pixelPerCell = w > h ? h : w;

                leftMargin = (getWidth() - pixelPerCell * houseSize) / 2;
                topMargin = (getHeight() - pixelPerCell * houseSize) / 2;
            }
        }

        super.paintComponent(g);
        Color c = g.getColor();
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        if(house != null) {
            // draw map
            for(int i=0; i<houseSize; i++) {
                for(int j=0; j<houseSize; j++) {
                    int x = leftMargin + pixelPerCell * j;
                    int y = topMargin + pixelPerCell * i;
                    g.drawImage(imageMap.get(house[i][j]), x, y, pixelPerCell, pixelPerCell, null);
                }
            }
        }

        g.setColor(c);
    }
}

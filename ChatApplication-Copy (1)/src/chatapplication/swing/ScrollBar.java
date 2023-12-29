package chatapplication.swing;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollBar;

public class ScrollBar extends JScrollBar {

    public ScrollBar() {
        setPreferredSize(new Dimension(10, 5));
        setBackground(new Color(242, 242, 242));
        setUnitIncrement(20);
    }
}

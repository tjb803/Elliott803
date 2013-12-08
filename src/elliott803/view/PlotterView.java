/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import elliott803.hardware.Plotter;

/**
 * A visual representation of the plotter.
 *
 * The plotter needs to retain enough detail to be able to redraw the entire
 * output if the window needs to be repainted.
 */
public class PlotterView extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    static final String PLOT_CLEAR = "Clear";

    Plotter plotter;
    
    PlotterPaper paper;

    public PlotterView(Plotter plotter) {
        super("Plotter", true, false, true, true);
        this.plotter = plotter;
        
        paper = new PlotterPaper();

        JScrollPane scroll = new JScrollPane(paper);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        actions.setAlignmentX(LEFT_ALIGNMENT);
        JButton clear = new JButton(PLOT_CLEAR);
        clear.addActionListener(this);
        actions.add(clear);
        
        plotter.setView(this);
        
        Container content = getContentPane();
        content.add(scroll, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    /*
     * Button actions
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(PLOT_CLEAR)) {
            plotter.reset();
            paper.plotClear();
        }
    }
    
    /*
     * GUI Visualisation
     */
    
    public void penMove(int x, int y, int dir) {
        paper.plotMove(x, y, dir);
    }
    
    public void penDraw(int x, int y, int dir) {
        paper.plotDraw(x, y, dir);
    }
}

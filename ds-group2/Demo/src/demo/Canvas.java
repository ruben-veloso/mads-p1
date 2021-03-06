/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import demo.objects.Diamond;
import demo.objects.Earth;
import demo.objects.Lift;
import demo.objects.Robot;
import demo.objects.Rock;
import demo.objects.Wall;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rúben Veloso
 */
public class Canvas extends javax.swing.JPanel {

    /**
     * Creates new form Canvas
     */
    public Canvas() {
        initComponents();
        map = new Map();
        load();
    }

    void load() {
        try {
            map.loadMap("fusion_rocks2.map");
            map.printmsp();
            GobbleDiamonds gd = new GobbleDiamonds();
            gd.lazyPlay(map.map, 4,1);
            
            last = new Date();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static enum OperationType {

        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        WAITING,
        ESCAVATING
    }
    private Date last;
    public OperationType operation = OperationType.WAITING;
    private int nDiamonds = 0;

    @Override
    public void paint(Graphics g) {
        Date d2 = new Date();
        long d3 = d2.getTime() - last.getTime();


        if (d3 > 1000) {
            if(operation != OperationType.WAITING){
                for (int i = 0; i < map.map.length; i++) {
                    for (int j = 0; j < map.map[0].length; j++) {
                        map.lastMap[i][j] = map.map[i][j];
                    }
                }
            }
            last = d2;
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] == '#') {
                        Wall.draw(g2, j * 15, i * 15);
                    }
                    if (map.map[i][j] == 'L') {
                        Lift.drawClosed(g2, j * 15, i * 15);
                        if (map.nDiamons == 0) {
                            map.map[i][j] = 'O';
                        }
                    } else if (map.map[i][j] == 'O') {
                        Lift.drawOpen(g2, j * 15, i * 15);
                    } else if (map.map[i][j] == '.') {
                        Earth.draw(g2, j * 15, i * 15);
                    } else if (map.map[i][j] == '*') {
                        Rock.draw(g2, j * 15, i * 15);
                        boolean enter = true;
                        if(map.map[i][j+1] != '#'){
                            for (int k = i; k < map.map.length-1; k++) {
                                if(map.map[k][j+2] != ' ' || map.map[k][j] != ' '){
                                    enter = false;
                                    break;
                                }
                            }
                            if(map.map[i][j+2] == '*' && map.map[i][j+3] != ' ' && enter  ){
                                if(map.map[i + 1][j] == ' '){
                                    map.map[i][j] = ' ';
                                    enter = false;
                                }
                            }else if(!enter) enter = true;
                        }
                        if(enter){
                            char c = map.map[i + 1][j];
                            if (map.map[i + 1][j] == ' ') {
                                map.map[i][j] = ' ';
                                map.map[i + 1][j] = '*';
                            } else if (map.map[i + 1][j] == '*' || map.map[i + 1][j] == 'x') {
                                if (map.map[i + 1][j + 1] == ' ' && map.map[i][j + 1] == ' ') {
                                    map.map[i][j] = ' ';
                                    map.map[i + 1][j + 1] = '*';
                                } else if (map.map[i + 1][j - 1] == ' ' && map.map[i][j - 1] == ' ') {
                                    map.map[i][j] = ' ';
                                    map.map[i + 1][j - 1] = '*';
                                }
                            } else if (map.map[i + 1][j] == 'R') {
                                load();
                            }
                        }
                    } else if (map.map[i][j] == 'x') {
                        Diamond.draw(g2, j * 15, i * 15);
                    } else if (map.map[i][j] == 'R') {
                        switch (operation) {
                            case ESCAVATING:
                                operation = OperationType.WAITING;
                                Robot.drawEscavating(g2, j * 15, i * 15);
                                break;
                            case WAITING:
                                Robot.drawNormal(g2, j * 15, i * 15);
                                break;
                            case MOVE_DOWN:
                                if (map.map[i + 1][j] == ' ') {
                                    map.map[i][j] = ' ';
                                    map.map[i + 1][j] = 'R';
                                    operation = OperationType.WAITING;
                                } else if (map.map[i + 1][j] == 'x' || map.map[i + 1][j] == '.') {
                                    if (map.map[i + 1][j] == 'x') {
                                        map.nDiamons--;
                                    }
                                    map.map[i][j] = ' ';
                                    map.map[i + 1][j] = 'R';
                                    operation = OperationType.ESCAVATING;
                                } else {
                                    operation = OperationType.WAITING;
                                }
                                break;
                            case MOVE_LEFT:
                                if (map.map[i][j - 1] == ' ') {
                                    map.map[i][j] = ' ';
                                    map.map[i][j - 1] = 'R';
                                    operation = OperationType.WAITING;
                                } else if (map.map[i][j - 1] == 'x' || map.map[i][j - 1] == '.') {
                                    if (map.map[i][j - 1] == 'x') {
                                        map.nDiamons--;
                                    }
                                    map.map[i][j] = ' ';
                                    map.map[i][j - 1] = 'R';
                                    operation = OperationType.ESCAVATING;
                                } else if (map.map[i][j - 1] == '*') {
                                    if (map.map[i][j - 2] == ' ') {
                                        map.map[i][j] = ' ';
                                        map.map[i][j - 1] = 'R';
                                        map.map[i][j - 2] = '*';
                                        operation = OperationType.WAITING;
                                    }
                                } else {
                                    operation = OperationType.WAITING;
                                }
                                break;
                            case MOVE_RIGHT:
                                if (map.map[i][j + 1] == ' ') {
                                    map.map[i][j] = ' ';
                                    map.map[i][j + 1] = 'R';
                                    operation = OperationType.WAITING;
                                } else if (map.map[i][j + 1] == 'x' || map.map[i][j + 1] == '.') {
                                    if (map.map[i][j + 1] == 'x') {
                                        map.nDiamons--;
                                    }
                                    map.map[i][j] = ' ';
                                    map.map[i][j + 1] = 'R';
                                    operation = OperationType.ESCAVATING;
                                } else if (map.map[i][j + 1] == '*') {
                                    if (map.map[i][j + 2] == ' ') {
                                        map.map[i][j] = ' ';
                                        map.map[i][j + 1] = 'R';
                                        map.map[i][j + 2] = '*';
                                        operation = OperationType.ESCAVATING;
                                    }
                                } else {
                                    operation = OperationType.WAITING;
                                }
                                break;
                            case MOVE_UP:
                                if (map.map[i - 1][j] == ' ') {
                                    map.map[i][j] = ' ';
                                    map.map[i - 1][j] = 'R';
                                    operation = OperationType.WAITING;
                                } else if (map.map[i - 1][j] == 'x' || map.map[i - 1][j] == '.') {
                                    if (map.map[i - 1][j] == 'x') {
                                        map.nDiamons--;
                                    }
                                    map.map[i][j] = ' ';
                                    map.map[i - 1][j] = 'R';
                                    operation = OperationType.ESCAVATING;
                                } else {
                                    operation = OperationType.WAITING;
                                }
                                break;
                            default:
                                Robot.drawNormal(g2, j * 15, i * 15);
                        }
                    }
                }
            }
        }
        repaint();
    }
    public Map map;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

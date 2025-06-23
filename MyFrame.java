//Erik Enriquez - eenrique@u.rochester.edu

import java.awt.*;
import java.util.Iterator;
import javax.swing.*;
class MyPanel extends JPanel{
    URLinkedList<Point> centers;
    URLinkedList<Point> directionNodes;
    int detail;
    MyPanel(int detail){
        centers = new URLinkedList<>();
        directionNodes = new URLinkedList<>();
        this.detail = detail;
        this.setPreferredSize(new Dimension(500,500));
    }

    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        //setup for the basic route
        g2D.setPaint(Color.DARK_GRAY);
        g2D.setStroke(new BasicStroke(1));

        //takes 2 from itr and maps them to scale
        //*ALERT* can not remove from data type bc it will stop working
        Iterator<Point> centerItr = centers.iterator();
        while (centerItr.hasNext()) {
            Point start = centerItr.next();
            Point end = centerItr.next();
            //it undergoes the following transformation to orient north:
            g2D.drawLine((int) (getWidth() - (start.y * (1.0/detail)) * getWidth()),
                    (int) (getHeight() - (start.x * (1.0/detail)) * getHeight()),
                    (int) (getWidth() - (end.y * (1.0/detail)) * getWidth()),
                    (int) (getHeight() - (end.x * (1.0/detail)) * getHeight()));
        }

        //setup for the direction
        g2D.setPaint(Color.BLUE);
        g2D.setStroke(new BasicStroke(3));

        Iterator<Point> directionItr = directionNodes.iterator();

        //dummy point for line backtracking (skips every other direction)
        Point origin = null;

        //*ALERT* can not remove from data type bc it will stop working
        while (directionItr.hasNext()) {
            Point start;

            //if something in dummy, use as start, else use yourself as start
            if (origin != null) {
                start = origin;
                origin = null;
            } else {
                start = directionItr.next();
            }

            //if it is not the last odd pair (singleton), add another for end
            //else it is a singleton, do nothing 💀
            if (directionItr.hasNext()) {
                Point end = directionItr.next();
                origin = end;

                //also undergoes the following transformation to orient north:
                g2D.drawLine((int) (getWidth() - (start.y * (1.0/detail)) * getWidth()),
                        (int) (getHeight() - (start.x * (1.0/detail)) * getHeight()),
                        (int) (getWidth() - (end.y * (1.0/detail)) * getWidth()),
                        (int) (getHeight() - (end.x * (1.0/detail)) * getHeight()));
            }
        }
    }
    public void addEdge(int x, int y) {
        centers.add(new Point(x, y));
    }
    public void addDirectionEdge(int x, int y) {
        directionNodes.add(new Point(x, y));
    }
}

public class MyFrame extends JFrame{
    MyPanel panel;
    Graph graph;
    URStack<String> directions;
    MyFrame(Graph graph, URStack<String> directions, double maxLat, double maxLog, double minLat, double minLog){
        //higher detail creates more detail (less snapping over another)
        int detail = 10000;

        panel = new MyPanel(detail);
        this.graph = graph;
        this.directions = directions;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);

        //write them all to the panel with scale
        for (Node node : graph.getNodes().values()) {
            for (Edge edge: node.getAdjacentNodes()) {
                //add start point to end point

                panel.addEdge((int) (detail * (Math.abs(Double.parseDouble(node.getLatitude())) - minLat)/(maxLat-minLat)),
                        (int) (detail * (Math.abs(Double.parseDouble(node.getLongitude())) - minLog)/(maxLog-minLog)));

                //add the end point
                panel.addEdge((int) (detail * (Math.abs(Double.parseDouble(edge.getEnd().getLatitude())) - minLat)/(maxLat-minLat)),
                        (int) (detail * (Math.abs(Double.parseDouble(edge.getEnd().getLongitude())) - minLog)/(maxLog-minLog)));
            }
        }

        //run the stack with a method named pass, will paint OVER the existing, color will be red
        while (!directions.isEmpty()) {
            Node node = graph.getNode(directions.pop());
            panel.addDirectionEdge((int) (detail * (Math.abs(Double.parseDouble(node.getLatitude())) - minLat)/(maxLat-minLat)),
                    (int) (detail * (Math.abs(Double.parseDouble(node.getLongitude())) - minLog)/(maxLog-minLog)));
        }

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
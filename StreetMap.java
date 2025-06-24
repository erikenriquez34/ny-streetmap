//Erik Enriquez - eenrique@u.rochester.edu

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

public class StreetMap {
    public static void main(String[] args) {
        Graph graph = new Graph();

        boolean show = false;
        boolean directions = false;
        boolean verbose = false;

        String map = "";
        String start = "";
        String end = "";

        if (args.length > 0) {
            map = args[0];

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--show")) {
                    show = true;
                } else if (args[i].equals("--directions")) {
                    directions = true;
                    start = args[i + 1];
                    end = args[i + 2];
                } else if (args[i].equals("--verbose")) {
                    verbose = true;
                } else if (args[i].equals("--help")) {
                    System.out.println("Usage: java StreetMap <map file> [--show] [--directions <start> <end>] [--verbose]");
                    System.out.println("Options:");
                    System.out.println("--show: Display the map with directions.");
                    System.out.println("--directions <start> <end>: Show directions from start to end.");
                    System.out.println("--verbose: Print additional information during execution.");
                    return;
                }
            }
        } else {
            //make error switch to map
            System.out.println("You need to input a map path.");
            map = "ur.txt";
        }

        System.out.println("start: " + start);
        System.out.println("end: " + end + "\n");

        //obtain the max latitude and max longitude for panel scaling
        double maxLat = 0;
        double maxLog = 0;
        double minLat = Double.MAX_VALUE;
        double minLog = Double.MAX_VALUE;

        try (BufferedReader br = new BufferedReader(new FileReader(map))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == 'i') {
                    graph.addNode(line.split("\t")[1], line.split("\t")[2], line.split("\t")[3]);
                    if (Math.abs(Double.parseDouble(line.split("\t")[2])) > maxLat) {
                        maxLat = Math.abs(Double.parseDouble(line.split("\t")[2]));
                    }
                    if (Math.abs(Double.parseDouble(line.split("\t")[3])) > maxLog) {
                        maxLog = Math.abs(Double.parseDouble(line.split("\t")[3]));
                    }
                    if (Math.abs(Double.parseDouble(line.split("\t")[2])) < minLat) {
                        minLat = Math.abs(Double.parseDouble(line.split("\t")[2]));
                    }
                    if (Math.abs(Double.parseDouble(line.split("\t")[3])) < minLog) {
                        minLog = Math.abs(Double.parseDouble(line.split("\t")[3]));
                    }
                } else {
                    graph.addEdge(line.split("\t")[1], line.split("\t")[2], line.split("\t")[3]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file.");
        }

        //set root node and undergo procedure
        Node startNode = graph.getNode(start);
        Node targetNode = graph.getNode(end);

        //if empty just do a route to self
        if (startNode == null && targetNode == null) {
            startNode = graph.getNode("i1");
            targetNode = graph.getNode("i1");
        }

        URHashTable<String, String> parents = dijkstra(graph, startNode, targetNode);

        if (verbose) {
            printDirections(graph, parents, startNode, targetNode);
        }
        if (show) {
            drawDirections(graph, parents, startNode, targetNode, maxLat, maxLog, minLat, minLog);
        }
    }

    private static URHashTable<String, String> dijkstra(Graph graph, Node startNode, Node targetNode) {
        URHashTable<Node, Double> distance = new URHashTable<>(1000);
        URHashTable<String, String> parents = new URHashTable<>(1000);
        minHeap<DistanceNodeContainer> minHeap = new minHeap<>();
        DecimalFormat oneDec = new DecimalFormat("#.##");

        //adds every intersection to parents and distance as inf
        for (Node node : graph.getNodes().values()) {
            distance.put(node, Double.MAX_VALUE);
            parents.put(node.getName(), node.getName());
        }
        //start exploring from origin
        distance.put(startNode, 0.0);
        minHeap.insert(new DistanceNodeContainer(startNode, 0));

        while (!minHeap.isEmpty()) {
            //select next closest
            Node current = minHeap.deleteMin().getNode();

            //explore all the edges
            for (Edge edge : current.getAdjacentNodes()) {
                Node adjacent;

                //orient by edge
                if (edge.getStart() == current) {
                    adjacent = edge.getEnd();
                } else {
                    adjacent = edge.getStart();
                }

                //traveled distance to be compared (infinity or other)
                double traveled = distance.get(current) + edge.getWeight();

                if (traveled < distance.get(adjacent)) {
                    //if found smaller distance, update the entry and explore further
                    distance.put(adjacent, traveled);

                    //the parent of this newfound adjacent node, is no one but the current
                    parents.put(adjacent.getName(), current.getName());
                    minHeap.insert(new DistanceNodeContainer(adjacent, traveled));
                }
            }
        }
//        printDistances(distance);

        //if a path could not be found
        if (distance.get(targetNode) == Double.MAX_VALUE) {
            throw new RuntimeException("\nNo path found from " + startNode.getName() + " to " + targetNode.getName() +
                    " using this dataset.");
        } else {
            System.out.println("Total distance traveled: " + oneDec.format(distance.get(targetNode)) + " miles");
        }
        return parents;
    }

    private static void drawDirections(Graph graph, URHashTable<String, String> parents, Node startNode, Node targetNode,
                                       double maxLat, double maxLog, double minLat, double minLog) {

        //backtrack through parents to get the path
        URStack<String> directions = new URStack<>();
        while (!targetNode.getName().equals(startNode.getName())) {
            //add the intersection
            directions.push(targetNode.getName());

            //follow the parent
            targetNode = graph.getNode(parents.get(targetNode.getName()));
        }
        new MyFrame(graph, directions, maxLat, maxLog, minLat, minLog);
    }

    private static void printDirections(Graph graph, URHashTable<String, String> parents, Node startNode, Node targetNode) {
        //backtrack through parents to get the path
        URStack<String> directions = new URStack<>();

        while (!targetNode.getName().equals(startNode.getName())) {
            //add the intersection
            directions.push(targetNode.getName());
            //add the road
            directions.push(graph.getEdge(targetNode.getName(), parents.get(targetNode.getName())).getName());
            //follow the parent
            targetNode = graph.getNode(parents.get(targetNode.getName()));
        }
        directions.push(targetNode.getName());

        while (!directions.isEmpty()) {
            System.out.println(directions.pop());
        }
    }

    private static void printDistances(URHashTable<Node, Double> distance) {
        DecimalFormat oneDec = new DecimalFormat("#.##");

        for (Node node : distance.keys()) {
            System.out.println("To Node " + node.getName() + ": " + oneDec.format(distance.get(node)) + " miles");
        }
    }
}

class DistanceNodeContainer implements Comparable<DistanceNodeContainer> {
    private Node node;
    private double distance;

    public DistanceNodeContainer(Node node, double distance) {
        this.node = node;
        this.distance = distance;
    }

    public Node getNode() {
        return this.node;
    }

    //compare them by distances
    @Override
    public int compareTo(DistanceNodeContainer otherContainer) {
        return Double.compare(this.distance, otherContainer.distance);
    }
}

class Graph {
    private URHashTable<String, Node> nodes;

    public Graph() {
        this.nodes = new URHashTable<>(1000);
    }

    public void addNode(String name, String latitude, String longitude) {
        Node newNode = new Node(name, latitude, longitude);
        nodes.put(name, newNode);
    }

    public void addEdge(String name, String startNode, String endNode) {
        Node start = nodes.get(startNode);
        Node end = nodes.get(endNode);
        double weight = haversine(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());

        //finally create the edge
        new Edge(name, start, end, weight);
    }

    public Edge getEdge(String start, String end) {
        URLinkedList<Edge> adjNodes = nodes.get(start).getAdjacentNodes();

        //cycle through the edges 💀 until the pair is found
        //we're looking at start's adjacent so .equals(end) but check both sides 😓
        for (Edge edge : adjNodes) {
            if (edge.getStart().getName().equals(end)) {
                return edge;
            } else if (edge.getEnd().getName().equals(end)) {
                return edge;
            }
        }
        throw new RuntimeException("Corresponding edge node pair not found.");
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    public URHashTable<String, Node> getNodes() {
        return this.nodes;
    }

    private double haversine(String startLat, String startLong, String endLat, String endLong) {
        //take difference to radians
        double latitude = Math.toRadians(Double.parseDouble(endLat) - Double.parseDouble(startLat));
        double longitude = Math.toRadians(Double.parseDouble(endLong) - Double.parseDouble(startLong));

        //proceed with haversine
        double a = Math.pow(Math.sin(latitude / 2), 2) +
                Math.pow(Math.sin(longitude / 2), 2) *
                        Math.cos(Math.toRadians(Double.parseDouble(startLat))) *
                        Math.cos(Math.toRadians(Double.parseDouble(endLat)));
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));

        //kilometers to miles
        return (rad * c) / 1.609;
    }
}

class Node {
    private String name;
    private String latitude;
    private String longitude;
    private URLinkedList<Edge> adjacentNodes;

    public Node(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adjacentNodes = new URLinkedList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public URLinkedList<Edge> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void addAdjacent(Edge edge) {
        adjacentNodes.add(edge);
    }
}

class Edge {
    private Node start;
    private Node end;
    private double weight;
    private String name;

    public Edge(String name, Node start, Node end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.name = name;

        // Add start and end connected to this edge
        start.addAdjacent(this);
        end.addAdjacent(this);
    }

    public Node getStart() {
        return this.start;
    }

    public Node getEnd() {
        return this.end;
    }

    public double getWeight() {
        return this.weight;
    }

    public String getName() {
        return this.name;
    }
}
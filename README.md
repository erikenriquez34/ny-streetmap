## NY-Streetmap

This project implements Dijkstra's algorithm to compute the fastest path between two points on real-world maps, such as the University of Rochester campus, Monroe County, and New York State.
It parses coordinate points and assigns edge weights using the Haversine formula, which calculates distance along a sphere. The route is then displayed with java graphics.

This project was developed for Data Structures and Algorithms at the University of Rochester, focusing on algorithm design and real-world applications of graphs. You will also notice that
I have implemented this project using my own data structures, which were assigned as labs throughout the year.

## How to set up:
Clone the project

    git clone https://github.com/erikenriquez34/ny-streetmap

Navigate to the project and compile the program with

    cd ./ny-streetmap
    javac StreetMap.java

## Usage:

Run the program with

    java StreetMap map.txt [--show] [--directions startIntersection endIntersection] [--verbose]

### Options:
    --show: Display the map
    --directions <start> <end>: Select from start to end (choosing none will a blank map)
    --verbose: Print all the directions taken
    --help: Show this message

**Tip**: For selecting start and end points, randomly choose *intersections* from the `map.txt` you have loaded

## Sample Run:

![sample](https://github.com/user-attachments/assets/d707e4bf-a7a0-41df-8b4c-fa13cd6b865e)

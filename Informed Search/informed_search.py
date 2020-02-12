import sys;

# variables     
cmdArgs = [];
inputFile = None;
source = str();
destination = str();
heuristicFile = None;
heuristicFor = "Kassel"

heuristicArr = [];
distanceArr = [];
fringe = [];
visited = [];
nodesExpanded = 0;
found = False;


class TreeNode:

    def __init__ (self, city, parent, cost, heuristic):
        
        # parent is of type TreeNode
        self.parent = parent;
        self.city = city;
        self.cost = cost;
        self.heuristic = heuristic;


# returns a list of treenodes which are successors of the node in the search tree
def getChildren (node):
    
    res = [];
    
    for pair in distanceArr:
        
        if pair[0] == node.city :
            
            entry = TreeNode (pair[1], node, node.cost + pair[2], getHeuristic (destination, pair[1]))
            res.append(entry);
            
        elif pair[1] == node.city :
            entry = TreeNode (pair[0], node, node.cost + pair[2], getHeuristic (destination, pair[0]))
            res.append(entry);
            
    return res;

# returns true if the node is already visited else returns false
def alreadyVisited (node):
    
    for entry in visited:
        if entry.city == node.city:
            return True;
        
    return False;

# returns the heuristic value for the pair if it exists, else returns zero
def getHeuristic (dest, source):
    
    heuristic = 0;
    
    for entry in heuristicArr:
        if entry[0] == dest and entry [1] == source :
            heuristic = entry[2];
            break;
    
    return heuristic;  
    
# read and validate command line args
cmdArgs = sys.argv;

if len(cmdArgs) < 4:
    print("All Inputs not provided.");
    sys.exit();
    
inputFile = open(cmdArgs[1], "r");
source = str(cmdArgs[2]);
destination = str(cmdArgs[3]);

# check if heuristic file is given
if (len(cmdArgs) == 5) :
    
    heuristicFile = open (cmdArgs[4] , "r");
    
    line = heuristicFile.readline().replace("\n", "").replace("\r", "");
    
    while line != "END OF INPUT":
        entry = line.replace("\n", "").replace("\r", "").split(" ");
        entry[1] = float(entry[1])
        entry.insert(0, heuristicFor)
        heuristicArr.append(entry);
        line = heuristicFile.readline().replace("\n", "").replace("\r", "");

# read input file     
line = inputFile.readline().replace("\n", "").replace("\r", "");

# populate the distanceArr form values in the file
while line != "END OF INPUT" :    
    entry = line.replace("\n", "").replace("\r", "").split(" ");
    entry[2] = float(entry[2]) 
    distanceArr.append(entry);
    line = inputFile.readline().replace("\n", "").replace("\r", "");
        
# create the first node - source
root = TreeNode (source, None, 0, getHeuristic(destination, source))

fringe.append(root);

while len(fringe) > 0:
    
    fringe.sort(key=lambda node: (node.cost + node.heuristic))

    node = fringe.pop(0);
    nodesExpanded += 1
    
    if node.city == destination:
        found = True;
        break;
    
    if not alreadyVisited(node) :
        children = getChildren(node);
        fringe.extend(children);
        
    visited.append(node);

print("Nodes expanded: " + str(nodesExpanded));
       
if found == True :
    
    ptr = node;
    path = [];
    
    while ptr != None : 
        path.insert(0, ptr);
        ptr = ptr.parent;
        
    prev = None;
    
    print("Distance: " + str(node.cost) + " km");
    
    print("Route:")
    for item in path :
        if prev == None:
            prev = item;
        else:
            print(prev.city + " to " + item.city + " , " + str(item.cost - prev.cost) + "km");
            prev = item;
else :
    print("Distance: infinity");
    print("Route: none");

# Informed and uninformed search algorithm

 Search algorithm that can find a route between any two cities using the heuristic file. If heuristic is not provided then program does uninformed search.
 
 ### Sample Input File
 
 ```
 Stuttgart Nuremberg 207
Nuremberg Munich 171
Manchester Birmingham 84
Birmingham Bristol 85
Birmingham London 117
END OF INPUT
```

### Sample heuristic file

```
Luebeck 300
Hamburg 200
Hannover 100
Berlin 200
Bremen 200
Dortmund 100
```

### Command Line format

`find_route input1.txt Munich Berlin` (For doing Uninformed search)   
or   
`find_route input1.txt Munich Berlin h_kassel.txt` (For doing Informed search)

 
 

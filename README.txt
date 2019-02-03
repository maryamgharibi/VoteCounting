

Algorithm:
Traversing the votes is done by Tree implementation.
Each node has it's own weight.

Weight of the Level1 's node is important for traversing.
Note:Level 0 has root with id = 0(Root node has id equal to zero.)



Domain package classes:
Model layer has 3 classes: 1- Ballot(means physical papers) 2-Votes(candidates) 3- Node(For tree implementation)

Running the project:
Firstly,the path of the file should be provided.
then , the candidates are read from that file and showed on page
The several votes can be selected by comma deliminator.(ex:2,3,4)
after typing tally, findWinner method is called.
FindWinner finds the node(s) with high weight. If more than one node is found,just the first one is assigned to the max weight variable.
then there is a loop through each nodes(just on the first level of tree) with min weights, for all of them second-level nodes are considered for processing.
if next nodes exist in first level, all of it's child append to it, otherwise new node is created under the root.

Findwinner method is called until the candidate with more than half of ballots is found.


Running the project:
Step 1 -> First enter the path of the text file
Step 2 -> Read text file and show each item on the page
Step 3 -> Enter the candidates by providing the numbers (ex:1,2,3)
Step 4 -> Type 'tally' for stopping reading inputs

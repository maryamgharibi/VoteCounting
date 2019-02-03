package com.oracle.service;

import com.oracle.domain.Ballot;
import com.oracle.domain.Node;

import java.util.*;
import java.util.stream.Collectors;

public class VoteServiceImpl implements VoteService {

    //Tree has one root with id = 0,root id and weight doesn't count for finding winner process,it is used
    // just for traversing the tree
    @Override
    public Node createRoot(String rootId) {
        Node root = new Node(rootId);
        root.setWeight(0);
        return root;
    }


    //Check availability of first node on level one

    /**
     * @param root
     * @param firstNode
     * @return Optional<Node>
     */
    public Optional<Node> isAvailableFirstNode(Node root, String firstNode) {
        List<Node> rootChildren = root.getChildren();
        return rootChildren.stream().filter(node -> node.getId().equals(firstNode)).findFirst();

    }

    //Part of creating root process

    /**
     * @param parent
     * @param nodes
     * @param ballotId
     */
    public void addChildren(Node parent, String[] nodes, int ballotId) {
        Node newParent = parent;
        for (String node : nodes) {
            if (!parent.getId().equals(node)) {
                newParent = addVote(newParent, node, ballotId);
            }


        }
    }

    // Traverse the tree from root node to find any nodes with minimum weights

    /**
     * @param root
     * @return List<Node>
     */
    public List<Node> findCandidatesWithMinWeights(Node root) {

        Comparator<Node> comparator = Comparator.comparing(Node::getWeight);

        if (root != null) {
            Node minNode = root.getChildren().stream().filter(node -> !node.isRemoved()).min(comparator).get();
            return root.getChildren().stream().filter(node -> node.getWeight() == minNode.getWeight() && !node.isRemoved()).collect(Collectors.toList());
        } else
            return null;

    }

    //Find specific node on level one of tree
    /*   @param nodeId
          @param root
          @return Optional<Node>
      */
    public Optional<Node> findNode(String nodeId, Node root) {

        List<Node> matchedNodes = root.getChildren().stream().filter(child -> !child.isRemoved()).filter(node -> node.getId().equals(nodeId)).collect(Collectors.toList());
        if (matchedNodes.size() > 0) {
            return Optional.ofNullable(matchedNodes.get(0));
        }
        return Optional.empty();
    }

    //Find all the equal nodes of specific nodeID (Search through all nodes of tree)
    /*   @param nodeId
         @param root
         @return Set<Node>
     */
    public Set<Node> findNodes(String nodeId, Node root) {

        List<Node> resultNodes;
        List<Node> queueItems;
        Set<Node> matchedNodes = new HashSet<>();
        resultNodes = searchNode(nodeId, root);
        if (resultNodes.size() > 0) {
            matchedNodes.addAll(resultNodes);
        }
        queueItems = root.getChildren();
        Queue<Node> queue = new LinkedList<>(queueItems);

        while (!queue.isEmpty()) {
            Node currentNode = queue.remove();
            resultNodes = searchNode(nodeId, currentNode);
            if (resultNodes.size() > 0) {
                matchedNodes.addAll(resultNodes);
            }
            queueItems = currentNode.getChildren();
            queue.addAll(queueItems);
        }


        return matchedNodes;

    }

    /* @param nodeId
       @param node
     * @return List<Node>
     * */
    private List<Node> searchNode(String nodeId, Node node) {
        return node.getChildren().stream().filter(n -> n.getId().equals(nodeId) && !n.isRemoved()).collect(Collectors.toList());
    }

    //Traverse the tree from root node and filter the nodes that have the maximum weights
    //If any node has status = removed , it shouldn't be consider in filtering
    /* @param root
     * @return List<Node>
     * */
    public List<Node> findMaxWeight(Node root) {

        Comparator<Node> comparator = Comparator.comparing(Node::getWeight);

        if (root != null) {
            Node maxNode = root.getChildren().stream().filter(node -> !node.isRemoved()).max(comparator).orElse(null);
            if (maxNode == null)
                return null;
            else

                return root.getChildren().stream().filter(node -> node.getWeight() == maxNode.getWeight() && !node.isRemoved()).collect(Collectors.toList());
        } else
            return null;

    }

    //Each ballot has ballotName property that equivalent to the sequence of votes.(ex:1,2,3)
    //This method reads ballotName and create tree
    //If first item in balletName exists , it means that first item has already been defined as child of the root
    //and other item should be added as it's child.Otherwise new child should be created for root node
    /* @param root
     * @param ballot
     * */
    public void createVoteTree(Node root, Ballot ballot) {
        String quoteName = ballot.getName();
        int ballotId = ballot.getId();
        String[] nodes = quoteName.split(",");
        if (nodes.length > 0) {
            String firstNode = nodes[0];
            Node parent;
            Optional<Node> parentAvaibility = isAvailableFirstNode(root, firstNode);
            if (parentAvaibility.isPresent()) {
                //Parent is firstNode and append nodes into firstNode
                parent = parentAvaibility.get();
                root.setWeight(root.getWeight() + 1);

            } else {
                //add all nodes to root
                parent = root;
            }
            parent.setWeight(parent.getWeight() + 1);
            addChildren(parent, nodes, ballotId);


        }
    }


    //New vote equal to tree node, and each node has its own weight.
    //The relation between node and it's ballot is defined by balootId
    /* @param parent
     * @param node
     * @param balootId
     * @return Node
     * */
    public Node addVote(Node parent, String node, int balootId) {
        Node newNode = new Node(node);
        newNode.setWeight(newNode.getWeight() + 1);
        newNode.setBallotId(balootId);
        if (!node.equals(parent.getId())) {

            parent.addChild(newNode);

        }
        return newNode;

    }
}


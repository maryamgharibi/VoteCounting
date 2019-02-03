package com.oracle.service;

import com.oracle.domain.Ballot;
import com.oracle.domain.Node;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VoteService {


    Node createRoot(String rootId);


    Optional<Node> isAvailableFirstNode(Node root, String firstNode);

    void addChildren(Node parent, String[] nodes, int ballotId);

    void createVoteTree(Node root, Ballot ballot);

    List<Node> findMaxWeight(Node root);

    List<Node> findCandidatesWithMinWeights(Node root);

    Optional<Node> findNode(String nodeId, Node root);

    Set<Node> findNodes(String nodeId, Node root);

    Node addVote(Node parent, String node, int balootId);
}

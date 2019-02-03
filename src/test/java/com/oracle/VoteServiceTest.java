package com.oracle;

import com.oracle.domain.Ballot;
import com.oracle.domain.Node;
import com.oracle.service.VoteService;
import com.oracle.service.VoteServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class VoteServiceTest {
    private VoteService voteService;

    @Before
    public void setUp() {
        voteService = new VoteServiceImpl();
    }


    @Test
    public void testCreateVoteTree() {
        Node root = new Node("0");
        Ballot[] ballots = {new Ballot(1, "2,3,1,5,9", false), new Ballot(2, "1,4,3,5,6", false), new
                Ballot(3, "2,4,1,6,4,3", false)};
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));
        junit.framework.Assert.assertEquals(2, root.getChildren().size());
    }

    @Test
    public void testIsAvailableFirstNode() {
        Node root = new Node("0");
        Ballot[] ballots = {new Ballot(1, "2,3", false), new Ballot(2, "1,4", false), new
                Ballot(3, "2,4", false)};
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));
        Optional<Node> availableFirstNode = voteService.isAvailableFirstNode(root, "2");
        Assert.assertTrue(availableFirstNode.isPresent());
    }

    @Test
    public void testAddChildren() {

        String quoteName = "2,3,4";
        int ballotId = 1;
        String[] nodes = quoteName.split(",");
        Node root = new Node("0");
        voteService.addChildren(root, nodes, ballotId);
        int rootChildCount = root.getChildren().size();
        assertEquals(rootChildCount, 1);
    }


    @Test
    public void testFindNode() {
        Node root = new Node("0");
        Ballot[] ballots = {new Ballot(1, "2,3,5,6,7,9", false), new Ballot(2, "1,4,6,8,2", false), new
                Ballot(3, "2,4,9,7,6,5,10", false)};
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));

        Optional<Node> nodeFound = voteService.findNode("1", root);
        assertTrue(nodeFound.isPresent());
    }

    @Test
    public void testFindNodes() {
        Node root = new Node("0");
        Ballot[] ballots = {new Ballot(1, "2,3,5,6,7,9", false), new Ballot(2, "1,4,6,8,2", false), new
                Ballot(3, "2,4,9,7,6,5,10", false)};
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));

        Set<Node> nodeFound = voteService.findNodes("10", root);

        assertTrue(nodeFound.size() > 0);
    }

    @Test
    public void addBalletsToTree() {
        Node root = new Node("0");
        Ballot[] ballots = {new Ballot(1, "2,3", false), new Ballot(2, "1,4", false), new
                Ballot(3, "2,4", false)};
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));
        assertEquals(root.getWeight(), 3);
    }

    @Test
    public void findMaxWeight() {
        Ballot[] ballots = {new Ballot(1, "2,3", false), new Ballot(2, "1,4", false), new
                Ballot(3, "2,4", false)};

        Node root = new Node("0");
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));
        List<Node> maxWieghts = voteService.findMaxWeight(root);

        assertEquals(maxWieghts.get(0).getId(), "2");
    }


    @Test
    public void testFindMinWeight() {
        Ballot[] ballots = {new Ballot(1, "2,3", false), new Ballot(2, "1,4", false), new
                Ballot(3, "2,4", false)};
        Node root = new Node("0");
        Arrays.stream(ballots).forEach(ballot -> voteService.createVoteTree(root, ballot));
        List<Node> minWeights = voteService.findCandidatesWithMinWeights(root);

        assertEquals(minWeights.get(0).getId(), "1");
        Ballot[] ballots_secondTest = {new Ballot(1, "1,2,4", false), new Ballot(2, "2,1,4", false), new
                Ballot(3, "3,1,2,4", false), new Ballot(4, "3,4,1,2", false),
                new Ballot(5, "4,1", false), new Ballot(6, "4,2", false),
                new Ballot(7, "2,1,3", false), new Ballot(8, "3,2,1,4", false)};

        Arrays.stream(ballots_secondTest).forEach(ballot -> voteService.createVoteTree(root, ballot));
        List<Node> minWeights_secondTest = voteService.findCandidatesWithMinWeights(root);
        assertEquals(minWeights_secondTest.get(0).getId(), "1");

    }


}

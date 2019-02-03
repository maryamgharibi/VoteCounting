package com.oracle.ui;

import com.oracle.domain.Ballot;
import com.oracle.domain.Node;
import com.oracle.log.LogUtil;
import com.oracle.service.BallotService;
import com.oracle.service.VoteService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* This class is called from main, all the steps in processing votes is done here
 * Step 1: read data from file
 * Step 2: shows candidates
 * Step 3: gets candidate numbers
 * Step 4: check validations of input votes
 * Step 5: after typing 'tally' traverse votes to find winner
 * Step 6 : tree structure is used in findWinner method, each vote is one tree node,all the related votes selected by one user should be in one branch
 * each branch in tree considers as a ballot
 * */

public class VoteCounter {


    private static final AtomicInteger prefix = new AtomicInteger(1);
    private static final Logger LOGGER = LogUtil.getLogger(VoteCounter.class);
    private Map<Integer, String> votes;
    private VoteService voteService;
    private BallotService ballotService;


    public VoteCounter(BallotService ballotService, VoteService voteService) {
        this.ballotService = ballotService;
        this.voteService = voteService;
    }

    /**
     * This method checks input numbers base on defined rules
     * Defined rules = vote numbers should not be duplicated,numbers should be separated by comma, numbers should not be greater than total numbers
     * of candidates
     *
     * @param candidateNumbers
     * @param totalCandidateNum
     * @return boolean
     */
    public static boolean checkCandidateNumbers(String candidateNumbers, int totalCandidateNum) {

        try {
            Pattern pattern = Pattern.compile("\\b(\\d)\\,\\d*\\,*\\1+\\b");

            Matcher matcher = pattern.matcher(candidateNumbers);
            // check all occurrence
            if (matcher.find()) {
                return false;
            }


            for (String candidateNum : candidateNumbers.split(",")) {
                if (totalCandidateNum < Integer.valueOf(candidateNum) || Integer.valueOf(candidateNum) <= 0) {
                    return false;
                }

            }
            return true;

        } catch (Exception e) {
            return false;
        }


    }

    public Map<Integer, String> getVotes() {
        return votes;
    }

    public void setVotes(Map<Integer, String> votes) {
        this.votes = votes;
    }

    /**
     * Read data from specified file: User enters his prefer candidates with commas(ex:1,2,3)
     *
     * @throws Exception
     */
    public void initiateVotingProcess() throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your file path:");
        List<Ballot> ballots = new ArrayList<>();
        int ballotId = 0;
        try {
            String pathString = br.readLine();
            readCandidatesFromFile(pathString);
            while (true) {
                System.out.println("Please enter your votes:");
                String votes = br.readLine();
                if (votes.equals("tally")) {
                    String rootId = "0";
                    Node rootNode = voteService.createRoot(rootId);
                    ballots.forEach(ballot -> voteService.createVoteTree(rootNode, ballot));
                    Optional<Node> winner = findWinner(rootNode, ballots);
                    if (winner.isPresent()) {
                        String winnerId = winner.get().getId();
                    }
                    break;
                } else if (!checkCandidateNumbers(votes, prefix.get())) {
                    System.out.println("Invalid vote");
                } else {
                    Ballot newBallot = new Ballot(++ballotId, votes, false);
                    ballots.add(newBallot);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in reading ...");
            LOGGER.log(Level.FINEST, e.getMessage());
            throw new Exception();
        }
    }

    /**
     * Read the lines from the file,each line has number prefix
     *
     * @param path
     * @throws IOException
     */

    private void readCandidatesFromFile(String path) throws IOException {
        votes = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.map(String::trim).forEach(line -> votes.put(prefix.getAndIncrement(), line));
            votes.forEach((k, v) -> System.out.println(k + " - " + v));
            setVotes(votes);

        } catch (Exception e) {
            System.out.println("Error while reading this file");
            LOGGER.log(Level.FINEST, e.getMessage());

            throw new IOException();
        }

    }


    /**
     * step 1: count the numbers of half of the non-exhausted ballots
     * step 2: find Nodes which have the maximum weights (Note: if there are more than one candidates in maxWeights list,just consider the first one)
     * step 3: check the condition of winner: winner weight is greater than the numbers of half of all ballots
     * step 4: find the nodes with min weights( it is like BFS traversing of tree,and just consider first level of tree)
     * step 5: check nodes with min weights:
     * step 5-a) update the node status to removed
     * step 5-b) find  next level nodes, and change the level of that node:if next level's node exist on first level, add the node as it's child,
     * otherwise create a new node on first level
     * step 5-c) if all the nodes in one branch updated to removed, consider that ballot as exhausted
     * step 6) repeat from step 3,until the condition becomes true.
     *
     * @param root
     * @param ballots
     * @return
     */
    public Optional<Node> findWinner(Node root, List<Ballot> ballots) {

        int winnerPoint = (ballots.stream().filter(ballot -> !ballot.isExhausted()).collect(Collectors.toList()).size() / 2) + 1;

        List<Node> maxWeights = voteService.findMaxWeight(root);

        if (maxWeights != null && maxWeights.size() > 0) {
            if (getVotes() != null) {
                Integer maxWeightId = Integer.valueOf(maxWeights.get(0).getId());

                LOGGER.log(Level.INFO, MessageFormat.format("First candidate with maximum numbers of votes is {0}-{1}",
                        maxWeightId, getVotes().get(maxWeightId)));
            }

            while (maxWeights.get(0).getWeight() < winnerPoint) {
                List<Node> minWeights = voteService.findCandidatesWithMinWeights(root);
                int minWeight = 0;
                if (minWeights.size() > 0) {
                     minWeight = minWeights.get(0).getWeight();
                }
                for (Node minNode : minWeights) {
                    //During the process of finding the winner, weights of each method can be changed.
                    if (minNode.getWeight() > minWeight)
                    {
                        continue;
                    }
                    if (getVotes() != null) {
                        int minNodeId = Integer.valueOf(minNode.getId());
                        LOGGER.log(Level.INFO, MessageFormat.format("Candidates with the least numbers of votes: {0}-{1}",
                                minNodeId, getVotes().get(minNodeId)));
                    }
                    if (minNode.getId().equals(maxWeights.get(0).getId())) {
                        continue;
                    }

                    parseMinChildren(minNode, root, ballots);

                }
                winnerPoint = (ballots.stream().filter(ballot -> !ballot.isExhausted()).collect(Collectors.toList()).size() / 2) + 1;
                LOGGER.log(Level.INFO, MessageFormat.format("Numbers of quota required to win is {0}", winnerPoint));
                maxWeights = voteService.findMaxWeight(root);
            }

        }
        if (maxWeights == null)
            return Optional.empty();
        else {
            Node winner = maxWeights.get(0);
            if (winner != null) {
                String foundCandidate = winner.getId();
                if (getVotes() != null) {
                    LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    LOGGER.log(Level.INFO, MessageFormat.format("++++++++++++ Winner vote is  {0}-{1} " + " ++++++++++++",
                            foundCandidate, this.votes.get(Integer.parseInt(foundCandidate))));
                    LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                }
            }
            return Optional.ofNullable(winner);
        }

    }


    /**
     * Changing the parent node of a specific node.
     * exp: If second candidate exists as a child of root,add the next candidates as it's child.
     * otherwise , create a new child for root parent
     *
     * @param child
     * @param root
     */
    private void changeBranch(Node child, Node root) {
        String nodeId = child.getId();
        Optional<Node> node = voteService.findNode(nodeId, root);
        if (getVotes() != null) {
            int childId = Integer.valueOf(child.getId());
            LOGGER.log(Level.INFO, MessageFormat.format("Branch of candidate with id {0}-{1} is changed.", childId, getVotes().get(childId)));
        }
        if (node.isPresent()) {
            Node foundNode = node.get();
            foundNode.setWeight(foundNode.getWeight() + 1);
            foundNode.setChildren(child.getChildren());
        } else {
            int newBallotId = root.getWeight() + 1;
            root.setWeight(newBallotId);

            root.addChild(child);
        }
    }


    /**
     * step 1: update status of all the min nodes to removed
     * step 2: change the branch of its children
     * step 3: if all the nodes in one branch updated to 'removed',update ballot status to exhuatsed
     *
     * @param minNode
     * @param root
     * @param ballots
     */
    private void parseMinChildren(Node minNode, Node root, List<Ballot> ballots) {
        Set<Node> matchedNodes = voteService.findNodes(minNode.getId(), root);
        for (Node matchedNode : matchedNodes) {
            matchedNode.setRemoved(true);
        }
        processNotRemovedChild(minNode,root);

        checkExhausted(minNode, ballots);

    }

    /**
     *
     * @param parent
     * @param root
     */
    private void processNotRemovedChild(Node parent,Node root)
    {
      //If direct child is removed, next child should be processed.

        for (Node child : parent.getChildren())
        {
            if (!child.isRemoved()) {
                changeBranch(child, root);
            }
            else
            {
                processNotRemovedChild(child,root);
            }

        }

    }
    /**
     * Check all the nodes defined in one ballot,if all of them are removed, then consider the ballot as exhausted.
     * Traverse in tree is done by BFS
     *
     * @param node
     * @param ballots
     */
    private void checkExhausted(Node node, List<Ballot> ballots) {
        boolean isExhausted = true;
        List<Node> nodeChild = node.getChildren();
        Queue<Node> childQueue = new LinkedList<>();

        if (nodeChild.size() == 0) {

            Ballot exhusted = ballots.stream().filter(ballot -> ballot.getId() == node.getBallotId()).findAny().orElse(null);
            if (exhusted != null) {
                ballotService.updateBallot(exhusted, true);
            }
        }
        for (Node currentChild : nodeChild) {
            childQueue.add(currentChild);

            while (!childQueue.isEmpty()) {
                Node investigateNode = childQueue.remove();
                if (!investigateNode.isRemoved()) {
                    isExhausted = false;
                }
                List<Node> investChild = investigateNode.getChildren();
                if (investChild.size() > 0) {
                    childQueue.addAll(investChild);
                }
            }
            if (isExhausted) {
                Ballot exhusted = ballots.stream().filter(ballot -> ballot.getId() == currentChild.getBallotId()).findAny().orElse(null);
                if (exhusted != null) {
                    ballotService.updateBallot(exhusted, true);
                }

            }
        }


    }

}

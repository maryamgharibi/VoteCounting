package com.oracle;

import com.oracle.domain.Ballot;
import com.oracle.domain.Node;
import com.oracle.log.LogUtil;
import com.oracle.service.BallotServiceImpl;
import com.oracle.service.VoteServiceImpl;
import com.oracle.ui.VoteCounter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class VoteCounterTest {

    private static final Logger LOGGER = LogUtil.getLogger(VoteCounterTest.class);
    private static VoteCounter voteCounter;
    private static VoteServiceImpl voteService;
    private Map<Integer, String> votes = new HashMap<>();

    @BeforeClass
    public static void setUp() {
        BallotServiceImpl ballotService = new BallotServiceImpl();
        voteService = new VoteServiceImpl();
        voteCounter = new VoteCounter(ballotService, voteService);
    }


    //1-initialize different arrays of Ballots(each ballot contains several candidates,and candidates defines in ballot 's name properties)
    //Each ballot has an id , and also by default ballot is not exhaused.
    //2- create instance of Ballots and votes
    @Before
    public void initialize() {

        votes.put(1, "Winery tour");
        votes.put(2, "Ten pin bowling");
        votes.put(3, "Movie night");
        votes.put(4, "Dinner at a restaurant");
        votes.put(5, "Art gallery visit");
        votes.put(6, "Picnic in the park");
        votes.put(7, "Horse riding lessons");
        votes.put(8, "Museum visit");
        votes.put(9, "Surfing lesson");
        voteCounter.setVotes(votes);
        //Arrays of sample ballots

    }


    @Test
    public void invalidCandidateNumber() {
        boolean validCandidateNum;
        String candidateNums = "3,1002,12,1";
        int totalCandidateNum = 10;
        validCandidateNum = VoteCounter.checkCandidateNumbers(candidateNums, totalCandidateNum);
        assertFalse(validCandidateNum);


        candidateNums = "1,1";
        validCandidateNum = VoteCounter.checkCandidateNumbers(candidateNums, totalCandidateNum);

        assertFalse(validCandidateNum);


        candidateNums = "1;1-3";
        validCandidateNum = VoteCounter.checkCandidateNumbers(candidateNums, totalCandidateNum);

        assertFalse(validCandidateNum);
    }

    @Test
    public void validCandidateNumber() {
        boolean validCandidateNum;
        String candidateNums = "1,2,3";
        int totalCandidateNum = 10;
        validCandidateNum = VoteCounter.checkCandidateNumbers(candidateNums, totalCandidateNum);
        assertTrue(validCandidateNum);
    }

    @Test
    public void testFindWinnerChangingBranchThirdCandidate() {

    }

    @Test
    public void testFindWinnerInOneRound() {
        Node root = new Node("0");
        Ballot[] ballots = {new Ballot(1, "2,3", false), new Ballot(2, "1,4", false), new
                Ballot(3, "2,4", false)};
        List<Ballot> ballotList = new ArrayList<>();
        for (int i = 0; i < ballots.length; i++) {
            Ballot ballot = new Ballot(i + 1, ballots[i].getName(), false);
            ballotList.add(ballot);
        }
        //Create tree of votes ,for each ballot's candidate
        ballotList.forEach(ballot -> voteService.createVoteTree(root, ballot));
        Optional<Node> winner = voteCounter.findWinner(root, ballotList);
        String foundCandidate = "";
        if (winner.isPresent()) {
            foundCandidate = winner.get().getId();
        }
        assertEquals("2", foundCandidate);
    }

    @Test
    public void testFindWinnerInTwoRound() {
        LOGGER.info("#######Test (1,2,4,3)(2,1,4)(2,1,3)(1,2,4,3)(3,1,2,4)(3,4,2,1)(3,2,1,4)(4,1)(4,2)  #########");
        String sampleCand = "";
        List<Ballot> definedInExerciseDescBallotList = new ArrayList<>();
        Node exerciseTestRoot = new Node("0");
        Ballot[] exerciseTestBallots = {new Ballot(1, "1,2,4,3", false), new Ballot(2, "2,1,4", false), new
                Ballot(3, "3,1,2,4", false), new Ballot(4, "3,4,1,2", false),
                new Ballot(5, "4,1", false), new Ballot(6, "4,2", false),
                new Ballot(7, "2,1,3", false), new Ballot(8, "3,2,1,4", false)};
        List<Ballot> exerciseTestBallotsList = new ArrayList<>();
        for (int i = 0; i < exerciseTestBallots.length; i++) {
            Ballot exerciseBallot = new Ballot(i + 1, exerciseTestBallots[i].getName(), false);
            definedInExerciseDescBallotList.add(exerciseBallot);
        }
        definedInExerciseDescBallotList.forEach(ballot -> voteService.createVoteTree(exerciseTestRoot, ballot));
        Optional<Node> exerciseTestWinner = voteCounter.findWinner(exerciseTestRoot, definedInExerciseDescBallotList);

        if (exerciseTestWinner.isPresent()) {
            sampleCand = exerciseTestWinner.get().getId();
        }

        assertEquals("2", sampleCand);


        LOGGER.info("#######Test (1,5,4,2)(1,2,4,5)(2,1,4,5)(2,5,4,1)(2,1,5,4)(5,2,4,1)(5,4,1,2)(4,,2,1,5)(4,5,2,1)  #########");
        Ballot[] testBallots = {new Ballot(1, "1,5,4,2", false), new Ballot(2, "1,2,4,5", false), new
                Ballot(3, "2,1,4,5", false), new Ballot(4, "2,5,4,1", false),
                new Ballot(5, "2,1,5,4", false), new Ballot(6, "5,2,4,1", false),
                new Ballot(7, "5,4,1,2", false), new Ballot(8, "4,2,1,5", false),
                new Ballot(9, "4,5,2,1", false)};
        List<Ballot> testBallotList = new ArrayList<>();
        Node testRoot = new Node("0");
        for (int i = 0; i < testBallots.length; i++) {
            Ballot testBallot = new Ballot(i + 1, testBallots[i].getName(), false);
            testBallotList.add(testBallot);
        }
        testBallotList.forEach(ballot -> voteService.createVoteTree(testRoot, ballot));
        Optional<Node> testWinner = voteCounter.findWinner(testRoot, testBallotList);
        String testCand = "";
        if (testWinner.isPresent()) {
            testCand = exerciseTestWinner.get().getId();
        }

        assertEquals("2", testCand);
    }

    @Test
    public void testFindWinnerInThreeRound() {
        LOGGER.info("#######Test (1,2,3)(2,6,3)(7,8,1)  #########");
        Node oneChildroot = new Node("0");
        Ballot[] oneChildBallots = {new Ballot(1, "1,2,3", false), new Ballot(2, "2,6,3", false), new
                Ballot(3, "7,8,1", false)};
        List<Ballot> oneChildBallotList = new ArrayList<>();
        for (int i = 0; i < oneChildBallots.length; i++) {
            Ballot oneChildBallot = new Ballot(i + 1, oneChildBallots[i].getName(), false);
            oneChildBallotList.add(oneChildBallot);
        }
        oneChildBallotList.forEach(ballot -> voteService.createVoteTree(oneChildroot, ballot));
        Optional<Node> oneChildWinner = voteCounter.findWinner(oneChildroot, oneChildBallotList);
        String oneChildFoundCand = "";
        if (oneChildWinner.isPresent()) {
            oneChildFoundCand = oneChildWinner.get().getId();

        }


        assertEquals("1", oneChildFoundCand);


        LOGGER.info("#######Test (1,2,3)(1,4)(2,4)(2,3)  #########");
        String twoMaxWeightCand = "";
        Ballot[] twoMaxWeightBallots = {new Ballot(1, "1,2,3", false), new Ballot(2, "1,4", false), new
                Ballot(3, "2,4", false), new Ballot(4, "2,3", false)};

        List<Ballot> twoMaxWeightBallotList = new ArrayList<>();
        Node twoWeightRoot = new Node("0");
        for (int i = 0; i < twoMaxWeightBallots.length; i++) {
            Ballot twoMaxWeightBallot = new Ballot(i + 1, twoMaxWeightBallots[i].getName(), false);
            twoMaxWeightBallotList.add(twoMaxWeightBallot);
        }
        twoMaxWeightBallotList.forEach(ballot -> voteService.createVoteTree(twoWeightRoot, ballot));
        Optional<Node> twoMaxWeightWinner = voteCounter.findWinner(twoWeightRoot, twoMaxWeightBallotList);

        if (twoMaxWeightWinner.isPresent()) {
            twoMaxWeightCand = twoMaxWeightWinner.get().getId();

        }


        assertEquals("1", twoMaxWeightCand);
    }

   @Ignore
    @Test
    public void testReadCandidatesFromFile() {
        String METHOD_NAME = "readCandidatesFromFile";
        Class[] parameterTypes;
        Object[] parameters;
        Method method;
        parameterTypes = new Class[1];
        parameterTypes[0] = java.lang.String.class;
        try {
            method = voteCounter.getClass().getDeclaredMethod(METHOD_NAME, parameterTypes);
            method.setAccessible(true);
            parameters = new Object[1];

            parameters[0] = "/Users/maryamgharibi/Documents/vote.txt";
            method.invoke(voteCounter, parameters);

            Map<Integer, String> result = voteCounter.getVotes();
            int resultSize = result.size();
            assertEquals(resultSize, 9);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}



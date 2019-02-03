package com.oracle.ui;

import com.oracle.service.BallotServiceImpl;
import com.oracle.service.VoteServiceImpl;


public class VoteApplication {


    public static void main(String[] args) throws Exception {

        // Step1 : inject services to VoteCounter
        BallotServiceImpl ballotService = new BallotServiceImpl();

        VoteServiceImpl voteService = new VoteServiceImpl();
        VoteCounter voteCounter = new VoteCounter(ballotService, voteService);
        //step2:create Ballots and candidates to find the winner

        voteCounter.initiateVotingProcess();


    }


}

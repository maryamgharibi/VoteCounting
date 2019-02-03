package com.oracle.service;

import com.oracle.domain.Ballot;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BallotServiceImpl implements BallotService {
    private static final Logger LOGGER = Logger.getLogger(BallotServiceImpl.class.getName());

    public BallotServiceImpl() {

    }

    /* Just for setting Exhausted field to TRUE
     * @param Ballot ballot
     * @param boolean exhausted
     * @return void
     * */
    public void updateBallot(Ballot ballot, boolean exhausted) {
        if (ballot != null) {
            ballot.setExhausted(exhausted);
            if (exhausted) {
                LOGGER.log(Level.INFO, "Candidate {0} is eliminated.", ballot.getName());

            }
        }

    }

}

package com.oracle.service;

import com.oracle.domain.Ballot;


public interface BallotService {


    void updateBallot(Ballot ballot, boolean exhausted);


}

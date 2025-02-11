package com.group_finity.mascot.tools;

import com.group_finity.mascot.Mascot;
import dev.langchain4j.agent.tool.Tool;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;

public class ShimejiBehaviorTools {
    private final Mascot mascot;

    public ShimejiBehaviorTools(Mascot mascot) {
        this.mascot = mascot;
    }

    @Tool("Makes the Shimeji chase the mouse cursor")
    public void chaseMouse() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("ChaseMouse");
    }

    @Tool("Makes the Shimeji sit and face the mouse cursor")
    public void sitAndFaceMouse() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitAndFaceMouse");
    }

    @Tool("Makes the Shimeji sit and spin its head")
    public void sitAndSpinHead() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitAndSpinHead");
    }

    @Tool("Makes the Shimeji fall")
    public void fall() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("Fall");
    }

    @Tool("Makes the Shimeji look like being dragged")
    public void dragged() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("Dragged");
    }

    @Tool("Makes the Shimeji look like being thrown")
    public void thrown() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("Thrown");
    }

    @Tool("Makes the Shimeji pull up")
    public void pullUp() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("PullUp");
    }

    @Tool("Makes the Shimeji split into two")
    public void splitIntoTwo() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SplitIntoTwo");
    }

    @Tool("Makes the Shimeji stand up")
    public void standUp() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("StandUp");
    }

    @Tool("Makes the Shimeji sit down")
    public void sitDown() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitDown");
    }

    @Tool("Makes the Shimeji sit while dangling legs")
    public void sitWhileDanglingLegs() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitWhileDanglingLegs");
    }

    @Tool("Makes the Shimeji lie down")
    public void lieDown() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("LieDown");
    }

    @Tool("Makes the Shimeji hold onto wall")
    public void holdOntoWall() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("HoldOntoWall");
    }

    @Tool("Makes the Shimeji fall from wall")
    public void fallFromWall() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("FallFromWall");
    }

    @Tool("Makes the Shimeji hold onto ceiling")
    public void holdOntoCeiling() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("HoldOntoCeiling");
    }

    @Tool("Makes the Shimeji walk along the work area floor")
    public void walkAlongWorkAreaFloor() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("WalkAlongWorkAreaFloor");
    }

    @Tool("Makes the Shimeji run along the work area floor")
    public void runAlongWorkAreaFloor() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("RunAlongWorkAreaFloor");
    }

    private void setBehavior(String behaviorName) throws BehaviorInstantiationException, CantBeAliveException {
        try {
            Configuration conf = mascot.getOwnImageSet().getConfiguration();
            mascot.setBehavior(conf.buildBehavior(behaviorName));
        } catch (Exception e) {
            throw new BehaviorInstantiationException("Failed to set behavior: " + behaviorName, e);
        }
    }
} 
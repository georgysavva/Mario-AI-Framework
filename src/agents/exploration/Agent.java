package agents.exploration;

import java.util.ArrayList;
import java.util.Random;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;

public class Agent implements MarioAgent {
    private Random rnd;
    private ArrayList<boolean[]> choicesRight;
    private ArrayList<boolean[]> choicesLeft;
    private static int randomActionRepeatMin = 8;
    private static int randomActionRepeatMax = 12;
    private static float randomActionProb = 0.5f;
    private static int stepsInOneDirection = 600;
    private boolean[] currentRandomAction = null;
    private int currentRandomActionCount = 0;
    private MarioAgent expertAgent;

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        rnd = new Random();
        expertAgent = new agents.robinBaumgarten.Agent();
        expertAgent.initialize(model, timer);

        choicesRight = new ArrayList<>();
        // right run
        for (int i = 0; i < 8; i++) {
            choicesRight.add(new boolean[]{false, true, false, true, false});
        }
        // right jump and run
        for (int i = 0; i < 8; i++) {
            choicesRight.add(new boolean[]{false, true, false, true, true});
        }
        // right
        for (int i = 0; i < 4; i++) {
            choicesRight.add(new boolean[]{false, true, false, false, false});
        }
        // right jump
        for (int i = 0; i < 4; i++) {
            choicesRight.add(new boolean[]{false, true, false, false, true});
        }
        // left run
        for (int i = 0; i < 1; i++) {
            choicesRight.add(new boolean[]{true, false, false, true, false});
        }
        // left jump and run
        for (int i = 0; i < 1; i++) {
            choicesRight.add(new boolean[]{true, false, false, true, true});
        }
        // left
        for (int i = 0; i < 1; i++) {
            choicesRight.add(new boolean[]{true, false, false, false, false});
        }
        // left jump
        for (int i = 0; i < 1; i++) {
            choicesRight.add(new boolean[]{true, false, false, false, true});
        }

        choicesLeft = new ArrayList<>();
        // right run
        for (int i = 0; i < 1; i++) {
            choicesLeft.add(new boolean[]{false, true, false, true, false});
        }
        // right jump and run
        for (int i = 0; i < 1; i++) {
            choicesLeft.add(new boolean[]{false, true, false, true, true});
        }
        // right
        for (int i = 0; i < 1; i++) {
            choicesLeft.add(new boolean[]{false, true, false, false, false});
        }
        // right jump
        for (int i = 0; i < 1; i++) {
            choicesLeft.add(new boolean[]{false, true, false, false, true});
        }
        // left run
        for (int i = 0; i < 8; i++) {
            choicesLeft.add(new boolean[]{true, false, false, true, false});
        }
        // left jump and run
        for (int i = 0; i < 8; i++) {
            choicesLeft.add(new boolean[]{true, false, false, true, true});
        }
        // left
        for (int i = 0; i < 4; i++) {
            choicesLeft.add(new boolean[]{true, false, false, false, false});
        }
        // left jump
        for (int i = 0; i < 4; i++) {
            choicesLeft.add(new boolean[]{true, false, false, false, true});
        }
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer, int currentStep) {
        if (currentStep/stepsInOneDirection % 2 == 0){
            return getActionsRight(model, timer, currentStep);
        } else {
            return getActionsLeft(model, timer, currentStep);
        }
    }
    private boolean[] getActionsRight(MarioForwardModel model, MarioTimer timer, int currentStep) {
        if (this.currentRandomAction != null){
            this.currentRandomActionCount--;    
            boolean [] action = this.currentRandomAction;
            if (this.currentRandomActionCount == 0){
                this.currentRandomAction = null;
            }
            // System.out.println("Repeat random action: " + java.util.Arrays.toString(action));
            return action;
        }
        if (rnd.nextFloat() < randomActionProb){
            this.currentRandomAction = choicesRight.get(rnd.nextInt(choicesRight.size()));
            this.currentRandomActionCount = rnd.nextInt(randomActionRepeatMax - randomActionRepeatMin + 1) + randomActionRepeatMin;
            // System.out.println("New random action: " + java.util.Arrays.toString(currentRandomAction));
            return this.currentRandomAction;
        } 
        boolean[] action= expertAgent.getActions(model, timer, currentStep);
        // System.out.println("Expert action: " + java.util.Arrays.toString(action));
        return action;

    }

    private boolean[] getActionsLeft(MarioForwardModel model, MarioTimer timer, int currentStep) {
        if (this.currentRandomAction != null){
            this.currentRandomActionCount--;    
            boolean [] action = this.currentRandomAction;
            if (this.currentRandomActionCount == 0){
                this.currentRandomAction = null;
            }
            // System.out.println("Repeat random action: " + java.util.Arrays.toString(action));
            return action;
        }
        this.currentRandomAction = choicesLeft.get(rnd.nextInt(choicesLeft.size()));
        this.currentRandomActionCount = rnd.nextInt(randomActionRepeatMax - randomActionRepeatMin + 1) + randomActionRepeatMin;
        // System.out.println("New random action: " + java.util.Arrays.toString(currentRandomAction));
        return this.currentRandomAction;
    } 
    @Override
    public String getAgentName() {
        return "ExplorationAgent";
    }

}

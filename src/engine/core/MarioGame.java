package engine.core;

import java.awt.image.VolatileImage;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyAdapter;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import agents.human.Agent;
import engine.helper.GameStatus;
import engine.helper.MarioActions;

public class MarioGame {
    /**
     * the maximum time that agent takes for each step
     */
    public static final long maxTime = 40;
    /**
     * extra time before reporting that the agent is taking more time that it should
     */
    public static final long graceTime = 10;
    /**
     * Screen width
     */
    public static final int width = 256;
    /**
     * Screen height
     */
    public static final int height = 256;
    /**
     * Screen width in tiles
     */
    public static final int tileWidth = width / 16;
    /**
     * Screen height in tiles
     */
    public static final int tileHeight = height / 16;
    /**
     * print debug details
     */
    public static final boolean verbose = false;
    public static final int maxSteps = 1200;

    /**
     * pauses the whole game at any moment
     */
    public boolean pause = false;

    /**
     * events that kills the player when it happens only care about type and param
     */
    private MarioEvent[] killEvents;

    // visualization
    private JFrame window = null;
    private MarioRender render = null;
    private MarioAgent agent = null;
    private MarioWorld world = null;

    /**
     * Create a mario game to be played
     */
    public MarioGame() {

    }

    /**
     * Create a mario game with a different forward model where the player on
     * certain event
     *
     * @param killEvents events that will kill the player
     */
    public MarioGame(MarioEvent[] killEvents) {
        this.killEvents = killEvents;
    }

    private int getDelay(int fps) {
        if (fps <= 0) {
            return 0;
        }
        return 1000 / fps;
    }

    private void setAgent(MarioAgent agent) {
        this.agent = agent;
        if (agent instanceof KeyAdapter) {
            this.render.addKeyListener((KeyAdapter) this.agent);
        }
    }

    // /**
    //  * Play a certain mario level
    //  *
    //  * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
    //  * @param timer number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
    //  * @return statistics about the current game
    //  */
    // public MarioResult playGame(String level, int timer) {
    //     return this.runGame(new Agent(), level, timer, 0, true, 30, 2);
    // }

    // /**
    //  * Play a certain mario level
    //  *
    //  * @param level      a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
    //  * @param timer      number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
    //  * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
    //  * @return statistics about the current game
    //  */
    // public MarioResult playGame(String level, int timer, int marioState) {
    //     return this.runGame(new Agent(), level, timer, marioState, true, 30, 2);
    // }

    // /**
    //  * Play a certain mario level
    //  *
    //  * @param level      a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
    //  * @param timer      number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
    //  * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
    //  * @param fps        the number of frames per second that the update function is following
    //  * @return statistics about the current game
    //  */
    // public MarioResult playGame(String level, int timer, int marioState, int fps) {
    //     return this.runGame(new Agent(), level, timer, marioState, true, fps, 2);
    // }

    // /**
    //  * Play a certain mario level
    //  *
    //  * @param level      a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
    //  * @param timer      number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
    //  * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
    //  * @param fps        the number of frames per second that the update function is following
    //  * @param scale      the screen scale, that scale value is multiplied by the actual width and height
    //  * @return statistics about the current game
    //  */
    // public MarioResult playGame(String level, int timer, int marioState, int fps, float scale) {
    //     return this.runGame(new Agent(), level, timer, marioState, true, fps, scale);
    // }

    /**
     * Run a certain mario level with a certain agent
     *
     * @param agent the current AI agent used to play the game
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything &lt;=0 will make the time infinite
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer) {
        return this.runGame(agent, level, timer, 0, false, false, "", 0, 2);
    }

    /**
     * Run a certain mario level with a certain agent
     *
     * @param agent      the current AI agent used to play the game
     * @param level      a string that constitutes the mario level, it uses the same
     *                   representation as the VGLC but with more details. for more
     *                   details about each symbol check the json file in the levels
     *                   folder.
     * @param timer      number of ticks for that level to be played. Setting timer
     *                   to anything &lt;=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1
     *                   large mario, and 2 fire mario.
     * @param visuals    show the game visuals if it is true and false otherwise
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals,
            boolean headless, String savePath) {
        return this.runGame(agent, level, timer, marioState, visuals, headless, savePath, visuals & !headless ? 30 : 0, 2);
    }

    /**
     * Run a certain mario level with a certain agent
     *
     * @param agent      the current AI agent used to play the game
     * @param level      a string that constitutes the mario level, it uses the same
     *                   representation as the VGLC but with more details. for more
     *                   details about each symbol check the json file in the levels
     *                   folder.
     * @param timer      number of ticks for that level to be played. Setting timer
     *                   to anything &lt;=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1
     *                   large mario, and 2 fire mario.
     * @param visuals    show the game visuals if it is true and false otherwise
     * @param fps        the number of frames per second that the update function is
     *                   following
     * @param scale      the screen scale, that scale value is multiplied by the
     *                   actual width and height
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals,
            boolean headless, String savePath, int fps, float scale) {
        if (visuals) {
            this.render = new MarioRender(scale);
            this.render.init();
            if (!headless) {
                this.window = new JFrame("Mario AI Framework");
                this.window.setContentPane(this.render);
                this.window.pack();
                this.window.setResizable(false);
                this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.window.setVisible(true);
            }
        }
        this.setAgent(agent);
        return this.gameLoop(level, timer, marioState, visuals, headless, savePath, fps);
    }

    private MarioResult gameLoop(String level, int timer, int marioState, boolean visual, boolean headless,
            String savePath, int fps){
        this.world = new MarioWorld(this.killEvents);
        this.world.visuals = visual;
        this.world.initializeLevel(level, 1000 * timer);
        if (visual) {
            this.world.initializeVisuals(this.render.getGraphicsConfiguration());
        }
        this.world.mario.isLarge = marioState > 0;
        this.world.mario.isFire = marioState > 1;
        this.world.update(new boolean[MarioActions.numberOfActions()]);
        long currentTime = System.currentTimeMillis();

        // initialize graphics
        Image renderTarget = null;
        Graphics backBuffer = null;
        Graphics currentBuffer = null;
        if (visual) {
            if (headless) {
                renderTarget = new BufferedImage(
                        MarioGame.width,
                                MarioGame.height,
                        BufferedImage.TYPE_INT_ARGB);
            } else {
                renderTarget = this.render.createVolatileImage(MarioGame.width, MarioGame.height);
                backBuffer = this.render.getGraphics();
            }
            currentBuffer = renderTarget.getGraphics();
            this.render.addFocusListener(this.render);
        }

        MarioTimer agentTimer = new MarioTimer(MarioGame.maxTime);
        this.agent.initialize(new MarioForwardModel(this.world.clone()), agentTimer);
        int stepNumber = 0;

        ArrayList<MarioEvent> gameEvents = new ArrayList<>();
        ArrayList<MarioAgentEvent> agentEvents = new ArrayList<>();
        File actionsFile = new File(savePath + "/actions.txt");
        List<BufferedImage> frames = new ArrayList<>();
        try (PrintWriter actionsWriter = new PrintWriter(new FileWriter(actionsFile))) {
            while (this.world.gameStatus == GameStatus.RUNNING) {
                // get actions
                agentTimer = new MarioTimer(MarioGame.maxTime);
                boolean[] actions = this.agent.getActions(new MarioForwardModel(this.world.clone()), agentTimer,
                        stepNumber);
                if (MarioGame.verbose) {
                    if (agentTimer.getRemainingTime() < 0
                            && Math.abs(agentTimer.getRemainingTime()) > MarioGame.graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                                + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                    }
                }
                // render world
                if (visual) {
                    if (headless) {
                        currentBuffer.setColor(Color.BLACK);
                        currentBuffer.fillRect(0, 0, MarioGame.width, MarioGame.height);
                    }
                    this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
                    if (headless) {
                        BufferedImage frameCopy = new BufferedImage(
                                renderTarget.getWidth(null),
                                renderTarget.getHeight(null),
                                BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = frameCopy.createGraphics();
                        g2d.drawImage(renderTarget, 0, 0, null);
                        g2d.dispose();

                        // Store in a list
                        frames.add(frameCopy);
                    }
                    StringBuilder sb = new StringBuilder();
                    for (boolean action : actions) {
                        sb.append(action ? "1" : "0").append(" ");
                    }
                    actionsWriter.println(sb.toString().trim());
                }
                // update world
                this.world.update(actions);
                gameEvents.addAll(this.world.lastFrameEvents);
                agentEvents.add(new MarioAgentEvent(actions, this.world.mario.x,
                        this.world.mario.y, (this.world.mario.isLarge ? 1 : 0) + (this.world.mario.isFire ? 1 : 0),
                        this.world.mario.onGround, this.world.currentTick));

                stepNumber++;
                // check if delay needed
                if (this.getDelay(fps) > 0) {
                    try {
                        currentTime += this.getDelay(fps);
                        Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (stepNumber >= MarioGame.maxSteps) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Only do this if we actually have frames
        if (!frames.isEmpty()) {
            int frameWidth = frames.get(0).getWidth();
            int frameHeight = frames.get(0).getHeight();
            int totalWidth = frameWidth * frames.size();
            int totalHeight = frameHeight; // for a single row; or multiply further if you want multiple rows

            BufferedImage spriteSheet = new BufferedImage(
                    totalWidth,
                    totalHeight,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics g = spriteSheet.getGraphics();
            for (int i = 0; i < frames.size(); i++) {
                g.drawImage(frames.get(i), i * frameWidth, 0, null);
            }
            g.dispose();

            // Now save ONE file
            File outputFile = new File(savePath + "/frames.png");
            try {
                ImageIO.write(spriteSheet, "png", outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new MarioResult(this.world, gameEvents, agentEvents, stepNumber);
    }
}

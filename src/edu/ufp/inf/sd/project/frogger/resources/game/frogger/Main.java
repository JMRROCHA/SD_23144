/**
 * Copyright (c) 2009 Vitaliy Pavlenko
 * <p>
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.ufp.inf.sd.project.frogger.resources.game.frogger;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.project.frogger.util.RabbitUtils;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;
import org.json.JSONObject;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;


public class Main extends StaticScreenGame implements Runnable {
    static final int WORLD_WIDTH = (13 * 32);
    static final int WORLD_HEIGHT = (14 * 32);
    static final Vector2D FROGGER_START = new Vector2D(6 * 32, WORLD_HEIGHT - 32);

    static final String RSC_PATH = "edu/ufp/inf/sd/project/frogger/resources/game/resources/";
    static final String SPRITE_SHEET = RSC_PATH + "frogger_sprites.png";

    static final int FROGGER_LIVES = 5;
    static final int STARTING_LEVEL = 1;
    static final int DEFAULT_LEVEL_TIME = 60;
    static final int GAME_INTRO = 0;
    static final int GAME_PLAY = 1;
    static final int GAME_FINISH_LEVEL = 2;
    static final int GAME_INSTRUCTIONS = 3;
    static final int GAME_OVER = 4;
    private final FroggerCollisionDetection frogCol;
    private final FroggerCollisionDetection frogCol2;
    private final Frogger frog;
    private final Frogger frog2;
    private final AudioEfx audiofx;
    //private final AudioEfx audiofx2;
    private final FroggerUI ui;
    private final WindGust wind;
    private final HeatWave hwave;
    private final GoalManager goalmanager;
    private final AbstractBodyLayer<MovingEntity> movingObjectsLayer;
    private final AbstractBodyLayer<MovingEntity> particleLayer;
    private final ImageBackgroundLayer backgroundLayer;
    private final String hostIp;
    private final String rmqPort;
    private static String exchangeName;
    private final String routingkey ;
    private final String userName ;
    public int GameLives = FROGGER_LIVES;
    public int GameScore = 0;
    public int levelTimer = DEFAULT_LEVEL_TIME;
    protected int GameState = GAME_INTRO;
    protected int GameLevel = STARTING_LEVEL;
    private Connection connectionRMQ;
    private static Channel channelRMQ;
    private String queueName;
    private MovingEntityFactory roadLine1;
    private MovingEntityFactory roadLine2;
    private MovingEntityFactory roadLine3;
    private MovingEntityFactory roadLine4;
    private MovingEntityFactory roadLine5;
    private MovingEntityFactory riverLine1;
    private MovingEntityFactory riverLine2;
    private MovingEntityFactory riverLine3;
    private MovingEntityFactory riverLine4;
    private MovingEntityFactory riverLine5;
    private boolean space_has_been_released = false;
    private boolean keyPressed = false;
    private boolean listenInput = true;

    /**
     * Initialize game objects
     */
    public Main(HashMap<String, String> argsRMQ) {

        super(WORLD_WIDTH, WORLD_HEIGHT, false);

        this.hostIp = argsRMQ.get("hostIp");
        this.rmqPort = argsRMQ.get("rmqPort");
        this.exchangeName = argsRMQ.get("exchangeName");
        this.routingkey = argsRMQ.get("routingKey");
        this.userName = argsRMQ.get("userName");

        initRMQ();
        consumeRMQ(routingkey);

        gameframe.setTitle("Frogger " + userName);

        ResourceFactory.getFactory().loadResources(RSC_PATH, "resources.xml");

        ImageResource bkg = ResourceFactory.getFactory().getFrames(SPRITE_SHEET + "#background").get(0);
        backgroundLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH, WORLD_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);

        // Used in CollisionObject, basically 2 different collision spheres
        // 30x30 is a large sphere (sphere that fits inside a 30x30 pixel rectangle)
        //  4x4 is a tiny sphere
        PaintableCanvas.loadDefaultFrames("col", 30, 30, 2, JIGSHAPE.RECTANGLE, null);
        PaintableCanvas.loadDefaultFrames("colSmall", 4, 4, 2, JIGSHAPE.RECTANGLE, null);

        frog = new Frogger(this);
        frogCol = new FroggerCollisionDetection(frog);
        audiofx = new AudioEfx(frogCol, frog);

        frog2 = new Frogger(this);
        frogCol2 = new FroggerCollisionDetection(frog2);
        //audiofx2 = new AudioEfx(frogCol2, frog2);

        ui = new FroggerUI(this);
        wind = new WindGust();
        hwave = new HeatWave();
        goalmanager = new GoalManager();

        movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
        particleLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();

        initializeLevel(1);

        JSONObject value = new JSONObject();
        value.put("roomName", routingkey);
        publishRMQ(routingkey, "PLAYER-READY", userName, value.toString(), "null");

    }


    public void initializeLevel(int level) {

        /* dV is the velocity multiplier for all moving objects at the current game level */
        double dV = level * 0.05 + 1;

        movingObjectsLayer.clear();

        /* River Traffic */
        riverLine1 = new MovingEntityFactory(new Vector2D(-(32 * 3), 2 * 32),
                new Vector2D(0.06 * dV, 0));

        riverLine2 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 3 * 32),
                new Vector2D(-0.04 * dV, 0));

        riverLine3 = new MovingEntityFactory(new Vector2D(-(32 * 3), 4 * 32),
                new Vector2D(0.09 * dV, 0));

        riverLine4 = new MovingEntityFactory(new Vector2D(-(32 * 4), 5 * 32),
                new Vector2D(0.045 * dV, 0));

        riverLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 6 * 32),
                new Vector2D(-0.045 * dV, 0));

        /* Road Traffic */
        roadLine1 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 8 * 32),
                new Vector2D(-0.1 * dV, 0));

        roadLine2 = new MovingEntityFactory(new Vector2D(-(32 * 4), 9 * 32),
                new Vector2D(0.08 * dV, 0));

        roadLine3 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 10 * 32),
                new Vector2D(-0.12 * dV, 0));

        roadLine4 = new MovingEntityFactory(new Vector2D(-(32 * 4), 11 * 32),
                new Vector2D(0.075 * dV, 0));

        roadLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 12 * 32),
                new Vector2D(-0.05 * dV, 0));

        goalmanager.init(level);
        for (Goal g : goalmanager.get()) {
            movingObjectsLayer.add(g);
        }

        /* Build some traffic before game starts buy running MovingEntityFactories for fews cycles */
        for (int i = 0; i < 500; i++)
            cycleTraffic(10);
    }

    /**
     * Populate movingObjectLayer with a cycle of cars/trucks, moving tree logs, etc
     *
     * @param deltaMs
     */
    public void cycleTraffic(long deltaMs) {
        MovingEntity m;
        /* Road traffic updates */
        roadLine1.update(deltaMs);
        if ((m = roadLine1.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine2.update(deltaMs);
        if ((m = roadLine2.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine3.update(deltaMs);
        if ((m = roadLine3.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine4.update(deltaMs);
        if ((m = roadLine4.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine5.update(deltaMs);
        if ((m = roadLine5.buildVehicle()) != null) movingObjectsLayer.add(m);


        /* River traffic updates */
        riverLine1.update(deltaMs);
        if ((m = riverLine1.buildShortLogWithTurtles(40)) != null) movingObjectsLayer.add(m);

        riverLine2.update(deltaMs);
        if ((m = riverLine2.buildLongLogWithCrocodile(30)) != null) movingObjectsLayer.add(m);

        riverLine3.update(deltaMs);
        if ((m = riverLine3.buildShortLogWithTurtles(50)) != null) movingObjectsLayer.add(m);

        riverLine4.update(deltaMs);
        if ((m = riverLine4.buildLongLogWithCrocodile(20)) != null) movingObjectsLayer.add(m);

        riverLine5.update(deltaMs);
        if ((m = riverLine5.buildShortLogWithTurtles(10)) != null) movingObjectsLayer.add(m);

        // Do Wind
        if ((m = wind.genParticles(GameLevel)) != null) particleLayer.add(m);

        // HeatWave
        if ((m = hwave.genParticles(frog.getCenterPosition())) != null) particleLayer.add(m);

        movingObjectsLayer.update(deltaMs);
        particleLayer.update(deltaMs);
    }

    /**
     * Handling Frogger movement from keyboard input
     */
    public void froggerKeyboardHandler() {
        keyboard.poll();

        JSONObject value = new JSONObject();

        boolean keyReleased = false;
        boolean downPressed = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean upPressed = keyboard.isPressed(KeyEvent.VK_UP);
        boolean leftPressed = keyboard.isPressed(KeyEvent.VK_LEFT);
        boolean rightPressed = keyboard.isPressed(KeyEvent.VK_RIGHT);

        // Enable/Disable cheating
        if (keyboard.isPressed(KeyEvent.VK_C))
            frog.cheating = true;
        if (keyboard.isPressed(KeyEvent.VK_V))
            frog.cheating = false;
        if (keyboard.isPressed(KeyEvent.VK_0)) {
            GameLevel = 10;
            initializeLevel(GameLevel);
        }


        /*
         * This logic checks for key strokes.
         * It registers a key press, and ignores all other key strokes
         * until the first key has been released
         */
        if (downPressed || upPressed || leftPressed || rightPressed)
            keyPressed = true;
        else if (keyPressed)
            keyReleased = true;

        if (listenInput) {
            if (downPressed) {
                value.put("posX", frog.getPosition().getX());
                value.put("posY", frog.getPosition().getY()-32);
                publishRMQ(routingkey, "PLAYER-MOVE", userName, value.toString(), "null");
                frog.moveDown();
            }
            if (upPressed) {
                value.put("posX", frog.getPosition().getX());
                value.put("posY", frog.getPosition().getY()+32);
                publishRMQ(routingkey, "PLAYER-MOVE", userName, value.toString(), "null");
                frog.moveUp();
            }
            if (leftPressed) {
                value.put("posX", frog.getPosition().getX()-32);
                value.put("posY", frog.getPosition().getY());
                publishRMQ(routingkey, "PLAYER-MOVE", userName, value.toString(), "null");
                frog.moveLeft();
            }
            if (rightPressed) {
                value.put("posX", frog.getPosition().getX()+32);
                value.put("posY", frog.getPosition().getY());
                publishRMQ(routingkey, "PLAYER-MOVE", userName, value.toString(), "null");
                frog.moveRight();
            }

            if (keyPressed)
                listenInput = false;
        }

        if (keyReleased) {
            listenInput = true;
            keyPressed = false;
        }

        if (keyboard.isPressed(KeyEvent.VK_ESCAPE))
            GameState = GAME_INTRO;
    }

    /**
     * Handle keyboard events while at the game intro menu
     */
    public void menuKeyboardHandler() {
        keyboard.poll();

        // Following 2 if statements allow capture space bar key strokes
        if (!keyboard.isPressed(KeyEvent.VK_SPACE)) {
            space_has_been_released = true;
        }

        if (!space_has_been_released)
            return;

        if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
            switch (GameState) {
                case GAME_INSTRUCTIONS:
                case GAME_OVER:
                    GameState = GAME_INTRO;
                    space_has_been_released = false;
                    break;
                    /*
                default:
                    GameLives = FROGGER_LIVES;
                    GameScore = 0;
                    GameLevel = STARTING_LEVEL;
                    levelTimer = DEFAULT_LEVEL_TIME;
                    frog.setPosition(FROGGER_START);
                    frog2.setPosition(FROGGER_START);
                    GameState = GAME_PLAY;
                    audiofx.playGameMusic();
                    initializeLevel(GameLevel);*/
            }
        }
        if (keyboard.isPressed(KeyEvent.VK_H))
            GameState = GAME_INSTRUCTIONS;
    }

    /**
     * Handle keyboard when finished a level
     */
    public void finishLevelKeyboardHandler() {
        keyboard.poll();
        if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
            GameState = GAME_PLAY;
            audiofx.playGameMusic();
            initializeLevel(++GameLevel);
        }
    }

    /**
     * w00t
     */
    public void update(long deltaMs) {
        switch (GameState) {
            case GAME_PLAY:
                froggerKeyboardHandler();
                wind.update(deltaMs);
                hwave.update(deltaMs);
                frog.update(deltaMs);
                audiofx.update(deltaMs);
                ui.update(deltaMs);

                cycleTraffic(deltaMs);
                frogCol.testCollision(movingObjectsLayer);

                // Wind gusts work only when Frogger is on the river
                if (frogCol.isInRiver())
                    wind.start(GameLevel);
                wind.perform(frog, GameLevel, deltaMs);

                // Do the heat wave only when Frogger is on hot pavement
                if (frogCol.isOnRoad())
                    hwave.start(frog, GameLevel);
                hwave.perform(frog, deltaMs, GameLevel);


                if (!frog.isAlive)
                    particleLayer.clear();

                goalmanager.update(deltaMs);

                if (goalmanager.getUnreached().size() == 0) {
                    GameState = GAME_FINISH_LEVEL;
                    audiofx.playCompleteLevel();
                    particleLayer.clear();
                }

                if (GameLives < 1) {
                    GameState = GAME_OVER;
                }

                break;

            case GAME_OVER:
            case GAME_INSTRUCTIONS:
            case GAME_INTRO:
                goalmanager.update(deltaMs);
                menuKeyboardHandler();
                cycleTraffic(deltaMs);
                break;

            case GAME_FINISH_LEVEL:
                finishLevelKeyboardHandler();
                break;
        }
    }

    /**
     * Rendering game objects
     */
    public void render(RenderingContext rc) {
        switch (GameState) {
            case GAME_FINISH_LEVEL:
            case GAME_PLAY:
                backgroundLayer.render(rc);


                if (frog.isAlive) {
                    movingObjectsLayer.render(rc);
                    //frog.collisionObjects.get(0).render(rc);
                    frog.render(rc);
                } else {
                    frog.render(rc);
                    movingObjectsLayer.render(rc);
                }
                frog2.render(rc);
                particleLayer.render(rc);
                ui.render(rc);
                break;

            case GAME_OVER:
            case GAME_INSTRUCTIONS:
            case GAME_INTRO:
                backgroundLayer.render(rc);
                movingObjectsLayer.render(rc);
                ui.render(rc);
                break;
        }
    }

    /**
     *
     */
    private void initRMQ() {
        try {
            connectionRMQ = RabbitUtils.newConnection2Server(hostIp, Integer.parseInt(rmqPort), "guest", "guest");
            channelRMQ = RabbitUtils.createChannel2Server(connectionRMQ);
            channelRMQ.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
            queueName = channelRMQ.queueDeclare().getQueue();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void consumeRMQ(String routingKey) {
        try {
            channelRMQ.queueBind(queueName, exchangeName, routingKey);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = (new String(delivery.getBody(), StandardCharsets.UTF_8));

                executeMethod(message);

                System.out.println("[x] Consumer Tag[" + consumerTag + "]Received '" + message + "'" + routingKey + exchangeName);

            };
            CancelCallback cancelCallback = (consumerTag) -> System.out.println("[x] Consumer Tag[" + consumerTag + "]CancelCallback");

            channelRMQ.basicConsume(queueName, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void publishRMQ(String routingKey, String operation, String username, String value, String token) {

        JSONObject message = new JSONObject();
        message.put("operation", operation);
        message.put("username", username);
        message.put("value", value);
        message.put("token", token);

        try {
            channelRMQ.basicPublish(exchangeName, routingKey, null, message.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeMethod(String message) {
        JSONObject jsonRequest = new JSONObject(message);
        String userName = jsonRequest.getString("username");
        String operation = jsonRequest.getString("operation");
        JSONObject value = new JSONObject(jsonRequest.getString("value"));
        String token = jsonRequest.getString("token");

        switch (operation) {
            case "START-GAME":
                GameLives = FROGGER_LIVES;
                GameScore = 0;
                GameLevel = STARTING_LEVEL;
                levelTimer = DEFAULT_LEVEL_TIME;
                frog.setPosition(FROGGER_START);
                frog2.setPosition(FROGGER_START);
                GameState = GAME_PLAY;
                audiofx.playGameMusic();
                initializeLevel(GameLevel);
                break;

            case "PLAYER-MOVE":
                if (!userName.equals(this.userName)) {
                    Double posX = value.getDouble("posX");
                    Double posY = value.getDouble("posY");

                    frog2.setPosition(new Vector2D(posX, posY));
                }

        }
    }



}
package graphics.gui;

import graphics.*;
import graphics.drawers.BuildingDrawer;
import graphics.drawers.Drawer;
import graphics.drawers.drawables.ImageDrawable;
import graphics.positioning.IsometricPositioningSystem;
import graphics.positioning.PositioningSystem;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Map;
import utils.RectF;
import utils.SizeF;

public class MapStage extends Stage
{
    private Map map;

    private GameLooper looper;
    protected GraphicHandler gHandler;

    protected GameScene gScene;
    private final Layer lFloor;
    private final Layer lObjects;

    private final double width;
    private final double height;

    public MapStage(Map map, double width, double height)
    {
        this.map = map;

        this.width = width;
        this.height = height;

        PositioningSystem.sScale = 50;

        lFloor = new Layer(0, new RectF(0, 0, width, height), IsometricPositioningSystem.getInstance());
        lObjects = new Layer(1, new RectF(0, 0, width, height), IsometricPositioningSystem.getInstance());
    }


    public Layer getFloorLayer()
    {
        return lFloor;
    }

    public Layer getObjectsLayer()
    {
        return lObjects;
    }

    public void setUpAndShow()
    {
        Group group = new Group();

        GraphicsValues.setScale(0.45);

        Canvas canvas = new Canvas(width, height);
        group.getChildren().add(canvas);

        gHandler = new GraphicHandler(canvas.getGraphicsContext2D(), new RectF(0, 0, canvas.getWidth(), canvas.getHeight()));
        gScene = new GameScene(new SizeF(canvas.getWidth(), canvas.getHeight()));

        gHandler.updateCamera(new RectF(0, -height, canvas.getWidth(), canvas.getHeight()));

        setUpFloor();

        addBuildings();

        gScene.addLayer(lFloor);
        gScene.addLayer(lObjects);
        gHandler.setScene(gScene);

        new GameLooper(gHandler).start();

        setScene(new Scene(group));
        show();
    }

    private void setUpFloor()
    {
        Image tile1 = new Image(getClass().getClassLoader().getResourceAsStream("assets/floor/isometric1.png"));
        Image tile2 = new Image(getClass().getClassLoader().getResourceAsStream("assets/floor/isometric2.png"));
        for (int i = 0; i < map.getWidth(); i++)
            for (int j = 0; j < map.getHeight(); j++)
            {
                ImageDrawable drawable = new ImageDrawable((i + j) % 2 == 0 ? tile1 : tile2, IsometricPositioningSystem.ANG_SIN * 2 * PositioningSystem.sScale);
                drawable.setPivot(.5, .5);
                Drawer drawer = new Drawer(drawable);
                drawer.setPosition(i, j);
                drawer.setLayer(lFloor);
            }
    }

    private void addBuildings()
    {
        map.getAllBuildings().forEach(building ->
        {
            try
            {
                BuildingDrawer drawer = new BuildingDrawer(building);
                drawer.setLayer(lObjects);
            }
            catch (Exception ignored) { ignored.printStackTrace(); }
        });
    }
}
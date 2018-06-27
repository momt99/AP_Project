package graphics.gui;

import graphics.*;
import graphics.drawers.BuildingDrawer;
import graphics.drawers.Drawer;
import graphics.drawers.drawables.ImageDrawable;
import graphics.helpers.BuildingGraphicHelper;
import graphics.layers.Layer;
import graphics.positioning.IsometricPositioningSystem;
import graphics.positioning.PositioningSystem;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Map;
import models.buildings.Building;
import utils.GraphicsUtilities;
import utils.RectF;
import utils.SizeF;

import java.net.URISyntaxException;
import java.util.ArrayList;

public abstract class MapStage extends Stage
{
    private Map map;

    private GameLooper looper;

    protected ArrayList<GraphicHandler> graphicHandlers = new ArrayList<>();

    protected GraphicHandler gHandler;

    protected GameScene gScene;
    private final Layer lFloor;
    private final Layer lObjects;

    protected final double width;
    protected final double height;

    public MapStage(Map map, double width, double height)
    {
        this.map = map;

        this.width = width;
        this.height = height;

        PositioningSystem.sScale = 25;

        lFloor = new Layer(0, new RectF(0, 0, width, height), IsometricPositioningSystem.getInstance());
        lObjects = new Layer(1, new RectF(0, 0, width, height), IsometricPositioningSystem.getInstance());
    }

    public Map getMap()
    {
        return map;
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

        GraphicsValues.setScale(0.8);
        if (System.getProperty("os.name").equals("Linux"))
            GraphicsValues.setScale(1.5);

        Canvas canvas = new Canvas(width * GraphicsValues.getScale(), height * GraphicsValues.getScale());
        group.getChildren().add(canvas);

        gHandler = new GraphicHandler(canvas.getGraphicsContext2D(),
                new RectF(
                        -(canvas.getWidth() - PositioningSystem.sScale * GraphicsValues.getScale() * IsometricPositioningSystem.ANG_COS * 30 * 2) / 2,
                        -(canvas.getHeight() - PositioningSystem.sScale * GraphicsValues.getScale() * IsometricPositioningSystem.ANG_SIN * 30 * 2) / 2 - (PositioningSystem.sScale * GraphicsValues.getScale() * IsometricPositioningSystem.ANG_SIN * 30 * 2) / 2,
                        canvas.getWidth(), canvas.getHeight()));
        gScene = new GameScene(new SizeF(width, height));

        setUpFloor();

        addBuildings();

        gScene.addLayer(lFloor);
        gScene.addLayer(lObjects);

        gHandler.setScene(gScene);
        graphicHandlers.add(gHandler);

        new GameLooper(gHandler).start();
        preShow(group);
        group.setOnMouseClicked(this::handleMouseClick);
        setScene(new Scene(group));
        show();
    }

    private void setUpFloor()
    {
        try
        {
            ImageDrawable tile1 = GraphicsUtilities.createImageDrawable("assets/floor/isometric1.png", IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_COS * 2, IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_SIN * 2, true);
            tile1.setPivot(.5, .5);
            ImageDrawable tile2 = GraphicsUtilities.createImageDrawable("assets/floor/isometric2.png", IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_COS * 2, IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_SIN * 2, true);
            tile2.setPivot(.5, .5);
            ImageDrawable tree = GraphicsUtilities.createImageDrawable("assets/floor/tree.png", IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_COS * 2, IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_SIN * 2, false);
            tree.setPivot(0.5, 1);
            ImageDrawable grass = GraphicsUtilities.createImageDrawable("assets/floor/grass.png", IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_COS * 2, IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_SIN * 2, false);
            grass.setPivot(0.5, 0.5);
            ImageDrawable mushroom = GraphicsUtilities.createImageDrawable("assets/floor/mushroom.png", IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_COS * 2, IsometricPositioningSystem.sScale * IsometricPositioningSystem.ANG_SIN * 2, false);
            mushroom.setPivot(0.5, 0.5);

            for (int i = 0; i < map.getWidth(); i++)
                for (int j = 0; j < map.getHeight(); j++)
                {
                    Drawer drawer = new Drawer((i + j) % 2 == 0 ? tile1 : tile2);
                    drawer.setPosition(i, j);
                    drawer.setLayer(lFloor);
                }

            for (int i = 1; i <= 15; i++)
                for (int j = i - 1; j <= 30 - i; j++)
                {
                    Drawer topDrawer = new Drawer(i < 3 ? mushroom : tree);
                    Drawer leftDrawer = new Drawer(i < 3 ? mushroom : tree);
                    Drawer downTree = new Drawer(i < 3 ? mushroom : tree);
                    Drawer rightTree = new Drawer(i < 3 ? mushroom : tree);
                    topDrawer.setPosition(j, -i);
                    leftDrawer.setPosition(-i, j);
                    rightTree.setPosition(map.getWidth() + i - 1, j);
                    downTree.setPosition(j, i + map.getWidth() - 1);
                    topDrawer.setLayer(lFloor);
                    leftDrawer.setLayer(lFloor);
                    rightTree.setLayer(lFloor);
                    downTree.setLayer(lFloor);
                }
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    private void addBuildings()
    {
        map.getAllBuildings().forEach(this::addBuilding);
    }

    public abstract BuildingGraphicHelper addBuilding(Building building);

    protected void setUpBuildingDrawer(BuildingDrawer drawer)
    { }

    protected void preShow(Group group)
    {
    }

    private void handleMouseClick(MouseEvent event)
    {
        for (int i = graphicHandlers.size() - 1; i >= 0; i--)
            if (graphicHandlers.get(i).handleMouseClickResult(event))
                return;
    }
}

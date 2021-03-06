package graphics.layers;

import graphics.Fonts;
import graphics.GraphicHandler;
import graphics.GraphicsValues;
import graphics.drawers.Drawer;
import graphics.drawers.drawables.RoundRectDrawable;
import graphics.drawers.drawables.TextDrawable;
import graphics.drawers.drawables.animators.AlphaAnimator;
import graphics.drawers.drawables.animators.Animator;
import graphics.positioning.PercentPositioningSystem;
import javafx.scene.paint.Color;
import utils.RectF;

public class ToastLayer extends Layer
{
    private static final double SHOW_HIDE_DURATION = 1;
    private static final double DELAY_DURATION = 1;

    private AlphaAnimator shower;
    private Animator delayer;
    private AlphaAnimator hider;

    private RoundRectDrawable background;

    private TextDrawable text;

    public ToastLayer(int order, RectF bounds, GraphicHandler gHandler)
    {
        super(order, bounds);
        setPosSys(new PercentPositioningSystem(this));

        shower = new AlphaAnimator(SHOW_HIDE_DURATION, 0, 1, this);
        shower.setOnFinish(() ->
        {
            gHandler.removeUpdatable(shower);
            delay(gHandler);
        });

        delayer = new Animator(DELAY_DURATION);
        delayer.setOnFinish(() ->
        {
            gHandler.removeUpdatable(delayer);
            hide(gHandler);
        });

        hider = new AlphaAnimator(SHOW_HIDE_DURATION, 1, 0, this);
        hider.setOnFinish(() ->
        {
            gHandler.removeUpdatable(hider);
            setVisible(false);
        });

        background = new RoundRectDrawable(0, 0, GraphicsValues.PADDING, Color.rgb(0, 0, 0, 0.6));
        background.setPivot(0.5, 0);
        Drawer dBackground = new Drawer(background);
        dBackground.setPosition(0.5, 0);
        dBackground.setLayer(this);

        text = new TextDrawable("", Color.WHITE, Fonts.getMedium());
        text.setPivot(0.5, 0);
        Drawer dText = new Drawer(text);
        dText.setPosition(0.5, GraphicsValues.PADDING / bounds.getHeight());
        dText.setLayer(this);
    }

    public void show(GraphicHandler gHandler)
    {
        hider.stop();
        delayer.stop();
        setVisible(true);
        setAlpha(0);
        gHandler.addUpdatable(shower);
        shower.restart();
    }

    private void delay(GraphicHandler gHandler)
    {
        gHandler.addUpdatable(delayer);
        delayer.restart();
    }

    private void hide(GraphicHandler gHandler)
    {
        gHandler.addUpdatable(hider);
        hider.restart();
    }

    public void showText(String txt, GraphicHandler gHandler)
    {
        this.text.setText(txt);
        this.background.setSize(this.text.getWidth() + GraphicsValues.PADDING * 2, this.text.getHeight() + GraphicsValues.PADDING * 2);
        show(gHandler);
    }
}

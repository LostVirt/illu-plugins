package net.runelite.client.plugins.iutils.bot;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.iutils.api.Interactable;
import net.runelite.client.plugins.iutils.scene.Locatable;
import net.runelite.client.plugins.iutils.scene.ObjectCategory;
import net.runelite.client.plugins.iutils.scene.ObjectType;
import net.runelite.client.plugins.iutils.scene.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class iObjectT implements Locatable, Interactable {

    private final Bot bot;
    private final TileObject tileObject;
    private final ObjectComposition definition;

    public iObjectT(Bot bot, TileObject tileObject, ObjectComposition definition) {
        this.bot = bot;
        this.tileObject = tileObject;
        this.definition = definition;
    }

    //	@Override
    public Bot bot() {
        return bot;
    }

    public Client client() {
        return bot.client;
    }

    @Override
    public Position position() {
        return new Position(tileObject.getWorldLocation());
    }

    public LocalPoint localPoint() {
        return tileObject.getLocalLocation();
    }

    public int id() {
        return tileObject.getId();
    }

    public String name() {
        return definition.getName();
    }

    public List<String> actions() {
        return Arrays.stream(definition().getActions())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ObjectComposition definition() {
//        return client().getObjectDefinition(id());
        return definition;
    }

    private Point menuPoint() {
        if (tileObject instanceof GameObject) {
            System.out.println("Is GO");
            GameObject temp = (GameObject) tileObject;
            return temp.getSceneMinLocation();
        }
        return new Point(localPoint().getSceneX(), localPoint().getSceneY());
    }

    @Override
    public void interact(String action) {
        for (int i = 0; i < actions().size(); i++) {
            if (action.equalsIgnoreCase(actions().get(i))) {
                interact(i);
                if (action.equalsIgnoreCase("Open")) {
                    bot.tick();
                }
                return;
            }
        }
        throw new IllegalArgumentException("no action \"" + action + "\" on object " + id());
    }

    public void interact(int action) {
        bot().clientThread.invoke(() -> {
            int menuAction;

            switch (action) {
                case 0:
                    menuAction = MenuAction.GAME_OBJECT_FIRST_OPTION.getId();
                    break;
                case 1:
                    menuAction = MenuAction.GAME_OBJECT_SECOND_OPTION.getId();
                    break;
                case 2:
                    menuAction = MenuAction.GAME_OBJECT_THIRD_OPTION.getId();
                    break;
                case 3:
                    menuAction = MenuAction.GAME_OBJECT_FOURTH_OPTION.getId();
                    break;
                case 4:
                    menuAction = MenuAction.GAME_OBJECT_FIFTH_OPTION.getId();
                    break;
                default:
                    throw new IllegalArgumentException("action = " + action);
            }

            client().invokeMenuAction("",
                    "",
                    id(),
                    menuAction,
                    menuPoint().getX(),
                    menuPoint().getY()
            );
        });
    }
}
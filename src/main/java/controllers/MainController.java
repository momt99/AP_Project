package controllers;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import exceptions.*;
import models.World;
import utils.ConsoleUtilities;
import utils.ICommandManager;
import views.ConsoleView;
import views.VillageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Matcher;

public class MainController implements ICommandManager
{
    private ConsoleView theView;

    public MainController(ConsoleView theView)
    {
        this.theView = theView;
    }

    public void start()
    {
        String command;
        while (!(command = theView.getCommand()).equalsIgnoreCase("end"))
        {
            try
            {
                manageCommand(command);
            }
            catch (ConsoleRuntimeException ex)
            {
                theView.showError(ex);
            }
            catch (ConsoleException ex)
            {
                theView.showError(ex);
            }
        }
    }

    private ICommandManager childCommandManager = null;

    public void manageCommand(String command) throws ConsoleException
    {
        try
        {
            if (childCommandManager != null)
                childCommandManager.manageCommand(command);
            else
                throw new InvalidCommandException(command);
        }
        catch (InvalidCommandException ex)
        {
            Matcher m;
            if (command.equals("newGame"))
            {
                World.newGame();
                enterGame();
            }
            else if ((m = ConsoleUtilities.getMatchedCommand("load\\s+(\\S+)", command)) != null)
            {
                openGame(Paths.get(m.group(1)));
                enterGame();
            }
            else if ((m = ConsoleUtilities.getMatchedCommand("save\\s+(\\S+)\\s+(\\S+)", command)) != null)
            {
                saveGame(Paths.get(m.group(1), m.group(2)));
            }
            else
                throw new InvalidCommandException(command);
        }
    }

    private void enterGame()
    {
        childCommandManager = new VillageController(new VillageView(theView.getScanner()));
    }

    private void openGame(Path path) throws ConsoleException
    {
        try (BufferedReader reader = Files.newBufferedReader(path))
        {
            World.openGame(reader);
        }
        catch (JsonParseException ex)
        {
            throw new MyJsonException(ex);
        }
        catch (NoSuchFileException ex)
        {
            throw new MyIOException("File not found.", ex);
        }
        catch (IOException ex)
        {
            throw new MyIOException(ex);
        }
        catch (Exception ex)
        {
            throw new ConsoleException("Couldn't open game.", ex.getMessage(), ex);
        }
    }

    private void saveGame(Path path) throws MyJsonException, MyIOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            World.saveGame(writer);
        }
        catch (JsonIOException ex)
        {
            throw new MyJsonException(ex);
        }
        catch (NoSuchFileException ex)
        {
            throw new MyIOException("File not found.", ex);
        }
        catch (IOException ex)
        {
            throw new MyIOException(ex);
        }
    }
}

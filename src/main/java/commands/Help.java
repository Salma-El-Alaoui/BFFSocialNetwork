package commands;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Created by salma on 02/11/2016.
 * Help Command
 */
@Parameters(commandDescription = "See usage of a command")
public final class Help {

    JCommander commander;

    public Help(JCommander commander) {
        this.commander = commander;
    }

    @Parameter(names = {"--command", "-cmd"}, description="command for which you want to see the usage", arity = 1)
    String commandName;

    public void usage() {
        if(commandName != null) {
            commander.usage(commandName);
        } else {
            commander.usage();
        }
    }
}
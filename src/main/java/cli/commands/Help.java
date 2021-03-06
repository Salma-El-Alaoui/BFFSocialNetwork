package cli.commands;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

/**
 * Created by salma on 02/11/2016.
 * Help Command
 */
@Parameters(commandDescription = "see usage of a command")
public final class Help {

    JCommander commander;

    public Help(JCommander commander) {
        this.commander = commander;
    }

    @Parameter(names = {"--command", "-cmd"}, description="command for which you want to see the usage", arity = 1)
    String commandName;

    public void usage() {
        if(commandName != null) {
            try{
                commander.usage(commandName);
            }
            catch(ParameterException pe){
                commander.usage();
            }
        } else {
            commander.usage();
        }
    }
}
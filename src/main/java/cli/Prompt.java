package cli;

import cli.commands.*;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by salma on 31/10/2016.
 * Connects to the database and handles the interactions with the user
 */
public final class Prompt {

    //Constants
    static private final String TABLE_NAME = "BFF_salma";
    //static private final String CONF_FILE = "src/main/resources/hbase-site.xml";
    static private final String CONF_FILE = "/etc/hbase/conf/hbase-site.xml";
    static private final String PUT = "put";
    static private final String GET = "get";
    static private final String CHECK ="check";
    static private final String HELP = "help";
    static private final String EXIT = "exit";
    static private final String SEP_ARGS = " ";

    private Connection connection;
    private Configuration configuration;

    public Prompt() {

        configuration = HBaseConfiguration.create();
        //Adding HBase configuration file
        configuration.addResource(new Path(CONF_FILE));
    }

    /**
     * Connects to Hbase table on the cluster
     * @param tableName table which previously exists and has the column families "friends" and "info"
     * @return the table
     * @throws IOException
     */
    private Table connectToTable(String tableName) throws IOException {
        connection = ConnectionFactory.createConnection(configuration);
        Table table = connection.getTable(TableName.valueOf(tableName));
        System.out.println("Established Connection to the table\n");
        return table;
    }


    private void closeConnection(Table table) throws IOException {
        table.close();
        connection.close();
    }

    private void displayMenu(Table table) {
        JCommander commander = new JCommander();
        commander.addCommand(PUT, new CommandPut(table));
        commander.addCommand(GET, new CommandGet(table));
        commander.addCommand(CHECK, new CommandCheck(table));
        commander.addCommand(HELP, new Help(commander));
        commander.addCommand(EXIT, new Exit());

        System.out.println("Welcome to the BFF Social Network Manager!\n");
        System.out.println("Usage: [command] [command options]");
        System.out.println("Mandatory command options are indicated with a star");
        System.out.println("----------------------Possible Commands--------------------\n");
        commander.usage(PUT);
        commander.usage(GET);
        commander.usage(CHECK);
        commander.usage(HELP);
        commander.usage(EXIT);
        System.out.println("---------------------------------------------------------\n");
    }

    /**
     * Runs the prompt which allows the user to fill the table
     * The user keeps entering commands until he exits (through the command exit)
     * The values of the options of the cli.commands are case insensitive
     */
    public void run() {
        try {

            Table table = connectToTable(TABLE_NAME);
            displayMenu(table);

            Map<String, Command> commands = new HashMap<String, Command>();
            //adds all possible commands
            commands.put(PUT, new CommandPut(table));
            commands.put(GET, new CommandGet(table));
            commands.put(CHECK, new CommandCheck(table));

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {

                //create a new commander for each new line entered by the user
                JCommander commander = new JCommander();
                //adds all the possible commands to the commander
                for (Map.Entry<String, Command> command : commands.entrySet()) {
                    commander.addCommand(command.getKey(), command.getValue());
                }

                //add command for help
                Help help = new Help(commander);
                commander.addCommand(HELP, help);
                commander.addCommand(EXIT, new Exit());

                try {
                    String[] asArgs = scanner.nextLine().split(SEP_ARGS);
                    commander.parse(asArgs);
                } catch (ParameterException pe) {
                    System.err.println(pe.getLocalizedMessage());
                    continue;
                }
                if(null == commander.getParsedCommand()) {
                    commander.usage();
                    continue;
                }
                if(HELP.equals(commander.getParsedCommand())) {
                    help.usage();
                    continue;
                }
                if(EXIT.equals(commander.getParsedCommand())){
                    closeConnection(table);
                    System.out.println("See you later!\n");
                    break;
                }
                Command command = commands.get(commander.getParsedCommand());
                try {
                    //execute the command that was parsed
                    if (command.execute()) {
                        System.out.println("Command succeeded. You may enter your next command.\n");
                    } else {
                        System.out.println("Command failed. You may enter your next command.\n");
                    }

                } catch (IOException e) {
                    //catches exceptions in the execution of the command
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            //catches exceptions in the connection to the table
            e.printStackTrace();
        }
    }
}


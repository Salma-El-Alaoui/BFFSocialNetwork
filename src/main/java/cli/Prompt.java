package cli;

import commands.Command;
import commands.CommandAdd;
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

    private Connection connection;
    private Configuration configuration;

    public Prompt() {

        configuration = HBaseConfiguration.create();
        //Adding HBase configuration file
        configuration.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
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

    /**
     * Runs the prompt which allows the user to fill the table
     * The user keeps entering commands until he exits (through the command exit)
     * The values of the options of the commands are case insensitive
     */
    public void run() {
        Boolean exit = false;

        try {
            Table table = connectToTable("BFF_salma");
            Map<String, Command> commands = new HashMap<String, Command>();
            //adds all possible commands
            commands.put("add", new CommandAdd(table));
            System.out.println("\nWelcome\n");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                //create a new commander for each new line entered by the user
                JCommander commander = new JCommander();
                //adds all the possible commands to the commander
                for (Map.Entry<String, Command> command : commands.entrySet()) {
                    commander.addCommand(command.getKey(), command.getValue());
                }
                try {
                    String[] asArgs = scanner.nextLine().split(" ");
                    commander.parse(asArgs);
                } catch (ParameterException pe) {
                    System.err.println(pe.getLocalizedMessage());
                    commander.usage();
                    continue;
                }
                if (null == commander.getParsedCommand()) {
                    commander.usage();
                    continue;
                }
                Command command = commands.get(commander.getParsedCommand());
                try {
                    //execute the command that was parsed
                    if (command.execute()) {
                        System.out.println("commands.Command succeeded. You may enter your next command\n");
                    } else {
                        System.out.println("commands.Command failed. You may enter your next command\n");
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


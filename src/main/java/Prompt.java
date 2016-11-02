/**
 * Created by salma on 31/10/2016.
 */

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

public final class Prompt {

    private Connection connection;
    private Configuration configuration;

    public Prompt() {

        configuration = HBaseConfiguration.create();
        //Adding HBase configuration file
        configuration.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));

    }

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

    public void run() {
        Boolean exit = false;

        try {
            Table table = connectToTable("BFF_salma");
            Map<String, Command> commands = new HashMap<String, Command>();
            commands.put("add", new CommandAdd(table));
            System.out.println("\nWelcome\n");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                JCommander commander = new JCommander();
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
                    if (command.execute()) {
                        System.out.println("Command succeeded. You may enter your next command\n");
                    } else {
                        System.out.println("Command failed. You may enter your next command\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



package commands;

import org.apache.hadoop.hbase.client.Table;
import java.io.IOException;

/**
 * Created by salma on 02/11/2016.
 * This abstract class models the commands which are provided by the command line tool
 */
public abstract class Command {

    // Hbase table where the operations are made
    protected static Table table;


    /**
     * Constructor
     */
    public Command(Table newTable) {
        table = newTable;
    }


    /**
     * Executes the operation provided by the command and is implemented in each subclass
     */
    public abstract boolean execute() throws IOException;
}

package slimesoccer;

import java.util.Arrays;

/**
 * Last modified: 14.06.2011
 * @author Timo Hinterleitner
 * @author Martin Kamleithner
 */
public class Main {

    /**
     * Startet einen neuen SlimeSoccer Client
     * Wird die Option -s beim start angegeben, wird anstantt des Clients ein Server gestartet
     * Beim Server kann als weitere Option ein Port folgen
     * @param args -s für Server, ansonsten wird ein Client gestartet
     */
    public static void main(String[] args) {
        if (args.length >= 1 && "-s".equals(args[0])) {
            server.Server.main(Arrays.copyOfRange(args, 1, args.length));
        } else {
            client.Client.main(Arrays.copyOfRange(args, 1, args.length));
        }
    }
}

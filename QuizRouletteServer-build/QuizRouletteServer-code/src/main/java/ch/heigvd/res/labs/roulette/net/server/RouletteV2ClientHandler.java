package ch.heigvd.res.labs.roulette.net.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    PrintWriter out = new PrintWriter(new OutputStreamWriter(os));

    out.println("Hello. Online HELP is available. Will you find it?");
    out.flush();

    String command;
    boolean done = false;
    int numberOfCommands = 0;

    while (!done && ((command = in.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:// OK
          ++numberOfCommands;
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          out.println(JsonObjectMapper.toJson(rcResponse));
          out.flush();
          break;

        case RouletteV2Protocol.CMD_HELP: // OK
          ++numberOfCommands;
          out.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;

        case RouletteV2Protocol.CMD_INFO: // OK
          ++numberOfCommands;
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION,
              store.getNumberOfStudents());
          out.println(JsonObjectMapper.toJson(response));
          out.flush();
          break;

        case RouletteV2Protocol.CMD_LOAD: // OK
          ++numberOfCommands;
          out.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          out.flush();

          int initialNumber = store.getNumberOfStudents();
          store.importData(in);
          int afterNumber = store.getNumberOfStudents();
          int number = afterNumber - initialNumber;

          LoadCommandResponse lcr = new LoadCommandResponse();
          lcr.setStatus(RouletteV2Protocol.STATUS_SUCCESS);
          lcr.setNumberOfNewStudents(number);
          String json1 = JsonObjectMapper.toJson(lcr);

          out.println(json1);
          out.flush();
          break;

        case RouletteV2Protocol.CMD_BYE: // OK
          ++numberOfCommands;
          done = true;

          ByeCommandResponse bcr = new ByeCommandResponse();
          bcr.setStatus(RouletteV2Protocol.STATUS_SUCCESS);
          bcr.setNumberOfCommands(numberOfCommands);
          String json2 = JsonObjectMapper.toJson(bcr);

          out.println(json2);
          out.flush();
          break;

        case RouletteV2Protocol.CMD_CLEAR: // OK
          ++numberOfCommands;
          store.clear();
          out.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          out.flush();
          break;

        case RouletteV2Protocol.CMD_LIST:// OK
          ++numberOfCommands;

          StudentsList studentsList = new StudentsList();
          studentsList.setStudents(store.listStudents());
          String json3 = JsonObjectMapper.toJson(studentsList);
          
          out.println(json3);
          out.flush();
          break;

        default: // OK
          out.println("Huh? please use HELP if you don't know what commands are available.");
          out.flush();
          break;
      }
      out.flush();
    }

  }

}

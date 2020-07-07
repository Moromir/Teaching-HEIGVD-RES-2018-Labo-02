package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  protected Socket socket;
  protected BufferedReader in;
  protected PrintWriter out;

  /**
   * Used to println and flush (short syntax is better).
   * @param s the String
   */
  protected void outPrintlnAndFlush(String s) {
    out.println(s);
    out.flush();
  };

  @Override
  public void connect(String server, int port) throws IOException {
    // OK A COMPLETER

    socket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

    // Read the first line with Hello message.
    in.readLine();

    LOG.info("Connect to server : " + server + " with port : " + port + ".");
  }

  @Override
  public void disconnect() throws IOException {
    // OK A COMPLETER

    // Send the BYE signal in protocol
    outPrintlnAndFlush(RouletteV1Protocol.CMD_BYE);

    // CLose the IOs and the socket
    out.close();
    in.close();
    socket.close();

    LOG.info("Disconnect form server.");
  }

  @Override
  public boolean isConnected() {
    // OK A COMPLETER
    return socket != null && socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    // OK A COMPLETER

    // Send LOAD
    outPrintlnAndFlush(RouletteV1Protocol.CMD_LOAD);
    // Read the answer "Send your data"
    in.readLine();

    // Send student and ENDOFDATA.
    outPrintlnAndFlush(fullname);
    outPrintlnAndFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    // Read the answer "Data loaded"
    in.readLine();
    LOG.info("Loaded student.");
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    // OK A COMPLETER
    outPrintlnAndFlush(RouletteV1Protocol.CMD_LOAD);
    in.readLine();

    for (Student s : students) {
      outPrintlnAndFlush(s.getFullname());
    }

    outPrintlnAndFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    in.readLine();
    LOG.info("Loaded students.");
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    // OK A COMPLETER
    outPrintlnAndFlush(RouletteV1Protocol.CMD_RANDOM);

    String json = in.readLine();

    if (JsonObjectMapper.parseJson(json, RandomCommandResponse.class).getError() == null) {
      Student s = Student.fromJson(json);
      LOG.info("Random Student : " + s + ".");
      return s;
    } else {
      LOG.warning("No student in store !");
      throw new EmptyStoreException();
    }
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    // OK A COMPLETER
    outPrintlnAndFlush(RouletteV1Protocol.CMD_INFO);

    String json = in.readLine();

    int n = JsonObjectMapper.parseJson(json, InfoCommandResponse.class).getNumberOfStudents();
    LOG.info("Number of students : " + n + ".");

    return n;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    // OK A COMPLETER
    outPrintlnAndFlush(RouletteV1Protocol.CMD_INFO);

    String json = in.readLine();

    return JsonObjectMapper.parseJson(json, InfoCommandResponse.class).getProtocolVersion();
  }

}

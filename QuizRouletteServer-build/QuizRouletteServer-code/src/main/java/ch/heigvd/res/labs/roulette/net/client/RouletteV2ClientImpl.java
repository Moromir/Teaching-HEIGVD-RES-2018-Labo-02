package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  private boolean statusSuccess = false;
  private int numberOfNewStudents = 0;
  private int numberOfCommands = 0;

  @Override
  public void clearDataStore() throws IOException {
    // OK A COMPLETER
    outPrintlnAndFlush(RouletteV2Protocol.CMD_CLEAR);

    String response = in.readLine();

    if (!response.equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
      throw new RuntimeException("Protocol Error : Response is not" + RouletteV2Protocol.RESPONSE_CLEAR_DONE);
    }

    LOG.info("Cleared Data store.");
  }

  @Override
  public List<Student> listStudents() throws IOException {
    // OK A COMPLETER 
    outPrintlnAndFlush(RouletteV2Protocol.CMD_LIST);

    String json = in.readLine();

    return JsonObjectMapper.parseJson(json, StudentsList.class).getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    outPrintlnAndFlush(RouletteV2Protocol.CMD_LOAD);
    in.readLine();
    outPrintlnAndFlush(fullname);
    outPrintlnAndFlush(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    String json = in.readLine();

    LoadCommandResponse lcr = JsonObjectMapper.parseJson(json, LoadCommandResponse.class);
    statusSuccess = lcr.getStatus().equals(RouletteV2Protocol.STATUS_SUCCESS);
    numberOfNewStudents = lcr.getNumberOfNewStudents();

    LOG.info("Loaded student.");
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    outPrintlnAndFlush(RouletteV2Protocol.CMD_LOAD);
    in.readLine();

    for (Student s : students) {
      outPrintlnAndFlush(s.getFullname());
    }

    outPrintlnAndFlush(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    String json = in.readLine();
    LoadCommandResponse lcr = JsonObjectMapper.parseJson(json, LoadCommandResponse.class);
    statusSuccess = lcr.getStatus().equals(RouletteV2Protocol.STATUS_SUCCESS);
    numberOfNewStudents = lcr.getNumberOfNewStudents();

    LOG.info("Loaded students.");
  }

  @Override
  public void disconnect() throws IOException {
    outPrintlnAndFlush(RouletteV2Protocol.CMD_BYE);

    String json = in.readLine();
    ByeCommandResponse bcr = JsonObjectMapper.parseJson(json, ByeCommandResponse.class);
    String status = bcr.getStatus();
    statusSuccess = status.equals(RouletteV2Protocol.STATUS_SUCCESS);
    numberOfCommands = bcr.getNumberOfCommands();

    out.close();
    in.close();
    socket.close();

    LOG.info("Disconnect form server.");
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    return super.pickRandomStudent();
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    return super.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return super.getProtocolVersion();
  }

  public boolean isStatusSuccess() {
    return statusSuccess;
  }

  public int getNumberOfNewStudents() {
    return numberOfNewStudents;
  }

  public int getNumberOfCommands() {
    return numberOfCommands;
  }

}

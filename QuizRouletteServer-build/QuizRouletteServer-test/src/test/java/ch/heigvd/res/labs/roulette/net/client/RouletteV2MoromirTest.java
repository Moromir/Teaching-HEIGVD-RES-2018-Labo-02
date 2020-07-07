package ch.heigvd.res.labs.roulette.net.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Moromir
 */
public class RouletteV2MoromirTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  /**
   * Utility method to get the casted client. (Syntactic sugar)
   * 
   * @return the client
   */
  private IRouletteV2Client client() {
    return (IRouletteV2Client) roulettePair.getClient();
  }

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "Moromir")
  public void theClearInstructionShouldWork() throws IOException {
    client().loadStudent("Frodo Baggins");
    client().clearDataStore();
    assertEquals(0, client().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "Moromir")
  public void theListInstructionShouldWork() throws IOException {
    client().loadStudent("Denethor");
    client().loadStudent("Boromir");
    client().loadStudent("Faramir");
    client().loadStudent("Moromir");

    assertEquals("Denethor", client().listStudents().get(0).getFullname());
    assertEquals("Boromir", client().listStudents().get(1).getFullname());
    assertEquals("Faramir", client().listStudents().get(2).getFullname());
    assertEquals("Moromir", client().listStudents().get(3).getFullname());
  }

  @Test
  @TestAuthor(githubId = "Moromir")
  public void theLoadInstructionShouldReturnTheNumberOfNewStudents() throws IOException {
    client().loadStudent("Isildur");
    assertEquals(1, client().getNumberOfNewStudents());

    Student[] students = { new Student("Jonas"), new Student("Minh"), new Student("Julien"), new Student("Nico") };
    client().loadStudents(Arrays.asList(students));
    assertEquals(4, client().getNumberOfNewStudents());
  }

  @Test
  @TestAuthor(githubId = "Moromir")
  public void theLoadInstructionShouldHaveASuccessStatusInResponse() throws IOException {
    client().loadStudent("Gollum");
    assertTrue(client().isStatusSuccess());
  }

  @Test
  @TestAuthor(githubId = "Moromir")
  public void theByeInstructionShouldReturnTheNumberOfInstructionsUsed() throws IOException, EmptyStoreException {
    client().loadStudent("Gandalf"); // 1
    client().pickRandomStudent();// 2
    client().pickRandomStudent();// 3
    client().disconnect();// 4
    assertEquals(client().getNumberOfCommands(), 4);
  }

}

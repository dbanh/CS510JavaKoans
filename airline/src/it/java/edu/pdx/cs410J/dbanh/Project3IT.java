package edu.pdx.cs410J.dbanh;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * An integration test for the {@link Project3} main class.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Project3IT extends InvokeMainTestCase {
	private static File airlineFile;

	  @BeforeClass
	  public static void createTempDirectoryForAirlineFile() throws IOException {
	    File tmpDirectory = new File(System.getProperty("java.io.tmpdir"));
	    airlineFile = new File(tmpDirectory, "airline.txt");
	  }

	  @AfterClass
	  public static void deleteTempDirectoryForAirlineFile() {
	    if (airlineFile.exists()) {
	      assertTrue(airlineFile.delete());
	    }
	  }
	  

	  private MainMethodResult invokeProject3(String... args) {
	    return invokeMain(Project3.class, args);
	  }

	  private String readFile(File file) throws FileNotFoundException {
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    StringBuilder sb = new StringBuilder();
	    Stream<String> lines = br.lines();
	    lines.forEach(line -> {
	      sb.append(line).append("\n");
	    });

	    return sb.toString();
	  }
	  
	  @Test
	  public void testFileExists() {
		  assertTrue(airlineFile.exists());
	  }

	  @Test
	  public void test1CreateNewAirlineFileWhenFileDoesNotExist() throws FileNotFoundException {
	    assertThat(airlineFile.exists(), equalTo(false));

	    MainMethodResult result =
	      invokeProject3("-textFile", airlineFile.getAbsolutePath(), "MyAirline",
	        "123", "PDX", "7/16/2017", "3:00", "PM", "LAX", "7/16/2017", "6:00", "PM");
	    assertThat(result.getExitCode(), equalTo(0));

	    String fileContents = readFile(airlineFile);
	    assertThat(fileContents, containsString("123"));
	  }

	  @Test
	  public void test2AddFlightToExistingAirlineFile() throws FileNotFoundException {
	    assertThat(airlineFile.exists(), equalTo(true));

	    MainMethodResult result =
	      invokeProject3("-textFile", airlineFile.getAbsolutePath(), "MyAirline",
	        "234", "PDX", "7/17/2017", "3:00", "PM", "LAX", "7/17/2017", "6:00", "PM");
	    assertThat(result.getExitCode(), equalTo(0));
	    
	    MainMethodResult result2 =
	  	      invokeProject3("-textFile", airlineFile.getAbsolutePath(), "MyAirline",
	  	        "567", "pdx", "7/17/2017", "3:00", "PM", "lax", "7/17/2017", "6:00", "PM");
	  	    assertThat(result.getExitCode(), equalTo(0));

	    String fileContents = readFile(airlineFile);
	    assertThat(fileContents, containsString("123"));
	    assertThat(fileContents, containsString("234"));
	    assertThat(fileContents, containsString("567"));
	    
	    
	    
	  }
	  
	  @Test
	  public void testAddFlightToExistingAirlineFileThatDoesNotMatchAirline() throws FileNotFoundException {
	    assertThat(airlineFile.exists(), equalTo(true));

	    MainMethodResult result =
	      invokeProject3("-textFile", airlineFile.getAbsolutePath(), "NotMyAirline",
	        "234", "PDX", "7/17/2017", "3:00", "PM", "LAX", "7/17/2017", "6:00", "PM");
	    assertThat(result.getExitCode(), equalTo(0));

	    assertThat(result.getTextWrittenToStandardError(), containsString("Airline entered does not match airline listed in"));
	  }
	  
	  /**
	   * Tests that invoking the main method with no arguments issues an error
	   */
	  @Test
	  public void testNoCommandLineArguments() {
	    MainMethodResult result = invokeProject3();
	    assertThat(result.getExitCode(), equalTo(0));
	    assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
	  }
	  
	  /**
	   * Tests that invoking the main method with not enough arguments issues an error
	   */
	  @Test
	  public void testNotEnoughArguments() {
	    MainMethodResult result = invokeProject3("United", "42", "ABE");
	    assertThat(result.getExitCode(), equalTo(0));
	    assertThat(result.getTextWrittenToStandardError(), containsString("Command line arguments not valid"));
	  }
	  
	  /**
	   * Tests that invoking the main method with too many arguments issues an error
	   */
	  @Test
	  public void testTooManyArguments() {
	    MainMethodResult result = invokeProject3("United", "42", "ABE", "01/01/2001", "1:11", "AM", "PDX", "03/03/2003", "3:33", "PM", "ABED");
	    assertThat(result.getExitCode(), equalTo(0));
	    assertThat(result.getTextWrittenToStandardError(), containsString("Command line arguments not valid."));
	  }

	  /**
	   * Tests that invoking the main method with -README prints out readme
	   */
	  @Test
	  public void testReadme() {
	    MainMethodResult result = invokeProject3("-README", "United", "42", "ABE", "01/01/2001", "1:11", "AM", "PDX", "03/03/2003", "3:33", "PM");
	    assertThat(result.getExitCode(), equalTo(0));
	    assertThat(result.getTextWrittenToStandardOut(), containsString("README"));
	    
	    MainMethodResult result2 = invokeProject3("-print", "-README", "-textFile", "test.txt", "United", "42", "ABE", "01/01/2001", "1:11", "AM", "PDX", "03/03/2003", "3:33", "AM");
	    assertThat(result2.getExitCode(), equalTo(0));
	    assertThat(result2.getTextWrittenToStandardOut(), containsString("README"));
	  }
	  
	  /**
	   * Tests that invoking the main method with -print prints out flight info
	   */
	  @Test
	  public void testPrint() {
	    MainMethodResult result = invokeProject3("-print", "United", "42", "ABE", "01/01/2001", "1:11", "AM", "PDX", "03/03/2003", "3:33", "AM");
	    assertThat(result.getExitCode(), equalTo(0));
	    assertThat(result.getTextWrittenToStandardOut(), containsString("Flight 42 departs ABE"));
	    
	    MainMethodResult result2 = invokeProject3("-textFile", "text.txt", "-print", "United", "42", "ABE", "01/01/2001", "1:11", "AM", "PDX", "03/03/2003", "3:33", "AM");
	    assertThat(result2.getExitCode(), equalTo(0));
	    assertThat(result2.getTextWrittenToStandardOut(), containsString("Flight 42 departs ABE"));
	  }
	  
	  /**
	   * Tests airport codes
	   */
	  @Test
	  public void testAirportCodeNotValid() {
		  MainMethodResult result = invokeProject3("-print", "United", "42", "AB1", "01/01/2001", "1:11", "AM", "PDX", "03/03/2003", "3:33", "AM");
		    assertThat(result.getExitCode(), equalTo(0));
		    assertThat(result.getTextWrittenToStandardError(), containsString("airport code is not valid."));
		    
		  MainMethodResult result2 = invokeProject3("-print", "United", "42", "ABE", "01/01/2001", "1:11", "AM", "PDXW", "03/03/2003", "3:33", "AM");
		    assertThat(result2.getExitCode(), equalTo(0));
		    assertThat(result2.getTextWrittenToStandardError(), containsString("airport code is not valid."));
  }
}
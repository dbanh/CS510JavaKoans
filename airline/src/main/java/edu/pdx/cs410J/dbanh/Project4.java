package edu.pdx.cs410J.dbanh;

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirportNames;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";
//    final static org.slf4j.Logger logger = LoggerFactory.getLogger(Project4.class);

    public static void main(String... args) {
        
        List<String> errorList = new ArrayList<String>();
		Airline airline = new Airline();
		Flight flight = new Flight();
		AbstractAirline parsedAirline = null;
		Boolean readMeOption = containsReadMe(args);
		Boolean correctNumberArgs = correctNumberOfArgs(args);
		Boolean printOption = containsPrint(args);
		Boolean hostOption = containsHost(args);
		Boolean searchOption = containsSearch(args);
		Boolean correctNumberOfArgsForSearch = correctNumberOfArgsForSearch(args);
		
		if(args.length == 0) {
			System.err.println("ERROR: Missing command line arguments");
	        System.exit(1);
		}
		
		if(readMeOption) {
			printReadMe();
		}
		
		else if(searchOption) {
			if(hostOption && correctNumberOfArgsForSearch) {
				String hostName = null;
				String portString = null;
				
		    	for(int i = 0; i < args.length; ++i) {
		    		if(args[i].equals("-host")) {
	    				hostName = args[i+1];
	    				break;
		    		}
		    	}
		    	
		    	for(int i = 0; i < args.length; ++i) {
		    		if(args[i].equals("-port")) {
		    			portString = args[i+1];
		    			break;
		    		}
		    	}
				
				if (hostName == null) {
					usage(MISSING_ARGS);

				} else if (portString == null) {
					usage("Missing port");
				}
				
				int port;
				try {
					port = Integer.parseInt(portString);

				} catch (NumberFormatException ex) {
					usage("Port \"" + portString + "\" must be an integer");
					return;
				}
				
				AirlineRestClient client = new AirlineRestClient(hostName, port);
				String message = null;
				String airlineName = null;
				String src = null;
				String dest = null;
				boolean validParameters = true;
				
				try {
					int startingPosition = args.length - 3; //accounts for any options included in the argument list
					
					for (int i = startingPosition; i < args.length; ++i) {
						
						if(i == startingPosition) {
					    	 airlineName = args[i];
						}

						else if( i == startingPosition+1) {
							if(validateAirportCode(args[i])) {
								src = args[i];
							}
							else {
								validParameters = false;
							}
						}

						else if(i == startingPosition+2) {
							if(validateAirportCode(args[i])) {
								dest = args[i];
							}
							else {
								validParameters = false;
							}
						}
					} 
					
					if (validParameters) {
						String result = client.searchForFlights(airlineName, src, dest);
						if(result != null) {
							message = Messages.searchFlight(result);
						} else {
							message = Messages.noFlightsFound(src, dest);
						}
					} else {
						System.err.println("ERROR: Must search for valid airport codes");
				        System.exit(1);
					}

				} catch (IOException ex) {
					System.err.println("ERROR: Contact with server unsuccessful. Check credentials and try again.");
					return;
				}
				System.out.println(message);
			} else {
				if(!correctNumberOfArgsForSearch) {
					System.err.println("ERROR: Missing or extraneous command line arguments");
			        System.exit(1);
				}
				if(!hostOption) {
					System.err.println("ERROR: Host and port needed to execute search");
			        System.exit(1);
				}
			}
		}
		
		else if(hostOption) {
			if (correctNumberArgs) {
				String hostName = null;
				String portString = null;
				
		    	for(int i = 0; i < args.length; ++i) {
		    		if(args[i].equals("-host")) {
		    			hostName = args[i+1];
		    			break;
		    		}
		    	}
		    	
		    	for(int i = 0; i < args.length; ++i) {
		    		if(args[i].equals("-port")) {
		    			portString = args[i+1];
		    			break;
		    		}
		    	}
				
				if (hostName == null) {
					usage(MISSING_ARGS);

				} else if (portString == null) {
					usage("Missing port");
				}
				
				int port;
				try {
					port = Integer.parseInt(portString);

				} catch (NumberFormatException ex) {
					usage("Port \"" + portString + "\" must be an integer");
					return;
				}
				
				AirlineRestClient client = new AirlineRestClient(hostName, port);
				String message = null;
				String airlineName = null;
				String flightNumber = null;
				String src = null;
				String departTime = null;
				String dest = null;
				String arriveTime = null;
				boolean validParameters = true;
				
				try {
					int startingPosition = args.length - 10; //accounts for any options included in the argument list
					
					for (int i = startingPosition; i < args.length; ++i) {
						
						if(i == startingPosition) {
					    	 airlineName = args[i];
						}
						else if(i == startingPosition+1) {
					    	flightNumber = args[i];
						}
						else if( i == startingPosition+2) {
							if(validateAirportCode(args[i])) {
								src = args[i];
							}
							else {
								validParameters = false;
							}
						}
						else if(i == startingPosition+3) {
							boolean validDate = validateDate(args[i]);
							boolean validTime = validateTime(args[i+1]);
							boolean validAmPm = validateAmPm(args[i+2]);
							
							if(validDate == false) {
								validParameters = false;
							}
							if(validTime == false || validAmPm == false) {
								validParameters = false;
							}
							
							else {
								departTime = new StringBuilder(args[i]).append(" ").append(args[i+1]).append(" ").append(args[i+2]).toString();
							}
						}
						else if(i == startingPosition+6) {
							if(validateAirportCode(args[i])) {
								dest = args[i];
							}
							else {
								validParameters = false;
							}
						}
						else if(i == startingPosition+7) {
							boolean validDate = validateDate(args[i]);
							boolean validTime = validateTime(args[i+1]);
							boolean validAmPm = validateAmPm(args[i+2]);
							
							if(validDate == false) {
								validParameters = false;
							}
							if(validTime == false || validAmPm == false) {
								validParameters = false;
							}
							
							else {
								arriveTime = new StringBuilder(args[i]).append(" ").append(args[i+1]).append(" ").append(args[i+2]).toString();
							}
						}
					} 
					
					if (validParameters) {
						String result = client.addAirlineAndFlight(airlineName, flightNumber, src, departTime, dest, arriveTime);
						if(result == null) {
							
						    if(printOption) {
							    errorList = createAirlineAndFlight(args, airline, flight);
							    
							    if(!errorList.isEmpty()) {
							    	for(String error : errorList) {
							    		System.err.println(error);
							    	}
								 } else {
						    		Iterator<AbstractFlight> iterator = airline.getFlights().iterator();
						    		while(iterator.hasNext()) {
						    			System.out.println(iterator.next());
						    		}
					    		}
					    	}	
						}
					} else {
						System.err.println("ERROR: Parameters invalid. Please check and try again.");
				        System.exit(1);
					}

				} catch (IOException ex) {
					error("While contacting server: " + ex);
					return;
				}
			}	else {
				if(!correctNumberArgs) {
					System.err.println("ERROR: Missing or extraneous command line arguments");
			        System.exit(1);
				}
			}
		}
		
		else if(correctNumberArgs) {
		    errorList = createAirlineAndFlight(args, airline, flight);
		    
		    if(!errorList.isEmpty()) {
			    	for(String error : errorList) {
			    		System.err.println(error);
			    	}
			 }

		    else if(printOption) {
	    		Iterator<AbstractFlight> iterator = airline.getFlights().iterator();
	    		while(iterator.hasNext()) {
	    			System.out.println(iterator.next());
	    		}
	    	}	
		}
		
		else {
			System.err.println("ERROR: Command line arguments not valid. Please check arguments and try again.");
		}
		
		
		
		System.exit(0);
    }

	private static boolean correctNumberOfArgsForSearch(String[] args) {
		int argsCount = 0;
		 int optionsCount = 0;

		 for(int i = 0; i < args.length; ++i) {
			 if(args[i].charAt(0) != '-') {
				 ++argsCount;
			 }
			 else {
				 //verify options are -print, -textFile, or -README
				 if(args[i].equals("-print") || args[i].equals("-README")) {
					 ++optionsCount;
				 }
				 else if(args[i].equals("-host")){ 
					++optionsCount;
			    	++i;
				 }
				 else if(args[i].equals("-port")) {
					 ++optionsCount;
					 ++i;
				 }
				 else if(args[i].equals("-search")) {
					 ++optionsCount;
				 }
				 //if argument is not -print, -README, -textFile, or a filename directly proceeding -textFile, 
				 //assume they are a part of the airline/flight arguments
				 else {
					 ++argsCount;
				 }
			 }
		 }
		 
		 if(argsCount == 3) {
			 return true;
		 }
		 else {
			 return false;
		 }
	}

	private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     * @param message An error message to print
     */
    private static void usage( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port name flightNumber src deartTime dest ArriveTime");
        err.println("  host			Host of web server");
        err.println("  port    		Port of web server");
        err.println("  name    		Airline name");
        err.println("  flightNumber Flight number");
        err.println("  src			Departure airport");
        err.println("  departTime   Time of departure");
        err.println("  dest   		Destination airport");
        err.println("  arriveTime   Time of arrival");
        err.println();
        err.println("This simple program posts airline flights to the server");
        err.println("User can search for flights with airline name, departure airport, and destination airport");
        err.println();

        System.exit(1);
    }
    

    /**
     * This method checks for -search in the options
     * @param args
     * @return true if args contains -search
     */
	private static Boolean containsSearch(String[] args) {
    	for(int i = 0; i < args.length; ++i) {
			  if(args[i].equals("-search")) {
				  return true;
			  }
    	}
    	return false;
	}

	
	/**
	 * This method checks for -host in the options
	 * @param args
	 * @return true if args contains -host
	 */
    private static Boolean containsHost(String[] args) {
    	for(int i = 0; i < args.length; ++i) {
			  if(args[i].equals("-host")) {
				  return true;
			  }
    	}
    	return false;
	}

	  
	  /**
	   * Method checks to see if -README is invoked as one of the options. Options will only be accepted before the airline/flight arguments. The method 
	   * will immediately return false once an argument without a '-' is encountered (this assumes that airline/flight arguments will not begin with a '-').
	   * @param arguments from command line
	   * @return true if one of the arguments classified as "options" is -README, otherwise return false
	   */
	  
	  private static boolean containsReadMe(String [] args) {
		  for(int i = 0; i < args.length; ++i) {
			  if(args[i].equals("-README")) {
				  return true;
			  }
//			  if(args[i].charAt(0) != '-') {
//				  return false;
//			  }
		  }
		  
		  return false;
	  }
	  
	  /**
	   * Method checks to see if -print is invoked as one of the options. Options will only be accepted before the airline/flight arguments. The method 
	   * will immediately return false once an argument without a '-' is encountered (this assumes that airline/flight arguments will not begin with a '-').
	   * @param arguments from command line
	   * @return true if one of the arguments classified as "options" is -print, otherwise return false
	   */
	  
	  private static boolean containsPrint(String [] args) {
		  for(int i = 0; i < args.length; ++i) {
			  if(args[i].equals("-print")) {
				  return true;
			  }
//			  if(args[i].charAt(0) != '-' && !args[i].contains(".txt")) {
//				  return false;
//			  }
		  }
		  
		  return false;
	  }
	  
	  /**
	   * Method checks to see if the number of arguments from the command line is 8 (the number of arguments necessary to create and airline and flight). 
	   * Arguments that are classified as "options" (-print, -textFile, and -README) will not be counted as part of the 8 arguments. But the method will verify that options
	   * appear before airline/flight arguments. This method assumes that any argument that begins with a '-' is an option and not an airline/flight argument. 
	   * It will also check that the argument immediately proceeding -textFile is a text file.
	   * @param arguments from the command line (includes "options" and "arguments"
	   * @return true if the number of arguments in 8 (not including options), false if there are more or less than 8 arguments (not including options)
	   */
	 private static boolean correctNumberOfArgs(String [] args) {
		 int argsCount = 0;
		 int optionsCount = 0;

		 for(int i = 0; i < args.length; ++i) {
			 if(args[i].charAt(0) != '-') {
				 ++argsCount;
			 }
			 else {
				 //verify options are -print, -textFile, or -README
				 if(args[i].equals("-print") || args[i].equals("-README")) {
					 ++optionsCount;
				 }
				 else if(args[i].equals("-host")){ 
					++optionsCount;
			    	++i;
				 }
				 else if(args[i].equals("-port")){ 
					++optionsCount;
			    	++i;
				 }
				 else if(args[i].equals("-search")){ 
					++optionsCount;
				 }
				 //if argument is not -print, -README, -textFile, or a filename directly proceeding -textFile, 
				 //assume they are a part of the airline/flight arguments
				 else {
					 ++argsCount;
				 }
			 }
		 }
		 
		 if(argsCount == 10) {
			 return true;
		 }
		 else {
			 return false;
		 }
	 }

	 /**
	  * This method parses the list from the command line, ignoring any "options" (-print or -README). This method assumes that there is the correct number
	  * of arguments necessary to save an airline and flight. The method performs validation on any argument with specific formats (see list below). Any validation
	  * errors will be added to an error list and returned to the caller. Arguments are expected in this order and format:
	  * 1 - -README or -print
	  * 2 - -README or -print
	  * 3 - Airline Name
	  * 4 - Flight number (numeric)
	  * 5 - Departure airport (3 character code)
	  * 6 - Departure date (MM/DD/YYYY)
	  * 7 - Departure time (HH:MM)
	  * 8 - Destination airport (3 character code)
	  * 9 - Arrival date (MM/DD/YYYY)
	  * 10 - Arrival time (HH:MM)
	  * @param arguments from command line
	  * @param Airline object
	  * @param Flight object that will be added to the Airline object
	  * @return list of validation errors (as Strings)
	  */
	private static List<String> createAirlineAndFlight(String[] args, Airline airline, Flight flight) {
	
		List<String> errorList = new ArrayList<String>();
		
		int startingPosition = args.length - 10; //accounts for any options included in the argument list
		
		for (int i = startingPosition; i < args.length; ++i) {
			
			if(i == startingPosition) {
		    	 airline.setName(args[i]);
			}
			else if(i == startingPosition+1) {
		    	try {
		    		int number = Integer.valueOf(args[i]);
		    		flight.setNumber(number);
		    	} catch (NumberFormatException e) {
		    		errorList.add("ERROR: Flight number is non-numeric");
		    	} 
			}
			else if( i == startingPosition+2) {
				if(validateAirportCode(args[i])) {
					flight.setSource(args[i]);
				}
				else {
					errorList.add("ERROR: Source airport code is not valid. Valid codes are 3 characters long, may only contains letters, and must be a real code.");
				}
			}
			else if(i == startingPosition+3) {
				boolean validDate = validateDate(args[i]);
				boolean validTime = validateTime(args[i+1]);
				boolean validAmPm = validateAmPm(args[i+2]);
				
				if(validDate == false) {
					errorList.add("ERROR: Departure date is not in the correct format. (Correct format: MM/DD/YYYY)");
				}
				if(validTime == false || validAmPm == false) {
					errorList.add("ERROR: Departure time is not in the correct format. (Correct format: HH:MM AM/PM)");
				}
				
				else {
					String sb = new StringBuilder(args[i]).append(" ").append(args[i+1]).append(" ").append(args[i+2]).toString();
					SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
					Date date;
					try {
						date = formatter.parse(sb);
						flight.setDeparture(date);
					} catch (ParseException e) {
						errorList.add("ERROR: Depature date/time is not in the correct format. (Correct format: MM/DD/YYYY HH:MM AM/PM)");
					}
					i+=2;
				}
			}
			else if(i == startingPosition+6) {
				if(validateAirportCode(args[i])) {
					flight.setDestination(args[i]);
				}
				else {
					errorList.add("ERROR: Destination airport code is not valid. Valid codes are 3 characters long, may only contains letters, and must be a real code.");
				}
			}
			else if(i == startingPosition+7) {
				boolean validDate = validateDate(args[i]);
				boolean validTime = validateTime(args[i+1]);
				boolean validAmPm = validateAmPm(args[i+2]);
				
				if(validDate == false) {
					errorList.add("ERROR: Arrival date is not in the correct format. (Correct format: MM/DD/YYYY)");
				}
				if(validTime == false || validAmPm == false) {
					errorList.add("ERROR: Arrival time is not in the correct format. (Correct format: HH:MM AM/PM - not 24-hour format)");
				}
				
				else {
					String sb = new StringBuilder(args[i]).append(" ").append(args[i+1]).append(" ").append(args[i+2]).toString();
					SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
					Date date;
					try {
						date = formatter.parse(sb);
						flight.setArrival(date);
					} catch (ParseException e) {
						errorList.add("ERROR: Arrival date/time is not in the correct format. (Correct format: MM/DD/YYYY HH:MM AM/PM - not 24-hour format)");
					}
					i+=2;
				}
			}
		} 
		airline.addFlight(flight);
		
		return errorList;
	}
	
	 /**
	 * This methods checks to see is the argument following time is correctly "am" or "pm"
	 * @param amPm
	 * @return true if "am" or "pm", false otherwise
	 */
	private static boolean validateAmPm(String amPm) {
		if(amPm.toLowerCase().equals("am") || amPm.toLowerCase().equals("pm")) {
			return true;
		}
		return false;
	}


	/**
	 * This method checks to see if the airport code entered is a valid code. It is valid if it is exactly 3 characters long and only contains letters.
	 * This method does not check that the airport code is a "real" airport code. 
	 * @param airportCode
	 * @return true if the airport code passed in is valid, false otherwise
	 */
	private static boolean validateAirportCode(String airportCode) {
		if(airportCode.length() != 3) {
			return false;
		}
		if(airportCode.matches(".*\\d+.*")) {
			return false;
		}
		if(AirportNames.getName(airportCode.toUpperCase()) != null) {
			return true;
		}
		return false; 
	}

	/**
	 * This method validates the time string according to "HH:MM" format. The validation will accept single-digit hours but not single-digit minutes. For instance:
	 * 1:11 is accepted
	 * 12:12 is accepted
	 * 2:2 is not accepted
	 * 2 is not accepted
	 * @param time string
	 * @return true if time matches "HH:MM" format, false if it does not
	 */
	private static boolean validateTime(String time) {
		String[] timeFields = time.split(":");
		int hour;
		int minutes;
		
		if(timeFields.length != 2)
		{
			return false;
		}
		
		//validating the hours (HH)
		try {
    		hour = Integer.valueOf(timeFields[0]);
    		if(hour > 12) {
    			return false;
    		}
    	} catch (NumberFormatException e) {
    		return false;
    	}
		
		//validating the minutes (MM)
		try {
    		minutes = Integer.valueOf(timeFields[1]);
    		if(minutes > 60) {
    			return false;
    		}
    		
    		if(minutes < 10) {
    			//will not accept single-digit minutes. Must be two-digit, such as 02.
    			if(timeFields[1].length() < 2) {
    				return false; 
    			}
    		}
    	} catch (NumberFormatException e) {
    		return false;
    	}
			
		return true;
		
	}

	/**
	 * This method validates the date string according to "MM/DD/YYYY" format. The validation will accept single-digit month and day values but not years less
	 * than 4 digits. The method does not accept month values greater than 12 or day values greater than 31. For example: 
	 * Accepted dates: 1/1/2001; 01/01/2001; 01/1/2001; 1/01/2001
	 * Not accepted dates: 13/1/2001; 1/1/01; 1/32/2001
	 * @param date string
	 * @return true if date string matches MM/DD/YYYY format
	 */
	private static boolean validateDate(String date) {
		
		String[] dateFields = date.split("/");
		int month;
		int day;
		
		if(dateFields.length != 3) {
			return false;
		}
		
		try {
    		month = Integer.valueOf(dateFields[0]);
    	} catch (NumberFormatException e) {
    		return false;
    	}
		
		if(month > 12) {
			return false;
		}
		
		try {
    		day = Integer.valueOf(dateFields[1]);
    	} catch (NumberFormatException e) {
    		return false;
    	}
		
		if(day > 31) {
			return false;
		}
		
		if(dateFields[2].length() != 4) {
			return false;
		}
		
		return true;
	}

	/**
	 * Displays the read me text
	 */
	private static void printReadMe() {
		System.out.println("\n------------------README------------------\n");
		System.out.println("DENISE BANH\nCS510 | ADVANCED PROGRAMMING WITH JAVA");
		System.out.println("PROJECT 4: A RESTFUL AIRLINE WEBSERVICE\n");
		System.out.println("This application contains classes that will track an airline as well as flights for the airline.\n"
				+ "The airline and flights will be saved via a RESTful service. Airline and flight information can be entered \n"
				+ "into the command line. Additional flights that match the airline will be saved as well. Flights can also be searched \n"
				+ "by passing in the airline name,  src, and destination.\n"
				+ "Options are:\n"
				+ "-print		displays the airline that was just saved\n"
				+ "-host		host name\n"
				+ "-port		port number\n"
				+ "-search		search for flights from a departure airport to a destination airport\n"
				+ "Arguments are:\n"
				+ "[1] airline name\n"
				+ "[2] flight number\n"
				+ "[3] departure airport\n"
				+ "[4] departure date\n"
				+ "[5] departure time\n"
				+ "[6] departure AM/PM\n"
				+ "[7] destination airport\n"
				+ "[8] arrival date\n"
				+ "[9] arrival time\n"
				+ "[10] arrival AM/PM\n"
				+ "\n----------------END README----------------\n");
	}
}
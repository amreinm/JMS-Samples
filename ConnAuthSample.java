// SCCSID "@(#) samples/jms/ConnAuthSample.java, jmscc.samples, k000, k000-L090724  1.7 09/04/18 08:03:50"
/*
 * <N_OCO_COPYRIGHT>
 * Licensed Materials - Property of IBM
 * 
 * 5724-H72, 5655-R36, 5724-L26, 5655-L82     
 * 
 * (c) Copyright IBM Corp. 2008, 2009 All Rights Reserved.
 * 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * <NOC_COPYRIGHT>
 *
 * ******************** specials for ConnAuth Test (August 2014)
 *                    * no Reply-To-Queue (inherited from template)
 *                    * additional variables for userID and password
 */
package demo;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.BytesMessage;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsDestination;

import com.ibm.mq.*;

/**
 * A JMS producer (sender or publisher) application that sends a simple message to the named
 * destination (queue or topic) by looking up the connection factory instance and the destination
 * instance in an initial context (This sample supports file system context only).
 * 
 * Notes:
 * 
 * API type: IBM JMS API (v1.1, unified domain)
 * 
 * Messaging domain: Point-to-point or Publish-Subscribe
 * 
 * Provider type: WebSphere MQ
 * 
 * Connection mode: Client connection or bindings connection
 * 
 * JNDI in use: Yes
 * 
 * Usage:
 * 
 * java ConnAuthSample -i initialContext -c connectionFactory -d destination -u userid -p password
 * 
 * for example:
 * 
 * java demo.ConnAuthSample -i file:/C:/$Data/JMSContext -c jms.QMCI -d jms.DEMO.REPLY -u WBI33 -p K0PLEX
 */
public class ConnAuthSample {

  private static String initialContextUrl = null;
  private static String connectionFactoryFromJndi = null;
  private static String destinationFromJndi = null;
  private static String Userid; 
  private static String Password; 


  private static String destinationReplyQ = "jms.DEMO.REPLY";


  // System exit status value (assume unset value to be 1)
  private static int status = 1;

  /**
   * Main method
   * 
   * @param args
   */
  public static void main(String[] args) {
    // Parse the arguments
    parseArgs(args);

    // Variables
    Connection connection = null;
    Session session = null;
    Destination requestQ = null;
    Destination replyQ = null;
    MessageProducer producer = null;

    try {
      // Instantiate the initial context
      String contextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
      Hashtable environment = new Hashtable();
      environment.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
      environment.put(Context.PROVIDER_URL, initialContextUrl);
      Context context = new InitialDirContext(environment);
      System.out.println("Initial context found!");

      // Lookup the connection factory
      JmsConnectionFactory cf = (JmsConnectionFactory) context.lookup(connectionFactoryFromJndi);

      // Lookup the requestQ
      requestQ = (JmsDestination) context.lookup(destinationFromJndi);

      
      // Create JMS objects
      connection = cf.createConnection(Userid,Password);
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      producer = session.createProducer(requestQ);

      // Create the (text) message
      TextMessage message = session
          .createTextMessage("Demo-Message von Client nach Connect mit Userid"+ Userid + " , Passwort= "+ Password);
      
      message.setStringProperty("JMS_IBM_Character_Set","1208");   
      message.setStringProperty("JMS_IBM_Format", MQC.MQFMT_STRING);
      message.setIntProperty("JMS_IBM_Encoding",546);    


      // add more properties- just for test
      message.setStringProperty("Event_Location", "Munich");
      message.setStringProperty("Userid", Userid);
      message.setStringProperty("Password", Password);     

      // Start the connection
      connection.start();

      // And, send the message
      producer.send(message);
      System.out.println("Sent message:\n" + message);

      recordSuccess();
    }
    catch (JMSException jmsex) {
      recordFailure(jmsex);
    }
    catch (NamingException ne) {
      System.out.println("The initial context could not be instantiated, or the lookup failed.");
      recordFailure(ne);
    }
    finally {
      if (producer != null) {
        try {
          producer.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Producer could not be closed.");
          recordFailure(jmsex);
        }
      }

      if (session != null) {
        try {
          session.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Session could not be closed.");
          recordFailure(jmsex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Connection could not be closed.");
          recordFailure(jmsex);
        }
      }
    }
    System.exit(status);
    return;
  } // end main()

  /**
   * Process a JMSException and any associated inner exceptions.
   * 
   * @param jmsex
   */
  private static void processJMSException(JMSException jmsex) {
    System.out.println(jmsex);
    Throwable innerException = jmsex.getLinkedException();
    if (innerException != null) {
      System.out.println("Inner exception(s):");
    }
    while (innerException != null) {
      System.out.println(innerException);
      innerException = innerException.getCause();
    }
    return;
  }

  /**
   * Record this run as successful.
   */
  private static void recordSuccess() {
    System.out.println("SUCCESS");
    status = 0;
    return;
  }

  /**
   * Record this run as failure.
   * 
   * @param ex
   */
  private static void recordFailure(Exception ex) {
    if (ex != null) {
      if (ex instanceof JMSException) {
        processJMSException((JMSException) ex);
      }
      else {
        System.out.println(ex);
      }
    }
    System.out.println("FAILURE");
    status = -1;
    return;
  }

  /**
   * Parse user supplied arguments.
   * 
   * @param args
   */
  private static void parseArgs(String[] args) {
    try {
      int length = args.length;
      if (length == 0) {
        throw new IllegalArgumentException("No arguments! Mandatory arguments must be specified.");
      }
      
  /** if (length != 5) { throw new IllegalArgumentException("Incorrect number of arguments!");       }   */

      int i = 0;

      while (i < length) {
        if ((args[i]).charAt(0) != '-') {
          throw new IllegalArgumentException("Expected a '-' character next: " + args[i]);
        }

        char opt = (args[i]).toLowerCase().charAt(1);

        switch (opt) {
          case 'i' :
            initialContextUrl = args[++i];
            break;
          case 'c' :
            connectionFactoryFromJndi = args[++i];
            break;
          case 'd' :
            destinationFromJndi = args[++i];
            break;
          case 'u' :
            Userid = args[++i];
            break;
          case 'p' :
            Password = args[++i];
            break;
          default : {
            throw new IllegalArgumentException("Unknown argument: " + opt);
          }
        }

        ++i;
      }

      if (initialContextUrl == null) {
        throw new IllegalArgumentException("An initial context must be specified.");
      }

      if (connectionFactoryFromJndi == null) {
        throw new IllegalArgumentException(
            "A connection factory to lookup in the initial context must be specified.");
      }

      if (destinationFromJndi == null) {
        throw new IllegalArgumentException(
            "A destination to lookup in the initial context must be specified.");
      }
      if (Userid == null) {
        throw new IllegalArgumentException(
            "A value for the Property_1 must be specified.");
      }
      if (Password == null) {
        throw new IllegalArgumentException(
            "A value for the Property_2 must be specified.");
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      printUsage();
      System.exit(-1);
    }
    return;
  }

  /**
   * Display usage help.
   */
  private static void printUsage() {
    System.out.println("\nUsage:");
    System.out.println("ConnAuthSample -i initialContext -c connectionFactory -d requestQ -u Userid -p Password");
    return;
  }

} // end class

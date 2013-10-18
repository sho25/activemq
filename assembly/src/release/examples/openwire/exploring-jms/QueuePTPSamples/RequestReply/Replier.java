begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * � 2001-2009, Progress Software Corporation and/or its subsidiaries or affiliates.  All rights reserved.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.   Sample Application  Writing a Basic JMS Application with Point-to-Point Queues, using:     - Synchronous Request/Reply     - javax.jms.QueueRequestor class     - JMSReplyTo Header  When this program runs, it waits for messages on the queue, "SampleQ1" (by default). When that message arrives, a response based on the request is sent back to the "Requestor" specified in the JMSReplyTo header.  This sample replies with a simple text manipulation of the request; the text is either folded to all UPPERCASE or all lowercase.  Usage:   java Replier -b<broker:port> -u<username> -p<password> -qr<queue> -m<code>       -b broker:port points to your message broker                      Default: tcp://localhost:61616       -u username    must be unique (but is not checked)                      Default: SampleReplier       -p password    password for user (not checked)                      Default: password       -qr queue      name of queue for receiving requests                      Default:  Q1       -m mode        replier mode (uppercase, or lowercase)                      Default: uppercase  Suggested demonstration:   - In a console window with the environment set,     start a copy of the Replier. For example:        java Replier -u SampleQReplier   - In another console window, start a Requestor.     For example:        java Requestor -u SampleQRequestor   - Enter text in the Requestor window then press Enter.     The Replier responds with the message in all uppercase characters.   - Start other Requestors with different user names to see that     replies are not broadcast to all users. For example:        java Requestor -u SampleRequestorFoo    - Start other Repliers.   - See that only one replier is receiving messages,(as it should).   - See the Requestor only receives one response.        java Replier -u toLower -m lowercase   */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|Replier
implements|implements
name|javax
operator|.
name|jms
operator|.
name|MessageListener
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_BROKER_NAME
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_USER_NAME
init|=
literal|"SampleReplier"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_QUEUE
init|=
literal|"Q1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_MODE
init|=
literal|"uppercase"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|UPPERCASE
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|LOWERCASE
init|=
literal|1
decl_stmt|;
specifier|private
name|javax
operator|.
name|jms
operator|.
name|Connection
name|connect
init|=
literal|null
decl_stmt|;
specifier|private
name|javax
operator|.
name|jms
operator|.
name|Session
name|session
init|=
literal|null
decl_stmt|;
specifier|private
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
name|replier
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|imode
init|=
name|UPPERCASE
decl_stmt|;
comment|/** Create JMS client for sending and receiving messages. */
specifier|private
name|void
name|start
parameter_list|(
name|String
name|broker
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|rQueue
parameter_list|,
name|String
name|mode
parameter_list|)
block|{
comment|// Set the operation mode
name|imode
operator|=
operator|(
name|mode
operator|.
name|equals
argument_list|(
literal|"uppercase"
argument_list|)
operator|)
condition|?
name|UPPERCASE
else|:
name|LOWERCASE
expr_stmt|;
comment|// Create a connection.
try|try
block|{
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
name|factory
decl_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|username
argument_list|,
name|password
argument_list|,
name|broker
argument_list|)
expr_stmt|;
name|connect
operator|=
name|factory
operator|.
name|createConnection
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|session
operator|=
name|connect
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|javax
operator|.
name|jms
operator|.
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|JMSException
name|jmse
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: Cannot connect to Broker - "
operator|+
name|broker
argument_list|)
expr_stmt|;
name|jmse
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Create Receivers to application queues as well as a Sender
comment|// to use for JMS replies.
try|try
block|{
name|javax
operator|.
name|jms
operator|.
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|rQueue
argument_list|)
decl_stmt|;
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
name|receiver
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|replier
operator|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// Queue will be set for each reply
comment|// Now that all setup is complete, start the Connection
name|connect
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|JMSException
name|jmse
parameter_list|)
block|{
name|jmse
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exit
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// Read standard input waiting for "EXIT" command.
name|java
operator|.
name|io
operator|.
name|BufferedReader
name|stdin
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|BufferedReader
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nReplier application:\n"
operator|+
literal|"============================\n"
operator|+
literal|"The application user "
operator|+
name|username
operator|+
literal|" connects to the broker at "
operator|+
name|DEFAULT_BROKER_NAME
operator|+
literal|".\n"
operator|+
literal|"The application gets requests with JMSReplyTo set on the "
operator|+
name|DEFAULT_QUEUE
operator|+
literal|" queue."
operator|+
literal|"The message is transformed to all uppercase or all lowercase, and then returned to the requestor."
operator|+
literal|"The Requestor application displays the result.\n\n"
operator|+
literal|"Enter EXIT or press Ctrl+C to close the Replier.\n"
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|stdin
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"EXIT"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nStopping Replier. Please wait..\n>"
argument_list|)
expr_stmt|;
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Handle the message.      * (as specified in the javax.jms.MessageListener interface).      *      * IMPORTANT NOTES:      * (1)We must follow the design paradigm for JMS      *    synchronous requests.  That is, we must:      *     - get the message      *     - look for the header specifying JMSReplyTo      *     - send a reply to the queue specified there.      *    Failing to follow these steps might leave the originator      *    of the request waiting forever.      * (2)Unlike the 'Talk' sample and others using an asynchronous      *    message listener, it is possible here to use ONLY      *    ONE SESSION because the messages being sent are sent from      *    the same thread of control handling message delivery. For      *    more information see the JMS spec v1.0.2 section 4.4.6.      *      * OPTIONAL BEHAVIOR: The following actions taken by the      * message handler represent good programming style, but are      * not required by the design paradigm for JMS requests.      *   - set the JMSCorrelationID (tying the response back to      *     the original request.      *   - use transacted session "commit" so receipt of request      *     won't happen without the reply being sent.      *      */
specifier|public
name|void
name|onMessage
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Message
name|aMessage
parameter_list|)
block|{
try|try
block|{
comment|// Cast the message as a text message.
name|javax
operator|.
name|jms
operator|.
name|TextMessage
name|textMessage
init|=
operator|(
name|javax
operator|.
name|jms
operator|.
name|TextMessage
operator|)
name|aMessage
decl_stmt|;
comment|// This handler reads a single String from the
comment|// message and prints it to the standard output.
try|try
block|{
name|String
name|string
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[Request] "
operator|+
name|string
argument_list|)
expr_stmt|;
comment|// Check for a ReplyTo Queue
name|javax
operator|.
name|jms
operator|.
name|Queue
name|replyQueue
init|=
operator|(
name|javax
operator|.
name|jms
operator|.
name|Queue
operator|)
name|aMessage
operator|.
name|getJMSReplyTo
argument_list|()
decl_stmt|;
if|if
condition|(
name|replyQueue
operator|!=
literal|null
condition|)
block|{
comment|// Send the modified message back.
name|javax
operator|.
name|jms
operator|.
name|TextMessage
name|reply
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|imode
operator|==
name|UPPERCASE
condition|)
name|reply
operator|.
name|setText
argument_list|(
literal|"Uppercasing-"
operator|+
name|string
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|reply
operator|.
name|setText
argument_list|(
literal|"Lowercasing-"
operator|+
name|string
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|reply
operator|.
name|setJMSCorrelationID
argument_list|(
name|aMessage
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|replier
operator|.
name|send
argument_list|(
name|replyQueue
argument_list|,
name|reply
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|JMSException
name|jmse
parameter_list|)
block|{
name|jmse
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|RuntimeException
name|rte
parameter_list|)
block|{
name|rte
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Cleanup resources cleanly and exit. */
specifier|private
name|void
name|exit
parameter_list|()
block|{
try|try
block|{
name|connect
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|JMSException
name|jmse
parameter_list|)
block|{
name|jmse
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// NOTE: the remainder of this sample deals with reading arguments
comment|// and does not utilize any JMS classes or code.
comment|//
comment|/** Main program entry point. */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
comment|// Values to be read from parameters
name|String
name|broker
init|=
name|DEFAULT_BROKER_NAME
decl_stmt|;
name|String
name|username
init|=
name|DEFAULT_USER_NAME
decl_stmt|;
name|String
name|password
init|=
name|DEFAULT_PASSWORD
decl_stmt|;
name|String
name|queue
init|=
name|DEFAULT_QUEUE
decl_stmt|;
name|String
name|mode
init|=
name|DEFAULT_MODE
decl_stmt|;
comment|// Check parameters
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argv
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|arg
init|=
name|argv
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-b"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|argv
operator|.
name|length
operator|-
literal|1
operator|||
name|argv
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: missing broker name:port"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|broker
operator|=
name|argv
index|[
operator|++
name|i
index|]
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-u"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|argv
operator|.
name|length
operator|-
literal|1
operator|||
name|argv
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: missing user name"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|username
operator|=
name|argv
index|[
operator|++
name|i
index|]
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-p"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|argv
operator|.
name|length
operator|-
literal|1
operator|||
name|argv
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: missing password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|password
operator|=
name|argv
index|[
operator|++
name|i
index|]
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-qr"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|argv
operator|.
name|length
operator|-
literal|1
operator|||
name|argv
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: missing queue"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|queue
operator|=
name|argv
index|[
operator|++
name|i
index|]
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-m"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|argv
operator|.
name|length
operator|-
literal|1
operator|||
name|argv
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: missing mode"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|mode
operator|=
name|argv
index|[
operator|++
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|mode
operator|.
name|equals
argument_list|(
literal|"uppercase"
argument_list|)
operator|||
name|mode
operator|.
name|equals
argument_list|(
literal|"lowercase"
argument_list|)
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: mode must be 'uppercase' or 'lowercase'"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Invalid argument
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error: unexpected argument: "
operator|+
name|arg
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Start the JMS client.
name|Replier
name|replier
init|=
operator|new
name|Replier
argument_list|()
decl_stmt|;
name|replier
operator|.
name|start
argument_list|(
name|broker
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|queue
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/** Prints the usage. */
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|StringBuffer
name|use
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"usage: java Replier (options) ...\n\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"options:\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"  -b name:port Specify name:port of broker.\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"               Default broker: "
operator|+
name|DEFAULT_BROKER_NAME
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"  -u name      Specify unique user name.\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"               Default broker: "
operator|+
name|DEFAULT_USER_NAME
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"  -p password  Specify password for user.\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"               Default password: "
operator|+
name|DEFAULT_PASSWORD
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"  -m mode      Replier operating mode - uppercase or lowercase.\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"               Default mode: "
operator|+
name|DEFAULT_MODE
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"  -qr queue    Specify name of queue for receiving.\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"               Default queue: "
operator|+
name|DEFAULT_QUEUE
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|use
operator|.
name|append
argument_list|(
literal|"  -h           This help screen.\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|use
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * � 2001-2009, Progress Software Corporation and/or its subsidiaries or affiliates.  All rights reserved.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.    Sample Application  Writing a Basic JMS Application using:     - Synchronous Request/Reply     - Publish/Subscribe     - javax.jms.TopicRequestor class     - JMSReplyTo Header  When this program runs, it reads input from System.in and then sends the text as a message to the topic "progress.samples.request".  A "Replier" class should be waiting for the request. It will reply with a message.  NOTE: You must run the TopicReplier first. (Otherwise the syncronous request will block forever.)  Usage:   java TopicRequestor -b<broker:port> -u<username> -p<password>       -b broker:port points to your message broker                      Default: tcp://localhost:61616       -u username    must be unique (but is not checked)                      Default: SampleRequestor       -p password    password for user (not checked)                      Default: password  Suggested demonstration:   - In a console window with the environment set,     start a copy of the Replier. For example:        java TopicReplier -u SampleReplier   - In another console window, start a Requestor.     For example:        java TopicRequestor -u SampleRequestor   - Enter text in the Requestor window then press Enter.        The Replier responds with the message in all uppercase characters.   - Start other Requestors with different user names to see that     replies are not broadcast to all users. For example:        java TopicRequestor -u SampleRequestorToo    - Start other Repliers.   - See that all repliers are receiving all the messages,(as they should).   - See the Requestor only receives one response.        java TopicReplier -u toLower -m lowercase */
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
name|TopicRequestor
block|{
specifier|private
specifier|static
specifier|final
name|String
name|APP_TOPIC
init|=
literal|"jms.samples.request"
decl_stmt|;
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
literal|"SampleRequestor"
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
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
name|connect
init|=
literal|null
decl_stmt|;
specifier|private
name|javax
operator|.
name|jms
operator|.
name|TopicSession
name|session
init|=
literal|null
decl_stmt|;
comment|/** Create JMS client for publishing and subscribing to messages. */
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
parameter_list|)
block|{
comment|// Create a connection.
try|try
block|{
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
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
name|createTopicConnection
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
name|createTopicSession
argument_list|(
literal|false
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
comment|// Create Topic for all requests.  TopicRequestor will be created
comment|// as needed.
name|javax
operator|.
name|jms
operator|.
name|Topic
name|topic
init|=
literal|null
decl_stmt|;
try|try
block|{
name|topic
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|APP_TOPIC
argument_list|)
expr_stmt|;
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
block|}
try|try
block|{
comment|// Read all standard input and send it as a message.
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nRequestor application:\n"
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
literal|"The application uses a TopicRequestor to on the "
operator|+
name|APP_TOPIC
operator|+
literal|" topic."
operator|+
literal|"The Replier application gets the message, and transforms it."
operator|+
literal|"The Requestor application displays the result.\n\n"
operator|+
literal|"Type some mixed case text, and then press Enter to make a request.\n"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
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
condition|)
name|exit
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|javax
operator|.
name|jms
operator|.
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|username
operator|+
literal|": "
operator|+
name|s
argument_list|)
expr_stmt|;
comment|// Instead of publishing, we will use a TopicRequestor.
name|javax
operator|.
name|jms
operator|.
name|TopicRequestor
name|requestor
init|=
operator|new
name|javax
operator|.
name|jms
operator|.
name|TopicRequestor
argument_list|(
name|session
argument_list|,
name|topic
argument_list|)
decl_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Message
name|response
init|=
name|requestor
operator|.
name|request
argument_list|(
name|msg
argument_list|)
decl_stmt|;
comment|// The message should be a TextMessage.  Just report it.
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
name|response
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[Reply] "
operator|+
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
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
comment|// Start the JMS client for the "chat".
name|TopicRequestor
name|requestor
init|=
operator|new
name|TopicRequestor
argument_list|()
decl_stmt|;
name|requestor
operator|.
name|start
argument_list|(
name|broker
argument_list|,
name|username
argument_list|,
name|password
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
literal|"usage: java Requestor (options) ...\n\n"
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


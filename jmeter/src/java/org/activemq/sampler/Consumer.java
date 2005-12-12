begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Protique Ltd  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|sampler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|IdGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|connection
operator|.
name|ServerConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|SampleResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|util
operator|.
name|JMeterUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jorphan
operator|.
name|logging
operator|.
name|LoggingManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mr
operator|.
name|MantaAgent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueReceiver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|Consumer
extends|extends
name|Sampler
implements|implements
name|MessageListener
block|{
specifier|public
specifier|static
name|int
name|counter
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggingManager
operator|.
name|getLoggerForClass
argument_list|()
decl_stmt|;
comment|// Otherwise, the response is scanned for these strings
specifier|private
specifier|static
specifier|final
name|String
name|STATUS_PREFIX
init|=
name|JMeterUtils
operator|.
name|getPropDefault
argument_list|(
literal|"tcp.status.prefix"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STATUS_SUFFIX
init|=
name|JMeterUtils
operator|.
name|getPropDefault
argument_list|(
literal|"tcp.status.suffix"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STATUS_PROPERTIES
init|=
name|JMeterUtils
operator|.
name|getPropDefault
argument_list|(
literal|"tcp.status.properties"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Properties
name|statusProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|private
name|int
name|batchCounter
init|=
literal|0
decl_stmt|;
static|static
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Protocol Handler name="
operator|+
name|getClassname
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Status prefix="
operator|+
name|STATUS_PREFIX
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Status suffix="
operator|+
name|STATUS_SUFFIX
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Status properties="
operator|+
name|STATUS_PROPERTIES
argument_list|)
expr_stmt|;
if|if
condition|(
name|STATUS_PROPERTIES
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|STATUS_PROPERTIES
argument_list|)
decl_stmt|;
try|try
block|{
name|statusProps
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Successfully loaded properties"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Property file not found"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Property file error "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Constructor for ConsumerSampler object.      */
specifier|public
name|Consumer
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Created "
operator|+
name|this
argument_list|)
expr_stmt|;
name|protocolHandler
operator|=
name|getProtocol
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Using Protocol Handler: "
operator|+
name|protocolHandler
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Increments the int variable.      *      * @param count - variable incremented.      */
specifier|private
specifier|synchronized
name|void
name|count
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|counter
operator|+=
name|count
expr_stmt|;
block|}
comment|/**      * @return the current number of messages sent.      */
specifier|public
specifier|static
specifier|synchronized
name|int
name|resetCount
parameter_list|()
block|{
name|int
name|answer
init|=
name|counter
decl_stmt|;
name|counter
operator|=
literal|0
expr_stmt|;
return|return
name|answer
return|;
block|}
comment|/**      * Subscribes the subject.      *      * @throws JMSException      */
specifier|protected
name|void
name|subscribe
parameter_list|()
throws|throws
name|JMSException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getNoConsumer
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|subject
init|=
name|subjects
index|[
name|i
operator|%
name|getNoSubject
argument_list|()
index|]
decl_stmt|;
name|subscribe
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Subscribes the message.      *      * @param subject - subject to be subscribed.      * @throws JMSException      */
specifier|protected
name|void
name|subscribe
parameter_list|(
name|String
name|subject
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|ServerConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
name|this
operator|.
name|getURL
argument_list|()
argument_list|,
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|,
name|this
operator|.
name|getTopic
argument_list|()
argument_list|,
name|this
operator|.
name|getEmbeddedBroker
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getDurable
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|ServerConnectionFactory
operator|.
name|JORAM_SERVER
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|)
operator|)
operator|||
operator|(
name|ServerConnectionFactory
operator|.
name|MANTARAY_SERVER
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|)
operator|)
condition|)
block|{
comment|//Id set by server
block|}
else|else
block|{
name|IdGenerator
name|idGenerator
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGenerator
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//start connection before receiving messages.
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|ServerConnectionFactory
operator|.
name|createSession
argument_list|(
name|connection
argument_list|,
name|this
operator|.
name|getTransacted
argument_list|()
argument_list|,
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|,
name|this
operator|.
name|getTopic
argument_list|()
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|ServerConnectionFactory
operator|.
name|createDestination
argument_list|(
name|session
argument_list|,
name|subject
argument_list|,
name|this
operator|.
name|getURL
argument_list|()
argument_list|,
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|,
name|this
operator|.
name|getTopic
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ServerConnectionFactory
operator|.
name|OPENJMS_SERVER
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getDurable
argument_list|()
condition|)
block|{
name|consumer
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Queue
name|queue
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createQueue
argument_list|(
name|subject
argument_list|)
decl_stmt|;
name|QueueReceiver
name|receiver
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|consumer
operator|=
operator|(
name|MessageConsumer
operator|)
name|receiver
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ServerConnectionFactory
operator|.
name|MANTARAY_SERVER
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getDurable
argument_list|()
condition|)
block|{
name|consumer
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|MantaAgent
operator|.
name|getInstance
argument_list|()
operator|.
name|getAgentName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Queue
name|queue
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createQueue
argument_list|(
name|subject
argument_list|)
decl_stmt|;
name|QueueReceiver
name|receiver
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|consumer
operator|=
operator|(
name|MessageConsumer
operator|)
name|receiver
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|this
operator|.
name|getDurable
argument_list|()
operator|&&
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|setSession
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Processes the received message.      *      * @param message - message received by the listener.      */
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|Session
name|session
decl_stmt|;
comment|// lets force the content to be deserialized
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|count
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|getTransacted
argument_list|()
condition|)
block|{
name|batchCounter
operator|++
expr_stmt|;
if|if
condition|(
name|batchCounter
operator|==
name|this
operator|.
name|getBatchSize
argument_list|()
condition|)
block|{
name|batchCounter
operator|=
literal|0
expr_stmt|;
name|session
operator|=
name|this
operator|.
name|getSession
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to force deserialize the content "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Runs and subscribes to messages.      *      * @throws JMSException      */
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|JMSException
block|{
name|start
argument_list|()
expr_stmt|;
name|subscribe
argument_list|()
expr_stmt|;
block|}
comment|/**      * Retrieves the sample as SampleResult object. There are times that this      * is ignored.      *      * @param e - Entry object.      * @return Returns the sample result.      */
specifier|public
name|SampleResult
name|sample
parameter_list|(
name|Entry
name|e
parameter_list|)
block|{
comment|// Entry tends to be ignored ...
name|SampleResult
name|res
init|=
operator|new
name|SampleResult
argument_list|()
decl_stmt|;
name|res
operator|.
name|setSampleLabel
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setSamplerData
argument_list|(
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|sampleStart
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error running consumer "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|res
operator|.
name|setResponseCode
argument_list|(
literal|"500"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setResponseMessage
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Calculate response time
name|res
operator|.
name|sampleEnd
argument_list|()
expr_stmt|;
comment|// Set if we were successful or not
name|res
operator|.
name|setSuccessful
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**      * Starts an instance of the Consumer tool.      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"##########################################"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Consumer * start *"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"##########################################"
argument_list|)
expr_stmt|;
name|Consumer
name|cons
init|=
operator|new
name|Consumer
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|displayToolParameters
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|mqServer
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"SONICMQ"
argument_list|)
condition|)
block|{
name|cons
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|SONICMQ_SERVER
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"TIBCOMQ"
argument_list|)
condition|)
block|{
name|cons
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|TIBCOMQ_SERVER
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"JBOSSMQ"
argument_list|)
condition|)
block|{
name|cons
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|JBOSSMQ_SERVER
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"OPENJMS"
argument_list|)
condition|)
block|{
name|cons
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|OPENJMS_SERVER
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"JORAM"
argument_list|)
condition|)
block|{
name|cons
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|JORAM_SERVER
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"MANTARAY"
argument_list|)
condition|)
block|{
name|cons
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|MANTARAY_SERVER
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mqServer
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ACTIVEMQ"
argument_list|)
condition|)
block|{
comment|//Run with the default broker
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Please enter a valid mq server: [ "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"SONICMQ | "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"TIBCOMQ | "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"JBOSSMQ | "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"OPENJMS | "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"JORAM | "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"MANTARAY |"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ACTIVEMQ ]"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|cons
operator|.
name|setURL
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|cons
operator|.
name|setDuration
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|cons
operator|.
name|setRampUp
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|cons
operator|.
name|setNoConsumer
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|5
condition|)
block|{
name|cons
operator|.
name|setNoSubject
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|6
condition|)
block|{
name|cons
operator|.
name|setDurable
argument_list|(
name|args
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|7
condition|)
block|{
name|cons
operator|.
name|setTopic
argument_list|(
name|args
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|8
condition|)
block|{
name|cons
operator|.
name|setTransacted
argument_list|(
name|args
index|[
literal|8
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|cons
operator|.
name|getTransacted
argument_list|()
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|9
condition|)
block|{
name|cons
operator|.
name|setBatchSize
argument_list|(
name|args
index|[
literal|9
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|displayToolParameters
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Please specify the batch size."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Runnning Consumer tool with the following parameters:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Server="
operator|+
name|cons
operator|.
name|getMQServer
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"URL="
operator|+
name|cons
operator|.
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Duration="
operator|+
name|cons
operator|.
name|getDuration
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ramp up="
operator|+
name|cons
operator|.
name|getRampUp
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No consumer="
operator|+
name|cons
operator|.
name|getNoConsumer
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No Subject="
operator|+
name|cons
operator|.
name|getNoSubject
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Is Durable="
operator|+
name|cons
operator|.
name|getDurable
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Is Topic="
operator|+
name|cons
operator|.
name|getTopic
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Is Transacted="
operator|+
name|cons
operator|.
name|getTransacted
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Batch size="
operator|+
name|cons
operator|.
name|getBatchSize
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|cons
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Excception e="
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"##########################################"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Consumer * end *"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"##########################################"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Prints to the console the Consumer tool parameters.      */
specifier|private
specifier|static
name|void
name|displayToolParameters
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Consumer tool usage: "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Message Queue Server] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[URL] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Duration] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Ramp up] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[No. of consumer] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[No. of subject] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Delivery mode] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Is topic] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Is transacted] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Batch size] "
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


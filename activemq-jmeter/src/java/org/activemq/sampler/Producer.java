begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|apache
operator|.
name|jmeter
operator|.
name|engine
operator|.
name|event
operator|.
name|LoopIterationEvent
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
name|testelement
operator|.
name|TestListener
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
name|activemq
operator|.
name|util
operator|.
name|IdGenerator
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
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_comment
comment|/**  * A sampler which understands Tcp requests.  */
end_comment

begin_class
specifier|public
class|class
name|Producer
extends|extends
name|Sampler
implements|implements
name|TestListener
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
specifier|static
specifier|final
name|long
name|INSECONDS
init|=
literal|60
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MSGINTERVALINSECS
init|=
literal|60
decl_stmt|;
specifier|private
name|Timer
name|timerPublish
decl_stmt|;
specifier|private
name|Timer
name|timerPublishLoop
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
comment|//haveStatusProps = true;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
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
name|info
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
comment|/**      * Constructor for ProducerSampler object.      */
specifier|public
name|Producer
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
name|this
operator|.
name|getProtocol
argument_list|()
expr_stmt|;
comment|//from superclass sampler.
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
specifier|protected
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
comment|/**      * @return Returns the message.      */
specifier|protected
name|String
name|getMessage
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getMsgSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
literal|'X'
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Retrieves the message then sends it via tcp.      *      * @throws Exception      */
specifier|protected
name|void
name|publish
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|threadRampUp
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|getNoProducer
argument_list|()
operator|>
literal|0
condition|)
block|{
name|threadRampUp
operator|=
call|(
name|long
call|)
argument_list|(
call|(
name|double
call|)
argument_list|(
name|getRampUp
argument_list|()
operator|*
name|INSECONDS
argument_list|)
operator|/
operator|(
operator|(
name|double
operator|)
name|getNoProducer
argument_list|()
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
name|timerPublish
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
name|timerPublish
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|newThread
argument_list|()
argument_list|,
literal|0
argument_list|,
name|threadRampUp
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sends the information from the client via tcp.      *      * @param text    - message that is sent.      * @param subject - subject of the message to be sent.      * @throws JMSException      */
specifier|protected
name|void
name|publish
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|subject
parameter_list|)
throws|throws
name|JMSException
block|{
name|Destination
name|destination
init|=
literal|null
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
name|MessageProducer
name|publisher
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|connection
operator|=
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
name|getAsyncSend
argument_list|()
argument_list|)
expr_stmt|;
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
name|SWIFTMQ_SERVER
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
comment|//Id set be server
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
name|session
operator|=
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
expr_stmt|;
name|destination
operator|=
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
expr_stmt|;
if|if
condition|(
operator|(
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
if|if
condition|(
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TopicPublisher
name|topicPublisher
init|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createPublisher
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|)
decl_stmt|;
name|publisher
operator|=
name|topicPublisher
expr_stmt|;
block|}
else|else
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|QueueSender
name|queuePublisher
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createSender
argument_list|(
operator|(
name|Queue
operator|)
name|destination
argument_list|)
decl_stmt|;
name|publisher
operator|=
name|queuePublisher
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|(
name|ServerConnectionFactory
operator|.
name|SWIFTMQ_SERVER
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getMQServer
argument_list|()
argument_list|)
operator|)
operator|&&
operator|!
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|Queue
name|strQ
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"testqueue@router1"
argument_list|)
decl_stmt|;
name|QueueSender
name|queuePublisher
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createSender
argument_list|(
name|strQ
argument_list|)
decl_stmt|;
name|publisher
operator|=
name|queuePublisher
expr_stmt|;
block|}
else|else
block|{
name|publisher
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|msgIntervalInMins
init|=
name|this
operator|.
name|getMsgInterval
argument_list|()
decl_stmt|;
name|long
name|msgIntervalInSecs
init|=
name|msgIntervalInMins
operator|*
name|INSECONDS
decl_stmt|;
if|if
condition|(
name|msgIntervalInSecs
operator|<
literal|0
condition|)
block|{
name|msgIntervalInSecs
operator|=
name|MSGINTERVALINSECS
expr_stmt|;
block|}
if|if
condition|(
name|getDurable
argument_list|()
condition|)
block|{
name|publisher
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|publisher
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getDefMsgInterval
argument_list|()
condition|)
block|{
while|while
condition|(
operator|!
name|stopThread
condition|)
block|{
name|publishLoop
argument_list|(
name|session
argument_list|,
name|publisher
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
name|ServerConnectionFactory
operator|.
name|close
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// set the session, publisher and connection.
name|this
operator|.
name|setSession
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|this
operator|.
name|setPublisher
argument_list|(
name|publisher
argument_list|)
expr_stmt|;
name|this
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|timerPublishLoop
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
name|timerPublishLoop
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|publish
argument_list|()
argument_list|,
literal|0
argument_list|,
name|msgIntervalInSecs
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sends a message through MessageProducer object.      *      * @param session   - Session oject.      * @param publisher - MessageProducer object.      * @param text      - text that is used to create Message object.      * @throws JMSException      */
specifier|protected
name|void
name|publishLoop
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|publisher
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
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
name|publisher
operator|instanceof
name|TopicPublisher
condition|)
block|{
name|Message
name|message
init|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
operator|(
operator|(
name|TopicPublisher
operator|)
name|publisher
operator|)
operator|.
name|publish
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|publisher
operator|instanceof
name|QueueSender
condition|)
block|{
name|Message
name|message
init|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
operator|(
operator|(
name|QueueSender
operator|)
name|publisher
operator|)
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
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
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|count
argument_list|(
literal|1
argument_list|)
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
comment|/**      * Runs and publish the message.      *      * @throws Exception      */
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|start
argument_list|()
expr_stmt|;
name|publish
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
comment|//run the benchmark tool code
name|this
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Error running producer "
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
comment|/**      * Logs the end of the test. This is called only once per      * class.      */
specifier|public
name|void
name|testEnded
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" test ended"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs the host at the end of the test.      *      * @param host - the host to be logged.      */
specifier|public
name|void
name|testEnded
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" test ended on "
operator|+
name|host
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs the start of the test. This is called only once      * per class.      */
specifier|public
name|void
name|testStarted
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" test started"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs the host at the start of the test.     *      * @param host - the host to be logged.      */
specifier|public
name|void
name|testStarted
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" test started on "
operator|+
name|host
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs the iteration event.      *      * @param event      */
specifier|public
name|void
name|testIterationStart
parameter_list|(
name|LoopIterationEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" test iteration start on "
operator|+
name|event
operator|.
name|getIteration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates thread for publishing messages.      */
class|class
name|newThread
extends|extends
name|TimerTask
block|{
specifier|final
name|String
name|text
init|=
name|getMessage
argument_list|()
decl_stmt|;
name|int
name|numberOfProducer
init|=
name|getNoProducer
argument_list|()
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|counter
operator|<
name|numberOfProducer
condition|)
block|{
specifier|final
name|String
name|subject
init|=
name|subjects
index|[
name|counter
operator|%
name|getNoSubject
argument_list|()
index|]
decl_stmt|;
name|counter
operator|++
expr_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|stopThread
condition|)
block|{
return|return;
block|}
else|else
block|{
name|publish
argument_list|(
name|text
argument_list|,
name|subject
argument_list|)
expr_stmt|;
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
literal|"Error publishing message "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|timerPublish
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Starts the publish loop timer.      */
class|class
name|publish
extends|extends
name|TimerTask
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
operator|!
name|stopThread
condition|)
block|{
name|publishLoop
argument_list|(
name|getSession
argument_list|()
argument_list|,
name|getPublisher
argument_list|()
argument_list|,
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ServerConnectionFactory
operator|.
name|close
argument_list|(
name|getConnection
argument_list|()
argument_list|,
name|getSession
argument_list|()
argument_list|)
expr_stmt|;
name|timerPublishLoop
operator|.
name|cancel
argument_list|()
expr_stmt|;
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
literal|"Could not publish "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Starts an instance of the Producer tool.      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
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
literal|" Producer * start *"
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
name|Producer
name|prod
init|=
operator|new
name|Producer
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
name|prod
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
name|prod
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
name|prod
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
name|prod
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
name|prod
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
name|prod
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
literal|"SWIFTMQ"
argument_list|)
condition|)
block|{
name|prod
operator|.
name|setMQServer
argument_list|(
name|ServerConnectionFactory
operator|.
name|SWIFTMQ_SERVER
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
literal|"Please enter a valid server: [ "
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
literal|"JORAM |"
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
name|print
argument_list|(
literal|"SWIFTMQ |"
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
name|prod
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
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Please specify the URL."
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
name|prod
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
name|prod
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
name|prod
operator|.
name|setNoProducer
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
name|prod
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
name|prod
operator|.
name|setMsgSize
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
name|prod
operator|.
name|setDurable
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
name|prod
operator|.
name|setTopic
argument_list|(
name|args
index|[
literal|8
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
literal|9
condition|)
block|{
name|prod
operator|.
name|setTransacted
argument_list|(
name|args
index|[
literal|9
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|10
condition|)
block|{
name|prod
operator|.
name|setBatchSize
argument_list|(
name|args
index|[
literal|10
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
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|11
condition|)
block|{
name|prod
operator|.
name|setDefMsgInterval
argument_list|(
name|args
index|[
literal|11
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|prod
operator|.
name|getDefMsgInterval
argument_list|()
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|12
condition|)
block|{
name|prod
operator|.
name|setMsgInterval
argument_list|(
name|args
index|[
literal|12
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
literal|"Please specify the message interval."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|prod
operator|.
name|setDefMsgInterval
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
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
name|prod
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
name|prod
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
name|prod
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
name|prod
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
literal|"No. Producer="
operator|+
name|prod
operator|.
name|getNoProducer
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No. Subject="
operator|+
name|prod
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
literal|"Msg Size="
operator|+
name|prod
operator|.
name|getMsgSize
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
name|prod
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
name|prod
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
name|prod
operator|.
name|getTransacted
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|prod
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
literal|"Producer * end *"
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
comment|/**      * Prints to the console the Producer tool parameters.      */
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
literal|"Producer tool usage: "
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
literal|"[No. of producer] "
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
literal|"[Message size] "
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
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"[Has Message interval] "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[Message interval] "
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


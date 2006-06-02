begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|JMSException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
specifier|public
class|class
name|JmsProducerClient
extends|extends
name|JmsPerfClientSupport
block|{
specifier|private
name|ConnectionFactory
name|factory
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|factoryClass
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|brokerUrl
init|=
literal|""
decl_stmt|;
specifier|private
name|String
index|[]
name|destName
init|=
literal|null
decl_stmt|;
specifier|private
name|Destination
index|[]
name|dest
init|=
literal|null
decl_stmt|;
specifier|private
name|TextMessage
name|message
init|=
literal|null
decl_stmt|;
specifier|public
name|JmsProducerClient
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|destName
operator|=
operator|new
name|String
index|[]
block|{
name|destName
block|}
expr_stmt|;
block|}
specifier|public
name|JmsProducerClient
parameter_list|(
name|String
name|factoryClass
parameter_list|,
name|String
name|brokerUrl
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|factoryClass
operator|=
name|factoryClass
expr_stmt|;
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
name|this
operator|.
name|destName
operator|=
operator|new
name|String
index|[]
block|{
name|destName
block|}
expr_stmt|;
block|}
specifier|public
name|JmsProducerClient
parameter_list|(
name|String
name|brokerUrl
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
name|this
operator|.
name|destName
operator|=
operator|new
name|String
index|[]
block|{
name|destName
block|}
expr_stmt|;
block|}
specifier|public
name|JmsProducerClient
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|,
name|String
index|[]
name|destName
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|destName
operator|=
name|destName
expr_stmt|;
block|}
specifier|public
name|JmsProducerClient
parameter_list|(
name|String
name|factoryClass
parameter_list|,
name|String
name|brokerUrl
parameter_list|,
name|String
index|[]
name|destName
parameter_list|)
block|{
name|this
operator|.
name|factoryClass
operator|=
name|factoryClass
expr_stmt|;
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
name|this
operator|.
name|destName
operator|=
name|destName
expr_stmt|;
block|}
specifier|public
name|JmsProducerClient
parameter_list|(
name|String
name|brokerUrl
parameter_list|,
name|String
index|[]
name|destName
parameter_list|)
block|{
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
name|this
operator|.
name|destName
operator|=
name|destName
expr_stmt|;
block|}
specifier|public
name|void
name|createProducer
parameter_list|()
throws|throws
name|JMSException
block|{
name|createProducer
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|createProducer
parameter_list|(
name|Map
name|settings
parameter_list|)
throws|throws
name|JMSException
block|{
name|createProducer
argument_list|(
literal|0
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|createProducer
parameter_list|(
name|int
name|messageSize
parameter_list|,
name|Map
name|settings
parameter_list|)
throws|throws
name|JMSException
block|{
name|addConfigParam
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|createProducer
argument_list|(
name|messageSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|createProducer
parameter_list|(
name|int
name|messageSize
parameter_list|)
throws|throws
name|JMSException
block|{
name|listener
operator|.
name|onConfigStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Create connection factory
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|createConnectionFactory
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|factoryClass
operator|!=
literal|null
condition|)
block|{
name|createConnectionFactory
argument_list|(
name|factoryClass
argument_list|,
name|brokerUrl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
expr_stmt|;
block|}
name|createConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
expr_stmt|;
comment|// Create destinations
name|dest
operator|=
operator|new
name|Destination
index|[
name|destName
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destName
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|destName
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"topic://"
argument_list|)
condition|)
block|{
name|dest
index|[
name|i
index|]
operator|=
name|createTopic
argument_list|(
name|destName
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"topic://"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destName
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"queue://"
argument_list|)
condition|)
block|{
name|dest
index|[
name|i
index|]
operator|=
name|createQueue
argument_list|(
name|destName
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"queue://"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dest
index|[
name|i
index|]
operator|=
name|createQueue
argument_list|(
name|destName
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Create actual message producer
if|if
condition|(
name|dest
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|createMessageProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createMessageProducer
argument_list|(
name|dest
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Create message to sent
if|if
condition|(
name|messageSize
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|val
init|=
operator|new
name|byte
index|[
name|messageSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|val
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|String
name|buff
init|=
operator|new
name|String
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|message
operator|=
name|createTextMessage
argument_list|(
name|buff
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onConfigEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendCountBasedMessages
parameter_list|(
name|long
name|messageCount
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// Parse through different ways to send messages
comment|// Avoided putting the condition inside the loop to prevent effect on performance
try|try
block|{
name|getConnection
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Send one type of message only, avoiding the creation of different messages on sending
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
comment|// Send to more than one actual destination
if|if
condition|(
name|dest
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dest
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Send to only one actual destination
block|}
else|else
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Send different type of messages using indexing to identify each one.
comment|// Message size will vary. Definitely slower, since messages properties
comment|// will be set individually each send.
block|}
else|else
block|{
comment|// Send to more than one actual destination
if|if
condition|(
name|dest
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dest
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|createTextMessage
argument_list|(
literal|"Text Message ["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Send to only one actual destination
block|}
else|else
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
literal|"Text Message ["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|getConnection
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sendTimeBasedMessages
parameter_list|(
name|long
name|duration
parameter_list|)
throws|throws
name|JMSException
block|{
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|duration
decl_stmt|;
comment|// Parse through different ways to send messages
comment|// Avoided putting the condition inside the loop to prevent effect on performance
comment|// Send one type of message only, avoiding the creation of different messages on sending
try|try
block|{
name|getConnection
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
comment|// Send to more than one actual destination
if|if
condition|(
name|dest
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dest
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Send to only one actual destination
block|}
else|else
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
condition|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Send different type of messages using indexing to identify each one.
comment|// Message size will vary. Definitely slower, since messages properties
comment|// will be set individually each send.
block|}
else|else
block|{
comment|// Send to more than one actual destination
name|long
name|count
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dest
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|createTextMessage
argument_list|(
literal|"Text Message ["
operator|+
name|count
operator|++
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Send to only one actual destination
block|}
else|else
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
condition|)
block|{
name|getMessageProducer
argument_list|()
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
literal|"Text Message ["
operator|+
name|count
operator|++
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|getConnection
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|duration
init|=
literal|1
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
name|long
name|rampUpTime
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
name|long
name|rampDownTime
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
name|long
name|interval
init|=
literal|1000
decl_stmt|;
name|PerfMeasurementTool
name|tool
init|=
operator|new
name|PerfMeasurementTool
argument_list|()
decl_stmt|;
name|tool
operator|.
name|setDuration
argument_list|(
name|duration
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setRampUpTime
argument_list|(
name|rampUpTime
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setRampDownTime
argument_list|(
name|rampDownTime
argument_list|)
expr_stmt|;
name|JmsProducerClient
index|[]
name|client
init|=
operator|new
name|JmsProducerClient
index|[
literal|10
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|client
index|[
name|i
index|]
operator|=
operator|new
name|JmsProducerClient
argument_list|(
literal|"org.apache.activemq.ActiveMQConnectionFactory"
argument_list|,
literal|"tcp://localhost:61616"
argument_list|,
literal|"topic://TEST.FOO"
argument_list|)
expr_stmt|;
name|client
index|[
name|i
index|]
operator|.
name|addConfigParam
argument_list|(
literal|"factory.asyncSend"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
index|[
name|i
index|]
operator|.
name|setPerfEventListener
argument_list|(
operator|new
name|PerfEventAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|client
index|[
name|i
index|]
operator|.
name|createProducer
argument_list|()
expr_stmt|;
name|tool
operator|.
name|registerClient
argument_list|(
name|client
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|tool
operator|.
name|startSampler
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|JmsProducerClient
name|p
init|=
name|client
index|[
name|i
index|]
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|p
operator|.
name|sendTimeBasedMessages
argument_list|(
name|duration
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


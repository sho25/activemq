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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|TextMessage
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
name|JmsProducerClient
extends|extends
name|JmsPerformanceSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsProducerClient
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_CONFIG_PRODUCER
init|=
literal|"producer."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TIME_BASED_SENDING
init|=
literal|"time"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COUNT_BASED_SENDING
init|=
literal|"count"
decl_stmt|;
specifier|protected
name|Properties
name|jmsProducerSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|protected
name|MessageProducer
name|jmsProducer
decl_stmt|;
specifier|protected
name|TextMessage
name|jmsTextMessage
decl_stmt|;
specifier|protected
name|int
name|messageSize
init|=
literal|1024
decl_stmt|;
comment|// Send 1kb messages by default
specifier|protected
name|long
name|sendCount
init|=
literal|1000000
decl_stmt|;
comment|// Send a million messages by default
specifier|protected
name|long
name|sendDuration
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// Send for 5 mins by default
specifier|protected
name|String
name|sendType
init|=
name|TIME_BASED_SENDING
decl_stmt|;
specifier|public
name|void
name|sendMessages
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onConfigEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Send a specific number of messages
if|if
condition|(
name|sendType
operator|.
name|equalsIgnoreCase
argument_list|(
name|COUNT_BASED_SENDING
argument_list|)
condition|)
block|{
name|sendCountBasedMessages
argument_list|(
name|getSendCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Send messages for a specific duration
block|}
else|else
block|{
name|sendTimeBasedMessages
argument_list|(
name|getSendDuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|Destination
index|[]
name|dest
init|=
name|createDestination
argument_list|()
decl_stmt|;
comment|// Create a producer, if none is created.
if|if
condition|(
name|getJmsProducer
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dest
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|createJmsProducer
argument_list|(
name|dest
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createJmsProducer
argument_list|()
expr_stmt|;
block|}
block|}
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
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Send one type of message only, avoiding the creation of different messages on sending
if|if
condition|(
name|getJmsTextMessage
argument_list|()
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
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|getJmsTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Send to only one actual destination
block|}
else|else
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|getJmsTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
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
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|createJmsTextMessage
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
comment|// Send to only one actual destination
block|}
else|else
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|createJmsTextMessage
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
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
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
name|Destination
index|[]
name|dest
init|=
name|createDestination
argument_list|()
decl_stmt|;
comment|// Create a producer, if none is created.
if|if
condition|(
name|getJmsProducer
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dest
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|createJmsProducer
argument_list|(
name|dest
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createJmsProducer
argument_list|()
expr_stmt|;
block|}
block|}
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
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onPublishStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Send one type of message only, avoiding the creation of different messages on sending
if|if
condition|(
name|getJmsTextMessage
argument_list|()
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
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|getJmsTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Send to only one actual destination
block|}
else|else
block|{
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
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|getJmsTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|incThroughput
argument_list|()
expr_stmt|;
block|}
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
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|dest
index|[
name|j
index|]
argument_list|,
name|createJmsTextMessage
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
comment|// Send to only one actual destination
block|}
else|else
block|{
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
name|getJmsProducer
argument_list|()
operator|.
name|send
argument_list|(
name|createJmsTextMessage
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
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onPublishEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|getConnection
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Properties
name|getJmsProducerSettings
parameter_list|()
block|{
return|return
name|jmsProducerSettings
return|;
block|}
specifier|public
name|void
name|setJmsProducerSettings
parameter_list|(
name|Properties
name|jmsProducerSettings
parameter_list|)
block|{
name|this
operator|.
name|jmsProducerSettings
operator|=
name|jmsProducerSettings
expr_stmt|;
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|jmsProducerSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MessageProducer
name|createJmsProducer
parameter_list|()
throws|throws
name|JMSException
block|{
name|jmsProducer
operator|=
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|jmsProducer
return|;
block|}
specifier|public
name|MessageProducer
name|createJmsProducer
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|jmsProducer
operator|=
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
return|return
name|jmsProducer
return|;
block|}
specifier|public
name|MessageProducer
name|getJmsProducer
parameter_list|()
block|{
return|return
name|jmsProducer
return|;
block|}
specifier|public
name|TextMessage
name|createJmsTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createJmsTextMessage
argument_list|(
name|getMessageSize
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|TextMessage
name|createJmsTextMessage
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|JMSException
block|{
name|jmsTextMessage
operator|=
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|buildText
argument_list|(
literal|""
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jmsTextMessage
return|;
block|}
specifier|public
name|TextMessage
name|createJmsTextMessage
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
name|jmsTextMessage
operator|=
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|buildText
argument_list|(
name|text
argument_list|,
name|getMessageSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jmsTextMessage
return|;
block|}
specifier|public
name|TextMessage
name|getJmsTextMessage
parameter_list|()
block|{
return|return
name|jmsTextMessage
return|;
block|}
specifier|protected
name|String
name|buildText
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
operator|-
name|text
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
return|return
name|text
operator|+
operator|new
name|String
argument_list|(
name|data
argument_list|)
return|;
block|}
specifier|public
name|int
name|getMessageSize
parameter_list|()
block|{
return|return
name|messageSize
return|;
block|}
specifier|public
name|void
name|setMessageSize
parameter_list|(
name|int
name|messageSize
parameter_list|)
block|{
name|this
operator|.
name|messageSize
operator|=
name|messageSize
expr_stmt|;
block|}
specifier|public
name|long
name|getSendCount
parameter_list|()
block|{
return|return
name|sendCount
return|;
block|}
specifier|public
name|void
name|setSendCount
parameter_list|(
name|long
name|sendCount
parameter_list|)
block|{
name|this
operator|.
name|sendCount
operator|=
name|sendCount
expr_stmt|;
block|}
specifier|public
name|long
name|getSendDuration
parameter_list|()
block|{
return|return
name|sendDuration
return|;
block|}
specifier|public
name|void
name|setSendDuration
parameter_list|(
name|long
name|sendDuration
parameter_list|)
block|{
name|this
operator|.
name|sendDuration
operator|=
name|sendDuration
expr_stmt|;
block|}
specifier|public
name|String
name|getSendType
parameter_list|()
block|{
return|return
name|sendType
return|;
block|}
specifier|public
name|void
name|setSendType
parameter_list|(
name|String
name|sendType
parameter_list|)
block|{
name|this
operator|.
name|sendType
operator|=
name|sendType
expr_stmt|;
block|}
specifier|public
name|Properties
name|getSettings
parameter_list|()
block|{
name|Properties
name|allSettings
init|=
operator|new
name|Properties
argument_list|(
name|jmsProducerSettings
argument_list|)
decl_stmt|;
name|allSettings
operator|.
name|putAll
argument_list|(
name|super
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|allSettings
return|;
block|}
specifier|public
name|void
name|setSettings
parameter_list|(
name|Properties
name|settings
parameter_list|)
block|{
name|super
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|jmsProducerSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|PREFIX_CONFIG_PRODUCER
argument_list|)
condition|)
block|{
name|jmsProducerSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
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
name|JMSException
block|{
name|Properties
name|samplerSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Properties
name|producerSettings
init|=
operator|new
name|Properties
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Get property define options only
name|int
name|index
init|=
name|args
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|args
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|args
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"sampler."
argument_list|)
condition|)
block|{
name|samplerSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|producerSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|JmsProducerClient
name|client
init|=
operator|new
name|JmsProducerClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|setSettings
argument_list|(
name|producerSettings
argument_list|)
expr_stmt|;
name|PerfMeasurementTool
name|sampler
init|=
operator|new
name|PerfMeasurementTool
argument_list|()
decl_stmt|;
name|sampler
operator|.
name|setSamplerSettings
argument_list|(
name|samplerSettings
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|registerClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|startSampler
argument_list|()
expr_stmt|;
name|client
operator|.
name|setPerfEventListener
argument_list|(
name|sampler
argument_list|)
expr_stmt|;
comment|// This will reuse only a single message every send, which will improve performance
name|client
operator|.
name|createJmsTextMessage
argument_list|()
expr_stmt|;
name|client
operator|.
name|sendMessages
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit


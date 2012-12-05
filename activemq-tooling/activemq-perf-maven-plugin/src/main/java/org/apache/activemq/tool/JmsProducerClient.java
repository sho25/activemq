begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

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
name|DeliveryMode
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|properties
operator|.
name|JmsClientProperties
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
name|tool
operator|.
name|properties
operator|.
name|JmsProducerProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|JmsProducerClient
extends|extends
name|AbstractJmsMeasurableClient
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JmsProducerClient
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|JmsProducerProperties
name|client
decl_stmt|;
specifier|protected
name|MessageProducer
name|jmsProducer
decl_stmt|;
specifier|protected
name|TextMessage
name|jmsTextMessage
decl_stmt|;
specifier|public
name|JmsProducerClient
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|JmsProducerProperties
argument_list|()
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JmsProducerClient
parameter_list|(
name|JmsProducerProperties
name|clientProps
parameter_list|,
name|ConnectionFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|clientProps
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// Send a specific number of messages
if|if
condition|(
name|client
operator|.
name|getSendType
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsProducerProperties
operator|.
name|COUNT_BASED_SENDING
argument_list|)
condition|)
block|{
name|sendCountBasedMessages
argument_list|(
name|client
operator|.
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
name|client
operator|.
name|getSendDuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|int
name|destCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|destCount
operator|=
name|destCount
expr_stmt|;
name|sendMessages
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|int
name|destIndex
parameter_list|,
name|int
name|destCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|destIndex
operator|=
name|destIndex
expr_stmt|;
name|sendMessages
argument_list|(
name|destCount
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
name|Destination
index|[]
name|dest
init|=
name|createDestination
argument_list|(
name|destIndex
argument_list|,
name|destCount
argument_list|)
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
name|client
operator|.
name|getMsgFileName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting to publish "
operator|+
name|messageCount
operator|+
literal|" messages from file "
operator|+
name|client
operator|.
name|getMsgFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting to publish "
operator|+
name|messageCount
operator|+
literal|" messages of size "
operator|+
name|client
operator|.
name|getMessageSize
argument_list|()
operator|+
literal|" byte(s)."
argument_list|)
expr_stmt|;
block|}
comment|// Send one type of message only, avoiding the creation of different messages on sending
if|if
condition|(
operator|!
name|client
operator|.
name|isCreateNewMsg
argument_list|()
condition|)
block|{
comment|// Create only a single message
name|createJmsTextMessage
argument_list|()
expr_stmt|;
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
argument_list|()
expr_stmt|;
block|}
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
name|Destination
index|[]
name|dest
init|=
name|createDestination
argument_list|(
name|destIndex
argument_list|,
name|destCount
argument_list|)
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
name|client
operator|.
name|getMsgFileName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting to publish messages from file "
operator|+
name|client
operator|.
name|getMsgFileName
argument_list|()
operator|+
literal|" for "
operator|+
name|duration
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting to publish "
operator|+
name|client
operator|.
name|getMessageSize
argument_list|()
operator|+
literal|" byte(s) messages for "
operator|+
name|duration
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
comment|// Send one type of message only, avoiding the creation of different messages on sending
if|if
condition|(
operator|!
name|client
operator|.
name|isCreateNewMsg
argument_list|()
condition|)
block|{
comment|// Create only a single message
name|createJmsTextMessage
argument_list|()
expr_stmt|;
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
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
name|sleep
argument_list|()
expr_stmt|;
name|commitTxIfNecessary
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|client
operator|.
name|getDeliveryMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsProducerProperties
operator|.
name|DELIVERY_MODE_PERSISTENT
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating producer to possible multiple destinations with persistent delivery."
argument_list|)
expr_stmt|;
name|jmsProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|client
operator|.
name|getDeliveryMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsProducerProperties
operator|.
name|DELIVERY_MODE_NON_PERSISTENT
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating producer to possible multiple destinations with non-persistent delivery."
argument_list|)
expr_stmt|;
name|jmsProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown deliveryMode value. Defaulting to non-persistent."
argument_list|)
expr_stmt|;
name|jmsProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|client
operator|.
name|getDeliveryMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsProducerProperties
operator|.
name|DELIVERY_MODE_PERSISTENT
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating producer to: "
operator|+
name|dest
operator|.
name|toString
argument_list|()
operator|+
literal|" with persistent delivery."
argument_list|)
expr_stmt|;
name|jmsProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|client
operator|.
name|getDeliveryMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsProducerProperties
operator|.
name|DELIVERY_MODE_NON_PERSISTENT
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating  producer to: "
operator|+
name|dest
operator|.
name|toString
argument_list|()
operator|+
literal|" with non-persistent delivery."
argument_list|)
expr_stmt|;
name|jmsProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown deliveryMode value. Defaulting to non-persistent."
argument_list|)
expr_stmt|;
name|jmsProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|client
operator|.
name|getMsgFileName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|loadJmsMessage
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|createJmsTextMessage
argument_list|(
name|client
operator|.
name|getMessageSize
argument_list|()
argument_list|)
return|;
block|}
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
comment|// support for adding message headers
name|Set
argument_list|<
name|String
argument_list|>
name|headerKeys
init|=
name|this
operator|.
name|client
operator|.
name|getHeaderKeys
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|headerKeys
control|)
block|{
name|jmsTextMessage
operator|.
name|setObjectProperty
argument_list|(
name|key
argument_list|,
name|this
operator|.
name|client
operator|.
name|getHeaderValue
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|client
operator|.
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
specifier|public
name|JmsClientProperties
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
specifier|public
name|void
name|setClient
parameter_list|(
name|JmsClientProperties
name|clientProps
parameter_list|)
block|{
name|client
operator|=
operator|(
name|JmsProducerProperties
operator|)
name|clientProps
expr_stmt|;
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
specifier|protected
name|void
name|sleep
parameter_list|()
block|{
if|if
condition|(
name|client
operator|.
name|getSendDelay
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sleeping for "
operator|+
name|client
operator|.
name|getSendDelay
argument_list|()
operator|+
literal|" milliseconds"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|client
operator|.
name|getSendDelay
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * loads the message to be sent from the specified TextFile      */
specifier|protected
name|TextMessage
name|loadJmsMessage
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
comment|// couple of sanity checks upfront
if|if
condition|(
name|client
operator|.
name|getMsgFileName
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Invalid filename specified."
argument_list|)
throw|;
block|}
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|client
operator|.
name|getMsgFileName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot load from "
operator|+
name|client
operator|.
name|getMsgFileName
argument_list|()
operator|+
literal|" as it is a directory not a text file."
argument_list|)
throw|;
block|}
comment|// try to load file
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuffer
name|payload
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|tmp
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|tmp
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|payload
operator|.
name|append
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
name|jmsTextMessage
operator|=
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|payload
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|jmsTextMessage
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|iox
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
name|iox
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

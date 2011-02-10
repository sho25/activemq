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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|Message
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|broker
operator|.
name|BrokerService
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
name|JMSMemtest
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
name|JMSMemtest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MESSAGECOUNT
init|=
literal|5000
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|durable
decl_stmt|;
specifier|protected
name|long
name|messageCount
decl_stmt|;
comment|//  how large the message in kb before we close/start the producer/consumer with a new connection.  -1 means no connectionCheckpointSize
specifier|protected
name|int
name|connectionCheckpointSize
decl_stmt|;
specifier|protected
name|long
name|connectionInterval
decl_stmt|;
specifier|protected
name|int
name|consumerCount
decl_stmt|;
specifier|protected
name|int
name|producerCount
decl_stmt|;
specifier|protected
name|int
name|checkpointInterval
decl_stmt|;
specifier|protected
name|int
name|prefetchSize
decl_stmt|;
comment|//set 10 kb of payload as default
specifier|protected
name|int
name|messageSize
decl_stmt|;
specifier|protected
name|String
name|reportDirectory
decl_stmt|;
specifier|protected
name|String
name|reportName
decl_stmt|;
specifier|protected
name|String
name|url
init|=
literal|""
decl_stmt|;
specifier|protected
name|MemProducer
index|[]
name|producers
decl_stmt|;
specifier|protected
name|MemConsumer
index|[]
name|consumers
decl_stmt|;
specifier|protected
name|String
name|destinationName
decl_stmt|;
specifier|protected
name|boolean
name|allMessagesConsumed
init|=
literal|true
decl_stmt|;
specifier|protected
name|MemConsumer
name|allMessagesList
init|=
operator|new
name|MemConsumer
argument_list|()
decl_stmt|;
specifier|protected
name|Message
name|payload
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|createConnectionPerClient
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|transacted
decl_stmt|;
specifier|protected
name|boolean
name|useEmbeddedBroker
init|=
literal|true
decl_stmt|;
specifier|protected
name|MemoryMonitoringTool
name|memoryMonitoringTool
decl_stmt|;
specifier|public
name|JMSMemtest
parameter_list|(
name|Properties
name|settings
parameter_list|)
block|{
name|url
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
literal|"url"
argument_list|)
expr_stmt|;
name|topic
operator|=
operator|new
name|Boolean
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"topic"
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|durable
operator|=
operator|new
name|Boolean
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"durable"
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|connectionCheckpointSize
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"connectionCheckpointSize"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|producerCount
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"producerCount"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|consumerCount
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"consumerCount"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|messageCount
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"messageCount"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|messageSize
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"messageSize"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|prefetchSize
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"prefetchSize"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|checkpointInterval
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"checkpointInterval"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
operator|*
literal|1000
expr_stmt|;
name|producerCount
operator|=
operator|new
name|Integer
argument_list|(
name|settings
operator|.
name|getProperty
argument_list|(
literal|"producerCount"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|reportName
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
literal|"reportName"
argument_list|)
expr_stmt|;
name|destinationName
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
literal|"destinationName"
argument_list|)
expr_stmt|;
name|reportDirectory
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
literal|"reportDirectory"
argument_list|)
expr_stmt|;
name|connectionInterval
operator|=
name|connectionCheckpointSize
operator|*
literal|1024
expr_stmt|;
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
block|{
name|Properties
name|sysSettings
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
name|sysSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|JMSMemtest
name|memtest
init|=
operator|new
name|JMSMemtest
argument_list|(
name|sysSettings
argument_list|)
decl_stmt|;
try|try
block|{
name|memtest
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
specifier|protected
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Monitor"
argument_list|)
expr_stmt|;
name|memoryMonitoringTool
operator|=
operator|new
name|MemoryMonitoringTool
argument_list|()
expr_stmt|;
name|memoryMonitoringTool
operator|.
name|setTestSettings
argument_list|(
name|getSysTestSettings
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|monitorThread
init|=
name|memoryMonitoringTool
operator|.
name|startMonitor
argument_list|()
decl_stmt|;
if|if
condition|(
name|messageCount
operator|==
literal|0
condition|)
block|{
name|messageCount
operator|=
name|DEFAULT_MESSAGECOUNT
expr_stmt|;
block|}
if|if
condition|(
name|useEmbeddedBroker
condition|)
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
block|}
name|connectionFactory
operator|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefetchSize
operator|>
literal|0
condition|)
block|{
name|connectionFactory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setTopicPrefetch
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
block|}
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
name|createPayload
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|publishAndConsume
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing resources"
argument_list|)
expr_stmt|;
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
name|monitorThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|boolean
name|resetConnection
parameter_list|(
name|int
name|counter
parameter_list|)
block|{
if|if
condition|(
name|connectionInterval
operator|>
literal|0
condition|)
block|{
name|long
name|totalMsgSizeConsumed
init|=
name|counter
operator|*
literal|1024
decl_stmt|;
if|if
condition|(
name|connectionInterval
operator|<
name|totalMsgSizeConsumed
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|protected
name|void
name|publishAndConsume
parameter_list|()
throws|throws
name|Exception
block|{
name|createConsumers
argument_list|()
expr_stmt|;
name|createProducers
argument_list|()
expr_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|boolean
name|resetCon
init|=
literal|false
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Start sending messages "
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
if|if
condition|(
name|resetCon
condition|)
block|{
name|closeConsumers
argument_list|()
expr_stmt|;
name|closeProducers
argument_list|()
expr_stmt|;
name|createConsumers
argument_list|()
expr_stmt|;
name|createProducers
argument_list|()
expr_stmt|;
name|resetCon
operator|=
literal|false
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|producers
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|producers
index|[
name|k
index|]
operator|.
name|sendMessage
argument_list|(
name|payload
argument_list|,
literal|"counter"
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|resetConnection
argument_list|(
name|counter
argument_list|)
condition|)
block|{
name|resetCon
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
specifier|protected
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|memoryMonitoringTool
operator|.
name|stopMonitor
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createPayload
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|byte
index|[]
name|array
init|=
operator|new
name|byte
index|[
name|messageSize
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|array
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|BytesMessage
name|bystePayload
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|bystePayload
operator|.
name|writeBytes
argument_list|(
name|array
argument_list|)
expr_stmt|;
name|payload
operator|=
operator|(
name|Message
operator|)
name|bystePayload
expr_stmt|;
block|}
specifier|protected
name|void
name|createProducers
parameter_list|()
throws|throws
name|JMSException
block|{
name|producers
operator|=
operator|new
name|MemProducer
index|[
name|producerCount
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
name|producerCount
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|=
operator|new
name|MemProducer
argument_list|(
name|connectionFactory
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|durable
condition|)
block|{
name|producers
index|[
name|i
index|]
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
name|producers
index|[
name|i
index|]
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
name|producers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|createConsumers
parameter_list|()
throws|throws
name|JMSException
block|{
name|consumers
operator|=
operator|new
name|MemConsumer
index|[
name|consumerCount
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|=
operator|new
name|MemConsumer
argument_list|(
name|connectionFactory
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|setParent
argument_list|(
name|allMessagesList
argument_list|)
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|closeProducers
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
name|producerCount
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|.
name|shutDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|closeConsumers
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|.
name|shutDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|url
operator|==
literal|null
operator|||
name|url
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|url
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
condition|)
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
return|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|addConnector
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Properties
name|getSysTestSettings
parameter_list|()
block|{
name|Properties
name|settings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"domain"
argument_list|,
name|topic
condition|?
literal|"topic"
else|:
literal|"queue"
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"durable"
argument_list|,
name|durable
condition|?
literal|"durable"
else|:
literal|"non-durable"
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"connection_checkpoint_size_kb"
argument_list|,
operator|new
name|Integer
argument_list|(
name|connectionCheckpointSize
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"producer_count"
argument_list|,
operator|new
name|Integer
argument_list|(
name|producerCount
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"consumer_count"
argument_list|,
operator|new
name|Integer
argument_list|(
name|consumerCount
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"message_count"
argument_list|,
operator|new
name|Long
argument_list|(
name|messageCount
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"message_size"
argument_list|,
operator|new
name|Integer
argument_list|(
name|messageSize
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"prefetchSize"
argument_list|,
operator|new
name|Integer
argument_list|(
name|prefetchSize
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"checkpoint_interval"
argument_list|,
operator|new
name|Integer
argument_list|(
name|checkpointInterval
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"destination_name"
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"report_name"
argument_list|,
name|reportName
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"report_directory"
argument_list|,
name|reportDirectory
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setProperty
argument_list|(
literal|"connection_checkpoint_size"
argument_list|,
operator|new
name|Integer
argument_list|(
name|connectionCheckpointSize
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|settings
return|;
block|}
block|}
end_class

end_unit


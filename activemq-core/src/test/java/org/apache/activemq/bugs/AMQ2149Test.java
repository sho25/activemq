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
name|bugs
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|DeliveryMode
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
name|MessageConsumer
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
name|MessageProducer
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
name|Topic
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BrokerPlugin
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
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
name|region
operator|.
name|DestinationStatistics
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
name|region
operator|.
name|RegionBroker
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|util
operator|.
name|LoggingBrokerPlugin
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
name|command
operator|.
name|ActiveMQDestination
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
name|store
operator|.
name|amq
operator|.
name|AMQPersistenceAdapterFactory
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
name|usage
operator|.
name|MemoryUsage
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
name|usage
operator|.
name|SystemUsage
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

begin_interface
interface|interface
name|Configurer
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

begin_class
specifier|public
class|class
name|AMQ2149Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AMQ2149Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|BROKER_STOP_PERIOD
init|=
literal|20
operator|*
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_CONNECTOR
init|=
literal|"tcp://localhost:61617"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"failover:("
operator|+
name|BROKER_CONNECTOR
operator|+
literal|")?maxReconnectDelay=1000&useExponentialBackOff=false"
decl_stmt|;
specifier|private
specifier|final
name|String
name|SEQ_NUM_PROPERTY
init|=
literal|"seqNum"
decl_stmt|;
specifier|final
name|int
name|MESSAGE_LENGTH_BYTES
init|=
literal|75
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|MAX_TO_SEND
init|=
literal|1500
decl_stmt|;
specifier|final
name|long
name|SLEEP_BETWEEN_SEND_MS
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|NUM_SENDERS_AND_RECEIVERS
init|=
literal|10
decl_stmt|;
specifier|final
name|Object
name|brokerLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|File
name|dataDirFile
decl_stmt|;
specifier|final
name|LoggingBrokerPlugin
index|[]
name|plugins
init|=
operator|new
name|LoggingBrokerPlugin
index|[]
block|{
operator|new
name|LoggingBrokerPlugin
argument_list|()
block|}
decl_stmt|;
specifier|public
name|void
name|createBroker
parameter_list|(
name|Configurer
name|configurer
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|AMQPersistenceAdapterFactory
name|persistenceFactory
init|=
operator|new
name|AMQPersistenceAdapterFactory
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setDataDirectory
argument_list|(
name|dataDirFile
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceFactory
argument_list|(
name|persistenceFactory
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|BROKER_CONNECTOR
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDataDirectoryFile
argument_list|(
name|dataDirFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|configurer
operator|!=
literal|null
condition|)
block|{
name|configurer
operator|.
name|configure
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dataDirFile
operator|=
operator|new
name|File
argument_list|(
literal|"target/"
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|brokerLock
init|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|buildLongString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|(
name|MESSAGE_LENGTH_BYTES
argument_list|)
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
name|MESSAGE_LENGTH_BYTES
condition|;
operator|++
name|i
control|)
block|{
name|stringBuilder
operator|.
name|append
argument_list|(
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
class|class
name|Receiver
implements|implements
name|MessageListener
block|{
specifier|private
specifier|final
name|javax
operator|.
name|jms
operator|.
name|Destination
name|dest
decl_stmt|;
specifier|private
specifier|final
name|Connection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|MessageConsumer
name|messageConsumer
decl_stmt|;
specifier|private
specifier|volatile
name|long
name|nextExpectedSeqNum
init|=
literal|0
decl_stmt|;
specifier|private
name|String
name|lastId
init|=
literal|null
decl_stmt|;
specifier|public
name|Receiver
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|BROKER_URL
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|dest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
if|if
condition|(
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|dest
argument_list|)
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|messageConsumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|dest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messageConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
name|messageConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getNextExpectedSeqNo
parameter_list|()
block|{
return|return
name|nextExpectedSeqNum
return|;
block|}
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
specifier|final
name|long
name|seqNum
init|=
name|message
operator|.
name|getLongProperty
argument_list|(
name|SEQ_NUM_PROPERTY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|seqNum
operator|%
literal|500
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|dest
operator|+
literal|" received "
operator|+
name|seqNum
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seqNum
operator|!=
name|nextExpectedSeqNum
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|dest
operator|+
literal|" received "
operator|+
name|seqNum
operator|+
literal|" in msg: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" expected "
operator|+
name|nextExpectedSeqNum
operator|+
literal|", lastId: "
operator|+
name|lastId
operator|+
literal|", message:"
operator|+
name|message
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|dest
operator|+
literal|" received "
operator|+
name|seqNum
operator|+
literal|" expected "
operator|+
name|nextExpectedSeqNum
argument_list|)
expr_stmt|;
block|}
operator|++
name|nextExpectedSeqNum
expr_stmt|;
name|lastId
operator|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|dest
operator|+
literal|" onMessage error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|Sender
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|javax
operator|.
name|jms
operator|.
name|Destination
name|dest
decl_stmt|;
specifier|private
specifier|final
name|Connection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|MessageProducer
name|messageProducer
decl_stmt|;
specifier|private
specifier|volatile
name|long
name|nextSequenceNumber
init|=
literal|0
decl_stmt|;
specifier|public
name|Sender
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|BROKER_URL
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|messageProducer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|String
name|longString
init|=
name|buildLongString
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextSequenceNumber
operator|<
name|MAX_TO_SEND
condition|)
block|{
try|try
block|{
specifier|final
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|longString
argument_list|)
decl_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
name|SEQ_NUM_PROPERTY
argument_list|,
name|nextSequenceNumber
argument_list|)
expr_stmt|;
operator|++
name|nextSequenceNumber
expr_stmt|;
name|messageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|dest
operator|+
literal|" send error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|SLEEP_BETWEEN_SEND_MS
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_BETWEEN_SEND_MS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|dest
operator|+
literal|" sleep interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignored
parameter_list|)
block|{             }
block|}
block|}
comment|// no need to run this unless there are some issues with the others
specifier|public
name|void
name|vanilaVerify_testOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|verifyOrderedMessageReceipt
argument_list|()
expr_stmt|;
name|verifyStats
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOrderWithMemeUsageLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|SystemUsage
name|usage
init|=
operator|new
name|SystemUsage
argument_list|()
decl_stmt|;
name|MemoryUsage
name|memoryUsage
init|=
operator|new
name|MemoryUsage
argument_list|()
decl_stmt|;
name|memoryUsage
operator|.
name|setLimit
argument_list|(
name|MESSAGE_LENGTH_BYTES
operator|*
literal|10
operator|*
name|NUM_SENDERS_AND_RECEIVERS
argument_list|)
expr_stmt|;
name|usage
operator|.
name|setMemoryUsage
argument_list|(
name|memoryUsage
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSystemUsage
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|verifyOrderedMessageReceipt
argument_list|()
expr_stmt|;
name|verifyStats
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// no need to run this unless there are some issues with the others
specifier|public
name|void
name|noProblem_testOrderWithRestartAndVMIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|AMQPersistenceAdapterFactory
name|persistenceFactory
init|=
operator|(
name|AMQPersistenceAdapterFactory
operator|)
name|broker
operator|.
name|getPersistenceFactory
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setPersistentIndex
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|schedualRestartTask
argument_list|(
name|timer
argument_list|,
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|AMQPersistenceAdapterFactory
name|persistenceFactory
init|=
operator|(
name|AMQPersistenceAdapterFactory
operator|)
name|broker
operator|.
name|getPersistenceFactory
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setPersistentIndex
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|verifyOrderedMessageReceipt
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|verifyStats
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOrderWithRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|schedualRestartTask
argument_list|(
name|timer
argument_list|,
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{                 }
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|verifyOrderedMessageReceipt
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|verifyStats
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|x_testTopicOrderWithRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|plugins
index|[
literal|0
index|]
operator|.
name|setLogAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|plugins
index|[
literal|0
index|]
operator|.
name|setLogInternalEvents
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|schedualRestartTask
argument_list|(
name|timer
argument_list|,
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|setPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|verifyOrderedMessageReceipt
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|verifyStats
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// no need to run this unless there are issues with the other restart tests
specifier|public
name|void
name|eaiserToRepoduce_testOrderWithRestartWithForceRecover
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|AMQPersistenceAdapterFactory
name|persistenceFactory
init|=
operator|(
name|AMQPersistenceAdapterFactory
operator|)
name|broker
operator|.
name|getPersistenceFactory
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setForceRecoverReferenceStore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|schedualRestartTask
argument_list|(
name|timer
argument_list|,
operator|new
name|Configurer
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|AMQPersistenceAdapterFactory
name|persistenceFactory
init|=
operator|(
name|AMQPersistenceAdapterFactory
operator|)
name|broker
operator|.
name|getPersistenceFactory
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setForceRecoverReferenceStore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|verifyOrderedMessageReceipt
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|verifyStats
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyStats
parameter_list|(
name|boolean
name|brokerRestarts
parameter_list|)
throws|throws
name|Exception
block|{
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
for|for
control|(
name|Destination
name|dest
range|:
name|regionBroker
operator|.
name|getQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|DestinationStatistics
name|stats
init|=
name|dest
operator|.
name|getDestinationStatistics
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerRestarts
condition|)
block|{
name|assertTrue
argument_list|(
literal|"qneue/dequeue match for: "
operator|+
name|dest
operator|.
name|getName
argument_list|()
argument_list|,
name|stats
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|<=
name|stats
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"qneue/dequeue match for: "
operator|+
name|dest
operator|.
name|getName
argument_list|()
argument_list|,
name|stats
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|stats
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|schedualRestartTask
parameter_list|(
specifier|final
name|Timer
name|timer
parameter_list|,
specifier|final
name|Configurer
name|configurer
parameter_list|)
block|{
class|class
name|RestartTask
extends|extends
name|TimerTask
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
synchronized|synchronized
init|(
name|brokerLock
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"stopping broker.."
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ex on broker stop"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"restarting broker"
argument_list|)
expr_stmt|;
try|try
block|{
name|createBroker
argument_list|(
name|configurer
argument_list|)
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ex on broker restart"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// do it again
try|try
block|{
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|RestartTask
argument_list|()
argument_list|,
name|BROKER_STOP_PERIOD
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignore_alreadyCancelled
parameter_list|)
block|{                    }
block|}
block|}
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|RestartTask
argument_list|()
argument_list|,
name|BROKER_STOP_PERIOD
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyOrderedMessageReceipt
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyOrderedMessageReceipt
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyOrderedMessageReceipt
parameter_list|(
name|byte
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
name|Vector
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|Vector
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|Vector
argument_list|<
name|Receiver
argument_list|>
name|receivers
init|=
operator|new
name|Vector
argument_list|<
name|Receiver
argument_list|>
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
name|NUM_SENDERS_AND_RECEIVERS
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|javax
operator|.
name|jms
operator|.
name|Destination
name|destination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"test.dest."
operator|+
name|i
argument_list|,
name|destinationType
argument_list|)
decl_stmt|;
name|receivers
operator|.
name|add
argument_list|(
operator|new
name|Receiver
argument_list|(
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Sender
argument_list|(
name|destination
argument_list|)
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|expiry
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
operator|*
literal|60
operator|*
literal|30
decl_stmt|;
while|while
condition|(
operator|!
name|threads
operator|.
name|isEmpty
argument_list|()
operator|&&
name|exceptions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|expiry
condition|)
block|{
name|Thread
name|sendThread
init|=
name|threads
operator|.
name|firstElement
argument_list|()
decl_stmt|;
name|sendThread
operator|.
name|join
argument_list|(
literal|1000
operator|*
literal|10
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|sendThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|threads
operator|.
name|remove
argument_list|(
name|sendThread
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"senders done..."
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|receivers
operator|.
name|isEmpty
argument_list|()
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|expiry
condition|)
block|{
name|Receiver
name|receiver
init|=
name|receivers
operator|.
name|firstElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|receiver
operator|.
name|getNextExpectedSeqNo
argument_list|()
operator|>=
name|MAX_TO_SEND
operator|||
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|receivers
operator|.
name|remove
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"No timeout waiting for senders/receivers to complete"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|expiry
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exceptions"
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


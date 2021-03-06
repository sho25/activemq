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
name|ObjectMessage
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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

begin_comment
comment|/*  * Try and replicate:  * Caused by: java.io.IOException: Could not locate data file data--188  *  at org.apache.activemq.kaha.impl.async.AsyncDataManager.getDataFile(AsyncDataManager.java:302)  *  at org.apache.activemq.kaha.impl.async.AsyncDataManager.read(AsyncDataManager.java:614)  *  at org.apache.activemq.store.amq.AMQPersistenceAdapter.readCommand(AMQPersistenceAdapter.java:523)  */
end_comment

begin_class
specifier|public
class|class
name|MissingDataFileTest
extends|extends
name|TestCase
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
name|MissingDataFileTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|counter
init|=
literal|500
decl_stmt|;
specifier|private
specifier|static
name|int
name|hectorToHaloCtr
decl_stmt|;
specifier|private
specifier|static
name|int
name|xenaToHaloCtr
decl_stmt|;
specifier|private
specifier|static
name|int
name|troyToHaloCtr
decl_stmt|;
specifier|private
specifier|static
name|int
name|haloToHectorCtr
decl_stmt|;
specifier|private
specifier|static
name|int
name|haloToXenaCtr
decl_stmt|;
specifier|private
specifier|static
name|int
name|haloToTroyCtr
decl_stmt|;
specifier|private
specifier|final
name|String
name|hectorToHalo
init|=
literal|"hectorToHalo"
decl_stmt|;
specifier|private
specifier|final
name|String
name|xenaToHalo
init|=
literal|"xenaToHalo"
decl_stmt|;
specifier|private
specifier|final
name|String
name|troyToHalo
init|=
literal|"troyToHalo"
decl_stmt|;
specifier|private
specifier|final
name|String
name|haloToHector
init|=
literal|"haloToHector"
decl_stmt|;
specifier|private
specifier|final
name|String
name|haloToXena
init|=
literal|"haloToXena"
decl_stmt|;
specifier|private
specifier|final
name|String
name|haloToTroy
init|=
literal|"haloToTroy"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|Connection
name|hectorConnection
decl_stmt|;
specifier|private
name|Connection
name|xenaConnection
decl_stmt|;
specifier|private
name|Connection
name|troyConnection
decl_stmt|;
specifier|private
name|Connection
name|haloConnection
decl_stmt|;
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|useTopic
init|=
literal|false
decl_stmt|;
specifier|final
name|boolean
name|useSleep
init|=
literal|true
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|500
index|]
argument_list|)
decl_stmt|;
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|Session
name|createSession
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|boolean
name|transacted
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
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
return|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Default"
argument_list|)
expr_stmt|;
name|SystemUsage
name|systemUsage
decl_stmt|;
name|systemUsage
operator|=
operator|new
name|SystemUsage
argument_list|()
expr_stmt|;
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// Just a few messags
name|broker
operator|.
name|setSystemUsage
argument_list|(
name|systemUsage
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|16
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setCleanupInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaDBPersistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker.."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|hectorConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|xenaConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|troyConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|haloConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testForNoDataFoundError
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|()
expr_stmt|;
name|hectorConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|Thread
name|hectorThread
init|=
name|buildProducer
argument_list|(
name|hectorConnection
argument_list|,
name|hectorToHalo
argument_list|,
literal|false
argument_list|,
name|useTopic
argument_list|)
decl_stmt|;
name|Receiver
name|hHectorReceiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|haloToHectorCtr
operator|++
expr_stmt|;
if|if
condition|(
name|haloToHectorCtr
operator|>=
name|counter
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|possiblySleep
argument_list|(
name|haloToHectorCtr
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|buildReceiver
argument_list|(
name|hectorConnection
argument_list|,
name|haloToHector
argument_list|,
literal|false
argument_list|,
name|hHectorReceiver
argument_list|,
name|useTopic
argument_list|)
expr_stmt|;
name|troyConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|Thread
name|troyThread
init|=
name|buildProducer
argument_list|(
name|troyConnection
argument_list|,
name|troyToHalo
argument_list|)
decl_stmt|;
name|Receiver
name|hTroyReceiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|haloToTroyCtr
operator|++
expr_stmt|;
if|if
condition|(
name|haloToTroyCtr
operator|>=
name|counter
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|possiblySleep
argument_list|(
name|haloToTroyCtr
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|buildReceiver
argument_list|(
name|hectorConnection
argument_list|,
name|haloToTroy
argument_list|,
literal|false
argument_list|,
name|hTroyReceiver
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|xenaConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|Thread
name|xenaThread
init|=
name|buildProducer
argument_list|(
name|xenaConnection
argument_list|,
name|xenaToHalo
argument_list|)
decl_stmt|;
name|Receiver
name|hXenaReceiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|haloToXenaCtr
operator|++
expr_stmt|;
if|if
condition|(
name|haloToXenaCtr
operator|>=
name|counter
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|possiblySleep
argument_list|(
name|haloToXenaCtr
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|buildReceiver
argument_list|(
name|xenaConnection
argument_list|,
name|haloToXena
argument_list|,
literal|false
argument_list|,
name|hXenaReceiver
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|haloConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
specifier|final
name|MessageSender
name|hectorSender
init|=
name|buildTransactionalProducer
argument_list|(
name|haloToHector
argument_list|,
name|haloConnection
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|MessageSender
name|troySender
init|=
name|buildTransactionalProducer
argument_list|(
name|haloToTroy
argument_list|,
name|haloConnection
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|MessageSender
name|xenaSender
init|=
name|buildTransactionalProducer
argument_list|(
name|haloToXena
argument_list|,
name|haloConnection
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Receiver
name|hectorReceiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|hectorToHaloCtr
operator|++
expr_stmt|;
name|troySender
operator|.
name|send
argument_list|(
name|payload
argument_list|)
expr_stmt|;
if|if
condition|(
name|hectorToHaloCtr
operator|>=
name|counter
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
name|possiblySleep
argument_list|(
name|hectorToHaloCtr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Receiver
name|xenaReceiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|xenaToHaloCtr
operator|++
expr_stmt|;
name|hectorSender
operator|.
name|send
argument_list|(
name|payload
argument_list|)
expr_stmt|;
if|if
condition|(
name|xenaToHaloCtr
operator|>=
name|counter
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|possiblySleep
argument_list|(
name|xenaToHaloCtr
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Receiver
name|troyReceiver
init|=
operator|new
name|Receiver
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|troyToHaloCtr
operator|++
expr_stmt|;
name|xenaSender
operator|.
name|send
argument_list|(
name|payload
argument_list|)
expr_stmt|;
if|if
condition|(
name|troyToHaloCtr
operator|>=
name|counter
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|buildReceiver
argument_list|(
name|haloConnection
argument_list|,
name|hectorToHalo
argument_list|,
literal|true
argument_list|,
name|hectorReceiver
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|buildReceiver
argument_list|(
name|haloConnection
argument_list|,
name|xenaToHalo
argument_list|,
literal|true
argument_list|,
name|xenaReceiver
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|buildReceiver
argument_list|(
name|haloConnection
argument_list|,
name|troyToHalo
argument_list|,
literal|true
argument_list|,
name|troyReceiver
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|haloConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|troyConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|troyThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|xenaConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|xenaThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|hectorConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|hectorThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForMessagesToBeDelivered
argument_list|()
expr_stmt|;
comment|// number of messages received should match messages sent
name|assertEquals
argument_list|(
name|hectorToHaloCtr
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"hectorToHalo received "
operator|+
name|hectorToHaloCtr
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xenaToHaloCtr
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"xenaToHalo received "
operator|+
name|xenaToHaloCtr
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|troyToHaloCtr
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"troyToHalo received "
operator|+
name|troyToHaloCtr
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|haloToHectorCtr
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"haloToHector received "
operator|+
name|haloToHectorCtr
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|haloToXenaCtr
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"haloToXena received "
operator|+
name|haloToXenaCtr
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|haloToTroyCtr
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"haloToTroy received "
operator|+
name|haloToTroyCtr
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|possiblySleep
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|useSleep
condition|)
block|{
if|if
condition|(
name|count
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|waitForMessagesToBeDelivered
parameter_list|()
block|{
comment|// let's give the listeners enough time to read all messages
name|long
name|maxWaitTime
init|=
name|counter
operator|*
literal|1000
decl_stmt|;
name|long
name|waitTime
init|=
name|maxWaitTime
decl_stmt|;
name|long
name|start
init|=
operator|(
name|maxWaitTime
operator|<=
literal|0
operator|)
condition|?
literal|0
else|:
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|boolean
name|hasMessages
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|hasMessages
operator|&&
name|waitTime
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|wait
argument_list|(
literal|200
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
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check if all messages have been received
name|hasMessages
operator|=
name|hectorToHaloCtr
operator|<
name|counter
operator|||
name|xenaToHaloCtr
operator|<
name|counter
operator|||
name|troyToHaloCtr
operator|<
name|counter
operator|||
name|haloToHectorCtr
operator|<
name|counter
operator|||
name|haloToXenaCtr
operator|<
name|counter
operator|||
name|haloToTroyCtr
operator|<
name|counter
expr_stmt|;
name|waitTime
operator|=
name|maxWaitTime
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|MessageSender
name|buildTransactionalProducer
parameter_list|(
name|String
name|queueName
parameter_list|,
name|Connection
name|connection
parameter_list|,
name|boolean
name|isTopic
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|MessageSender
argument_list|(
name|queueName
argument_list|,
name|connection
argument_list|,
literal|true
argument_list|,
name|isTopic
argument_list|)
return|;
block|}
specifier|public
name|Thread
name|buildProducer
parameter_list|(
name|Connection
name|connection
parameter_list|,
specifier|final
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|buildProducer
argument_list|(
name|connection
argument_list|,
name|queueName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|Thread
name|buildProducer
parameter_list|(
name|Connection
name|connection
parameter_list|,
specifier|final
name|String
name|queueName
parameter_list|,
name|boolean
name|transacted
parameter_list|,
name|boolean
name|isTopic
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|MessageSender
name|producer
init|=
operator|new
name|MessageSender
argument_list|(
name|queueName
argument_list|,
name|connection
argument_list|,
name|transacted
argument_list|,
name|isTopic
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
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
name|counter
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"on "
operator|+
name|queueName
operator|+
literal|" send"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
return|return
name|thread
return|;
block|}
specifier|public
name|void
name|buildReceiver
parameter_list|(
name|Connection
name|connection
parameter_list|,
specifier|final
name|String
name|queueName
parameter_list|,
name|boolean
name|transacted
parameter_list|,
specifier|final
name|Receiver
name|receiver
parameter_list|,
name|boolean
name|isTopic
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Session
name|session
init|=
name|transacted
condition|?
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
else|:
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
decl_stmt|;
name|MessageConsumer
name|inputMessageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|isTopic
condition|?
name|session
operator|.
name|createTopic
argument_list|(
name|queueName
argument_list|)
else|:
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
argument_list|)
decl_stmt|;
name|MessageListener
name|messageListener
init|=
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
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
name|ObjectMessage
name|objectMessage
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
name|String
name|s
init|=
operator|(
name|String
operator|)
name|objectMessage
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|receiver
operator|.
name|receive
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|.
name|getTransacted
argument_list|()
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
block|}
decl_stmt|;
name|inputMessageConsumer
operator|.
name|setMessageListener
argument_list|(
name|messageListener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


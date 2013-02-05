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
name|broker
operator|.
name|region
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|Connection
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
name|ConnectionContext
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
name|Connector
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
name|ProducerBrokerExchange
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
name|ActiveMQQueue
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
name|ActiveMQTextMessage
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
name|Command
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
name|ConnectionControl
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
name|Message
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
name|MessageId
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
name|ProducerInfo
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
name|Response
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
name|state
operator|.
name|ProducerState
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
name|MessageStore
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
name|PersistenceAdapter
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|QueueOptimizedDispatchExceptionTest
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
name|QueueOptimizedDispatchExceptionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|brokerName
init|=
literal|"testBroker"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|brokerUrl
init|=
literal|"vm://"
operator|+
name|brokerName
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|count
init|=
literal|50
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|mesageIdRoot
init|=
literal|"11111:22222:"
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue-"
operator|+
name|QueueOptimizedDispatchExceptionTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|messageBytesSize
init|=
literal|256
decl_stmt|;
specifier|private
specifier|final
name|String
name|text
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
name|messageBytesSize
index|]
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup and start the broker
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|brokerUrl
argument_list|)
expr_stmt|;
comment|// Start the broker
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
specifier|private
class|class
name|MockMemoryUsage
extends|extends
name|MemoryUsage
block|{
specifier|private
name|boolean
name|full
init|=
literal|true
decl_stmt|;
specifier|public
name|void
name|setFull
parameter_list|(
name|boolean
name|full
parameter_list|)
block|{
name|this
operator|.
name|full
operator|=
name|full
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|full
return|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|TestOptimizedDispatchCME
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PersistenceAdapter
name|persistenceAdapter
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
specifier|final
name|MessageStore
name|queueMessageStore
init|=
name|persistenceAdapter
operator|.
name|createQueueMessageStore
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|ConnectionContext
name|contextNotInTx
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|contextNotInTx
operator|.
name|setConnection
argument_list|(
operator|new
name|Connection
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|updateClient
parameter_list|(
name|ConnectionControl
name|control
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|serviceExceptionAsync
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|serviceException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|Response
name|service
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSlow
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNetworkConnection
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isManageable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFaultTolerantConnection
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isBlocked
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConnectionStatistics
name|getStatistics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDispatchQueueSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Connector
name|getConnector
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getConnectionId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispatchSync
parameter_list|(
name|Command
name|message
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|dispatchAsync
parameter_list|(
name|Command
name|command
parameter_list|)
block|{             }
block|}
argument_list|)
expr_stmt|;
specifier|final
name|DestinationStatistics
name|destinationStatistics
init|=
operator|new
name|DestinationStatistics
argument_list|()
decl_stmt|;
specifier|final
name|Queue
name|queue
init|=
operator|new
name|Queue
argument_list|(
name|broker
argument_list|,
name|destination
argument_list|,
name|queueMessageStore
argument_list|,
name|destinationStatistics
argument_list|,
name|broker
operator|.
name|getTaskRunnerFactory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|MockMemoryUsage
name|usage
init|=
operator|new
name|MockMemoryUsage
argument_list|()
decl_stmt|;
name|queue
operator|.
name|setOptimizedDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|queue
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|queue
operator|.
name|start
argument_list|()
expr_stmt|;
name|queue
operator|.
name|memoryUsage
operator|=
name|usage
expr_stmt|;
name|ProducerBrokerExchange
name|producerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
operator|new
name|ProducerInfo
argument_list|()
decl_stmt|;
name|ProducerState
name|producerState
init|=
operator|new
name|ProducerState
argument_list|(
name|producerInfo
argument_list|)
decl_stmt|;
name|producerExchange
operator|.
name|setProducerState
argument_list|(
name|producerState
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setConnectionContext
argument_list|(
name|contextNotInTx
argument_list|)
expr_stmt|;
comment|// populate the queue store, exceed memory limit so that cache is disabled
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|getMessage
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|queue
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|usage
operator|.
name|setFull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|queue
operator|.
name|wakeup
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
literal|"Queue threw an unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not throw an exception."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Message
name|getMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|mesageIdRoot
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Msg:"
operator|+
name|i
operator|+
literal|" "
operator|+
name|text
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

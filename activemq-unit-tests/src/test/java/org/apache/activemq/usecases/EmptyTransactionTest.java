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
name|usecases
package|;
end_package

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
name|store
operator|.
name|kahadb
operator|.
name|MessageDatabase
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
name|DefaultTestAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|experimental
operator|.
name|theories
operator|.
name|Theories
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
name|TextMessage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
specifier|public
class|class
name|EmptyTransactionTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|int
name|CHECKPOINT_INTERVAL
init|=
literal|500
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|public
name|void
name|testEmptyTransactionsCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicBoolean
name|hadRecovery
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|DefaultTestAppender
name|appender
init|=
operator|new
name|DefaultTestAppender
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doAppend
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getMessage
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Recovering from the journal @"
argument_list|)
condition|)
block|{
name|hadRecovery
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|MessageDatabase
operator|.
name|class
argument_list|)
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
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
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"QueueName"
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|session
argument_list|,
name|producer
argument_list|)
expr_stmt|;
comment|// wait checkpoint
comment|// When we create a new consumer a KahaProducerAuditCommand written to the journal files changing the lastUpdate pointer
name|Thread
operator|.
name|sleep
argument_list|(
name|CHECKPOINT_INTERVAL
operator|*
literal|2
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
name|session
argument_list|,
name|producer
argument_list|)
expr_stmt|;
block|}
name|restart
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|hadRecovery
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
specifier|final
name|Session
name|session
parameter_list|,
specifier|final
name|MessageProducer
name|producer
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|m
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hi"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|restart
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
name|start
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|start
parameter_list|(
specifier|final
name|boolean
name|deleteMessages
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
name|KahaDBPersistenceAdapter
name|kahaDB
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaDB
operator|.
name|setCheckpointInterval
argument_list|(
name|CHECKPOINT_INTERVAL
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaDB
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
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteMessages
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit


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
name|MessageConsumer
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|ActiveMQConnection
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
name|transport
operator|.
name|failover
operator|.
name|FailoverTransport
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|RedeliveryRestartTest
extends|extends
name|BrokerRestartTestSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RedeliveryRestartTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setMaxTestTime
argument_list|(
literal|2
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|configureBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setRewriteOnRedelivery
argument_list|(
literal|true
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
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testValidateRedeliveryFlagAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|")?jms.transactedIndividualAck=true"
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestination
argument_list|(
literal|10
argument_list|,
name|queueName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
literal|null
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"not redelivered? got: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got the message"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first delivery"
argument_list|,
literal|1
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not a redelivery"
argument_list|,
literal|false
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|restartBroker
argument_list|()
expr_stmt|;
comment|// make failover aware of the restarted auto assigned port
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|FailoverTransport
operator|.
name|class
argument_list|)
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
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
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"redelivered? got: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got the message again"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivery count survives restart"
argument_list|,
literal|2
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"re delivery flag"
argument_list|,
literal|true
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// consume the rest that were not redeliveries
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
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"not redelivered? got: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got the message"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first delivery"
argument_list|,
literal|1
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not a redelivery"
argument_list|,
literal|false
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testValidateRedeliveryFlagAfterRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|"?jms.transactedIndividualAck=true"
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestination
argument_list|(
literal|1
argument_list|,
name|queueName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|20000
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got the message"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first delivery"
argument_list|,
literal|1
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not a redelivery"
argument_list|,
literal|false
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|stopBrokerWithStoreFailure
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createRestartedBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|"?jms.transactedIndividualAck=true"
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got the message again"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivery count survives restart"
argument_list|,
literal|2
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"re delivery flag"
argument_list|,
literal|true
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|stopBrokerWithStoreFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
comment|// have the broker stop with an IOException on next checkpoint so it has a pending local transaction to recover
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|populateDestination
parameter_list|(
specifier|final
name|int
name|nbMessages
parameter_list|,
specifier|final
name|String
name|destinationName
parameter_list|,
name|javax
operator|.
name|jms
operator|.
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session
init|=
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|nbMessages
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"<hello id='"
operator|+
name|i
operator|+
literal|"'/>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|RedeliveryRestartTest
operator|.
name|class
argument_list|)
return|;
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


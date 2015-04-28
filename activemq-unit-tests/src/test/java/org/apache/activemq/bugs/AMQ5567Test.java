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
name|concurrent
operator|.
name|TimeUnit
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
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|broker
operator|.
name|BrokerRestartTestSupport
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
name|StubConnection
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
name|jmx
operator|.
name|QueueViewMBean
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
name|ConnectionInfo
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
name|ConsumerInfo
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
name|MessageAck
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
name|SessionInfo
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
name|XATransactionId
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
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|leveldb
operator|.
name|LevelDBPersistenceAdapter
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
name|IOHelper
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
name|AMQ5567Test
extends|extends
name|BrokerRestartTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ5567Test
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
decl_stmt|;
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
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|PolicyEntry
name|getDefaultPolicy
parameter_list|()
block|{
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setMemoryLimit
argument_list|(
literal|60
operator|*
literal|1024
argument_list|)
expr_stmt|;
return|return
name|policy
return|;
block|}
specifier|public
name|void
name|initCombosForTestPreparedTransactionNotDispatched
parameter_list|()
throws|throws
name|Exception
block|{
name|PersistenceAdapter
index|[]
name|persistenceAdapters
init|=
operator|new
name|PersistenceAdapter
index|[]
block|{
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
block|,
operator|new
name|LevelDBPersistenceAdapter
argument_list|()
block|,
operator|new
name|JDBCPersistenceAdapter
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|adapter
range|:
name|persistenceAdapters
control|)
block|{
name|adapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addCombinationValues
argument_list|(
literal|"persistenceAdapter"
argument_list|,
name|persistenceAdapters
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPreparedTransactionNotDispatched
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
decl_stmt|;
name|StubConnection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
name|createProducerInfo
argument_list|(
name|sessionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
name|XATransactionId
name|txid
init|=
name|createXATransaction
argument_list|(
name|sessionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createBeginTransaction
argument_list|(
name|connectionInfo
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createPrepareTransaction
argument_list|(
name|connectionInfo
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
comment|// send another non tx, will poke dispatch
name|message
operator|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Since prepared but not committed.. only one should get delivered
name|StubConnection
name|connectionC
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfoC
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfoC
init|=
name|createSessionInfo
argument_list|(
name|connectionInfoC
argument_list|)
decl_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfoC
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|connectionC
operator|.
name|send
argument_list|(
name|connectionInfoC
argument_list|)
expr_stmt|;
name|connectionC
operator|.
name|send
argument_list|(
name|sessionInfoC
argument_list|)
expr_stmt|;
name|connectionC
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
name|Message
name|m
init|=
name|receiveMessage
argument_list|(
name|connectionC
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got non tx message"
argument_list|,
name|m
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
comment|// cannot get the prepared message till commit
name|assertNull
argument_list|(
name|receiveMessage
argument_list|(
name|connectionC
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMessagesLeft
argument_list|(
name|connectionC
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"commit: "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|connection
operator|.
name|request
argument_list|(
name|createCommitTransaction2Phase
argument_list|(
name|connectionInfo
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|receiveMessage
argument_list|(
name|connectionC
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got non null message"
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestCursorStoreSync
parameter_list|()
throws|throws
name|Exception
block|{
name|PersistenceAdapter
index|[]
name|persistenceAdapters
init|=
operator|new
name|PersistenceAdapter
index|[]
block|{
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
block|,
operator|new
name|LevelDBPersistenceAdapter
argument_list|()
block|,
operator|new
name|JDBCPersistenceAdapter
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|PersistenceAdapter
name|adapter
range|:
name|persistenceAdapters
control|)
block|{
name|adapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addCombinationValues
argument_list|(
literal|"persistenceAdapter"
argument_list|,
name|persistenceAdapters
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCursorStoreSync
parameter_list|()
throws|throws
name|Exception
block|{
name|StubConnection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
name|createProducerInfo
argument_list|(
name|sessionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
name|XATransactionId
name|txid
init|=
name|createXATransaction
argument_list|(
name|sessionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createBeginTransaction
argument_list|(
name|connectionInfo
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|connection
operator|.
name|request
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|request
argument_list|(
name|createPrepareTransaction
argument_list|(
name|connectionInfo
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueueViewMBean
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"cache is enabled"
argument_list|,
name|proxy
operator|.
name|isCacheEnabled
argument_list|()
argument_list|)
expr_stmt|;
comment|// send another non tx, will fill cursor
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|10
operator|*
literal|1024
index|]
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
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|connection
operator|.
name|request
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"cache is disabled"
argument_list|,
operator|!
name|proxy
operator|.
name|isCacheEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|StubConnection
name|connectionC
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfoC
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfoC
init|=
name|createSessionInfo
argument_list|(
name|connectionInfoC
argument_list|)
decl_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfoC
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|connectionC
operator|.
name|send
argument_list|(
name|connectionInfoC
argument_list|)
expr_stmt|;
name|connectionC
operator|.
name|send
argument_list|(
name|sessionInfoC
argument_list|)
expr_stmt|;
name|connectionC
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
name|Message
name|m
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|m
operator|=
name|receiveMessage
argument_list|(
name|connectionC
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got non tx message"
argument_list|,
name|m
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|connectionC
operator|.
name|request
argument_list|(
name|createAck
argument_list|(
name|consumerInfo
argument_list|,
name|m
argument_list|,
literal|1
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"commit: "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|connection
operator|.
name|request
argument_list|(
name|createCommitTransaction2Phase
argument_list|(
name|connectionInfo
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
comment|// consume the rest including the 2pc send in TX
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|m
operator|=
name|receiveMessage
argument_list|(
name|connectionC
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received["
operator|+
name|i
operator|+
literal|"] "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|3
condition|)
block|{
name|assertNotNull
argument_list|(
literal|"Got  tx message"
argument_list|,
name|m
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
literal|"Got non tx message"
argument_list|,
name|m
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|connectionC
operator|.
name|request
argument_list|(
name|createAck
argument_list|(
name|consumerInfo
argument_list|,
name|m
argument_list|,
literal|1
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|QueueViewMBean
name|getProxyToQueueViewMBean
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq"
operator|+
literal|":destinationType=Queue,destinationName="
operator|+
name|destination
operator|.
name|getQueueName
argument_list|()
operator|+
literal|",type=Broker,brokerName=localhost"
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
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
name|AMQ5567Test
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit


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
name|store
operator|.
name|kahadb
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|net
operator|.
name|URI
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
name|Collection
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
name|AtomicInteger
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
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
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
name|ActiveMQSession
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
name|TransportConnector
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
name|Topic
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
name|ActiveMQTopic
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
name|store
operator|.
name|MessageRecoveryListener
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
name|TopicMessageStore
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
name|Wait
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|KahaDBDurableMessageRecoveryTest
block|{
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"recoverIndex"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|false
block|}
block|,
block|{
literal|true
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|dataFileDir
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|URI
name|brokerConnectURI
decl_stmt|;
specifier|private
name|boolean
name|recoverIndex
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUpBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
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
comment|/**      * @param deleteIndex      */
specifier|public
name|KahaDBDurableMessageRecoveryTest
parameter_list|(
name|boolean
name|recoverIndex
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|recoverIndex
operator|=
name|recoverIndex
expr_stmt|;
block|}
specifier|protected
name|void
name|startBroker
parameter_list|(
name|boolean
name|recoverIndex
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
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDataDirectoryFile
argument_list|(
name|dataFileDir
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|TransportConnector
argument_list|()
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setName
argument_list|(
literal|"tcp"
argument_list|)
expr_stmt|;
name|configurePersistence
argument_list|(
name|broker
argument_list|,
name|recoverIndex
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
name|brokerConnectURI
operator|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|configurePersistence
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|boolean
name|forceRecoverIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|KahaDBPersistenceAdapter
name|adapter
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|adapter
operator|.
name|setForceRecoverIndex
argument_list|(
name|forceRecoverIndex
argument_list|)
expr_stmt|;
comment|// set smaller size for test
name|adapter
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|20
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|restartBroker
parameter_list|(
name|boolean
name|deleteIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|startBroker
argument_list|(
name|deleteIndex
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Session
name|getSession
parameter_list|(
name|int
name|ackMode
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerConnectURI
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId1"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|ackMode
argument_list|)
decl_stmt|;
return|return
name|session
return|;
block|}
comment|/**      * Test that on broker restart a durable topic subscription will recover all      * messages before the "last ack" in KahaDB which could happen if using      * individual acknowledge mode and skipping messages      */
annotation|@
name|Test
specifier|public
name|void
name|durableRecoveryIndividualAcknowledge
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testTopic
init|=
literal|"test.topic"
decl_stmt|;
name|Session
name|session
init|=
name|getSession
argument_list|(
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
operator|(
name|ActiveMQTopic
operator|)
name|session
operator|.
name|createTopic
argument_list|(
name|testTopic
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|subscriber
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
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
literal|10
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
literal|"msg: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|10
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
comment|// Receive only the 5th message using individual ack mode
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|5
condition|)
block|{
name|received
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Verify there are 9 messages left still and restart broker
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|9
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|subscriber
operator|.
name|close
argument_list|()
expr_stmt|;
name|restartBroker
argument_list|(
name|recoverIndex
argument_list|)
expr_stmt|;
comment|// Verify 9 messages exist in store on startup
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|9
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
comment|// Recreate subscriber and try and receive the other 9 messages
name|session
operator|=
name|getSession
argument_list|(
name|ActiveMQSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|subscriber
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"msg: "
operator|+
name|i
argument_list|,
name|received
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"msg: "
operator|+
name|i
argument_list|,
name|received
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|subscriber
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|0
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleDurableRecoveryIndividualAcknowledge
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testTopic
init|=
literal|"test.topic"
decl_stmt|;
name|Session
name|session
init|=
name|getSession
argument_list|(
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
operator|(
name|ActiveMQTopic
operator|)
name|session
operator|.
name|createTopic
argument_list|(
name|testTopic
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|subscriber1
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|subscriber2
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub2"
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
literal|10
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
literal|"msg: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|10
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|10
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub2"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
comment|// Receive 2 messages using individual ack mode only on first sub
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|3
operator|||
name|i
operator|==
literal|7
condition|)
block|{
name|received
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Verify there are 8 messages left still and restart broker
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|8
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|10
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub2"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|subscriber1
operator|.
name|close
argument_list|()
expr_stmt|;
name|subscriber2
operator|.
name|close
argument_list|()
expr_stmt|;
name|restartBroker
argument_list|(
name|recoverIndex
argument_list|)
expr_stmt|;
comment|// Verify 8 messages exist in store on startup on sub 1 and 10 on sub 2
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|8
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|10
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub2"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
comment|// Recreate subscriber and try and receive the other 8 messages
name|session
operator|=
name|getSession
argument_list|(
name|ActiveMQSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|subscriber1
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
expr_stmt|;
name|subscriber2
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub2"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"msg: "
operator|+
name|i
argument_list|,
name|received
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|4
init|;
name|i
operator|<=
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"msg: "
operator|+
name|i
argument_list|,
name|received
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"msg: "
operator|+
name|i
argument_list|,
name|received
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Make sure sub 2 gets all 10
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber2
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"msg: "
operator|+
name|i
argument_list|,
name|received
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|subscriber1
operator|.
name|close
argument_list|()
expr_stmt|;
name|subscriber2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|0
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|0
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub2"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleDurableTestRecoverSubscription
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testTopic
init|=
literal|"test.topic"
decl_stmt|;
name|Session
name|session
init|=
name|getSession
argument_list|(
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
operator|(
name|ActiveMQTopic
operator|)
name|session
operator|.
name|createTopic
argument_list|(
name|testTopic
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|subscriber1
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|subscriber2
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub2"
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
literal|10
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
literal|"msg: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Receive 2 messages using individual ack mode only on first sub
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|subscriber1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|3
operator|||
name|i
operator|==
literal|7
condition|)
block|{
name|received
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Verify there are 8 messages left on sub 1 and 10 on sub2 and restart
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|8
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
literal|10
operator|==
name|getPendingMessageCount
argument_list|(
name|topic
argument_list|,
literal|"clientId1"
argument_list|,
literal|"sub2"
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|subscriber1
operator|.
name|close
argument_list|()
expr_stmt|;
name|subscriber2
operator|.
name|close
argument_list|()
expr_stmt|;
name|restartBroker
argument_list|(
name|recoverIndex
argument_list|)
expr_stmt|;
comment|//Manually recover subscription and verify proper messages are loaded
specifier|final
name|Topic
name|brokerTopic
init|=
operator|(
name|Topic
operator|)
name|broker
operator|.
name|getDestination
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|TopicMessageStore
name|store
init|=
operator|(
name|TopicMessageStore
operator|)
name|brokerTopic
operator|.
name|getMessageStore
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|sub1Recovered
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|sub2Recovered
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|store
operator|.
name|recoverSubscription
argument_list|(
literal|"clientId1"
argument_list|,
literal|"sub1"
argument_list|,
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
if|if
condition|(
name|textMessage
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"msg: "
operator|+
literal|3
argument_list|)
operator|||
name|textMessage
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"msg: "
operator|+
literal|7
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Got wrong message: "
operator|+
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
throw|;
block|}
name|sub1Recovered
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|store
operator|.
name|recoverSubscription
argument_list|(
literal|"clientId1"
argument_list|,
literal|"sub2"
argument_list|,
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|sub2Recovered
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//Verify proper number of messages are recovered
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|sub1Recovered
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|sub2Recovered
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|long
name|getPendingMessageCount
parameter_list|(
name|ActiveMQTopic
name|topic
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subId
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Topic
name|brokerTopic
init|=
operator|(
name|Topic
operator|)
name|broker
operator|.
name|getDestination
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|TopicMessageStore
name|store
init|=
operator|(
name|TopicMessageStore
operator|)
name|brokerTopic
operator|.
name|getMessageStore
argument_list|()
decl_stmt|;
return|return
name|store
operator|.
name|getMessageCount
argument_list|(
name|clientId
argument_list|,
name|subId
argument_list|)
return|;
block|}
block|}
end_class

end_unit


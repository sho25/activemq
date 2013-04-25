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
name|DeliveryMode
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
name|VMPendingSubscriberMessageStoragePolicy
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
name|LocalTransactionId
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

begin_class
specifier|public
class|class
name|MessageExpirationTest
extends|extends
name|BrokerTestSupport
block|{
specifier|public
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|public
name|int
name|prefetch
decl_stmt|;
specifier|public
name|byte
name|destinationType
init|=
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
decl_stmt|;
specifier|public
name|boolean
name|durableConsumer
decl_stmt|;
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|ProducerInfo
name|producerInfo
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|int
name|deliveryMode
parameter_list|,
name|int
name|timeToLive
parameter_list|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|message
operator|.
name|setTimestamp
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|message
operator|.
name|setExpiration
argument_list|(
name|now
operator|+
name|timeToLive
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|void
name|initCombosForTestMessagesWaitingForUssageDecreaseExpire
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|PolicyEntry
name|getDefaultPolicy
parameter_list|()
block|{
name|PolicyEntry
name|policy
init|=
name|super
operator|.
name|getDefaultPolicy
argument_list|()
decl_stmt|;
comment|// disable spooling
name|policy
operator|.
name|setPendingSubscriberPolicy
argument_list|(
operator|new
name|VMPendingSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// have aggressive expiry period to ensure no deadlock or clash
name|policy
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|policy
return|;
block|}
specifier|public
name|void
name|testMessagesWaitingForUsageDecreaseExpire
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start a producer
specifier|final
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
specifier|final
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
comment|// Start a consumer..
specifier|final
name|StubConnection
name|connection2
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo2
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo2
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo2
argument_list|)
decl_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|connectionInfo2
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|sessionInfo2
argument_list|)
expr_stmt|;
name|destination
operator|=
name|createDestinationInfo
argument_list|(
name|connection2
argument_list|,
name|connectionInfo2
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo2
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo2
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|consumerInfo2
operator|.
name|setPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|request
argument_list|(
name|consumerInfo2
argument_list|)
expr_stmt|;
comment|// Reduce the limit so that only 1 message can flow through the broker
comment|// at a time.
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Message
name|m1
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
specifier|final
name|Message
name|m2
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Message
name|m3
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
specifier|final
name|Message
name|m4
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
comment|// Produce in an async thread since the producer will be getting blocked
comment|// by the usage manager..
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// m1 and m3 should not expire.. but the others should.
try|try
block|{
name|connection
operator|.
name|send
argument_list|(
name|m1
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|m2
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|m3
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|m4
argument_list|)
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
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Make sure only 1 message was delivered due to prefetch == 1
name|Message
name|m
init|=
name|receiveMessage
argument_list|(
name|connection2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoMessagesLeft
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// Sleep before we ack so that the messages expire on the usage manager
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|createAck
argument_list|(
name|consumerInfo2
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
comment|// 2nd message received should be m3.. it should have expired 2nd
comment|// message sent.
name|m
operator|=
name|receiveMessage
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m3
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Sleep before we ack so that the messages expire on the usage manager
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|createAck
argument_list|(
name|consumerInfo2
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
comment|// And there should be no messages left now..
name|assertNoMessagesLeft
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestMessagesInLongTransactionExpire
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMessagesInLongTransactionExpire
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start a producer and consumer
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
name|destination
operator|=
name|createDestinationInfo
argument_list|(
name|connection
argument_list|,
name|connectionInfo
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
comment|// Start the tx..
name|LocalTransactionId
name|txid
init|=
name|createLocalTransaction
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
comment|// m1 and m3 should not expire.. but the others should.
name|Message
name|m1
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
name|m1
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
name|m1
argument_list|)
expr_stmt|;
name|Message
name|m
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|m
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
name|m
argument_list|)
expr_stmt|;
name|Message
name|m3
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
name|m3
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
name|m3
argument_list|)
expr_stmt|;
name|m
operator|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|m
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
name|m
argument_list|)
expr_stmt|;
comment|// Sleep before we commit so that the messages expire on the commit
comment|// list..
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createCommitTransaction1Phase
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
name|connection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
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
comment|// 2nd message received should be m3.. it should have expired 2nd
comment|// message sent.
name|m
operator|=
name|receiveMessage
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m3
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
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
comment|// And there should be no messages left now..
name|assertNoMessagesLeft
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestMessagesInSubscriptionPendingListExpire
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMessagesInSubscriptionPendingListExpire
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start a producer and consumer
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
name|destination
operator|=
name|createDestinationInfo
argument_list|(
name|connection
argument_list|,
name|connectionInfo
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
comment|// m1 and m3 should not expire.. but the others should.
name|Message
name|m1
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|m1
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|Message
name|m3
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|m3
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure only 1 message was delivered due to prefetch == 1
name|Message
name|m
init|=
name|receiveMessage
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoMessagesLeft
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// Sleep before we ack so that the messages expire on the pending list..
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
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
comment|// 2nd message received should be m3.. it should have expired 2nd
comment|// message sent.
name|m
operator|=
name|receiveMessage
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m3
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
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
comment|// And there should be no messages left now..
name|assertNoMessagesLeft
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|closeConnectionInfo
argument_list|(
name|connectionInfo
argument_list|)
argument_list|)
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
name|MessageExpirationTest
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


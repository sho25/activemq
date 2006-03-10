begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|IOException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|MessageNotWriteableException
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
name|CombinationTestSupport
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
name|BrokerFactory
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
name|ConnectionId
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
name|DestinationInfo
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
name|MessageDispatch
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
name|RemoveInfo
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
name|TransactionId
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
name|TransactionInfo
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
name|memory
operator|.
name|UsageManager
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

begin_class
specifier|public
class|class
name|BrokerTestSupport
extends|extends
name|CombinationTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BrokerTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Setting this to false makes the test run faster but they may be less accurate.      */
specifier|public
specifier|static
name|boolean
name|FAST_NO_MESSAGE_LEFT_ASSERT
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"FAST_NO_MESSAGE_LEFT_ASSERT"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
specifier|protected
name|RegionBroker
name|regionBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|long
name|idGenerator
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|msgIdGenerator
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|txGenerator
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|tempDestGenerator
init|=
literal|0
decl_stmt|;
specifier|protected
name|PersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|protected
name|int
name|MAX_WAIT
init|=
literal|4000
decl_stmt|;
specifier|protected
name|UsageManager
name|memoryManager
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/localhost?persistent=false"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ConsumerInfo
name|createConsumerInfo
parameter_list|(
name|SessionInfo
name|sessionInfo
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
operator|++
name|idGenerator
argument_list|)
decl_stmt|;
name|info
operator|.
name|setBrowser
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPrefetchSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|RemoveInfo
name|closeConsumerInfo
parameter_list|(
name|ConsumerInfo
name|consumerInfo
parameter_list|)
block|{
return|return
name|consumerInfo
operator|.
name|createRemoveCommand
argument_list|()
return|;
block|}
specifier|protected
name|ProducerInfo
name|createProducerInfo
parameter_list|(
name|SessionInfo
name|sessionInfo
parameter_list|)
throws|throws
name|Exception
block|{
name|ProducerInfo
name|info
init|=
operator|new
name|ProducerInfo
argument_list|(
name|sessionInfo
argument_list|,
operator|++
name|idGenerator
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|SessionInfo
name|createSessionInfo
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|)
throws|throws
name|Exception
block|{
name|SessionInfo
name|info
init|=
operator|new
name|SessionInfo
argument_list|(
name|connectionInfo
argument_list|,
operator|++
name|idGenerator
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|ConnectionInfo
name|createConnectionInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionInfo
name|info
init|=
operator|new
name|ConnectionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setConnectionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"connection:"
operator|+
operator|(
operator|++
name|idGenerator
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|ProducerInfo
name|producerInfo
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
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
name|producerInfo
argument_list|,
operator|++
name|msgIdGenerator
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
try|try
block|{
name|message
operator|.
name|setText
argument_list|(
literal|"Test Message Payload."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|e
parameter_list|)
block|{         }
return|return
name|message
return|;
block|}
specifier|protected
name|MessageAck
name|createAck
parameter_list|(
name|ConsumerInfo
name|consumerInfo
parameter_list|,
name|Message
name|msg
parameter_list|,
name|int
name|count
parameter_list|,
name|byte
name|ackType
parameter_list|)
block|{
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|setAckType
argument_list|(
name|ackType
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setConsumerId
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setLastMessageId
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|ack
return|;
block|}
specifier|protected
name|void
name|gc
parameter_list|()
block|{
name|regionBroker
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|profilerPause
parameter_list|(
name|String
name|prompt
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"profiler"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|prompt
operator|+
literal|"> Press enter to continue: "
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
operator|!=
literal|'\n'
condition|)
block|{                         }
name|log
operator|.
name|info
argument_list|(
name|prompt
operator|+
literal|"> Done."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|RemoveInfo
name|closeConnectionInfo
parameter_list|(
name|ConnectionInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|createRemoveCommand
argument_list|()
return|;
block|}
specifier|protected
name|RemoveInfo
name|closeSessionInfo
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|createRemoveCommand
argument_list|()
return|;
block|}
specifier|protected
name|RemoveInfo
name|closeProducerInfo
parameter_list|(
name|ProducerInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|createRemoveCommand
argument_list|()
return|;
block|}
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
argument_list|)
decl_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
name|deliveryMode
operator|==
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|protected
name|LocalTransactionId
name|createLocalTransaction
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
block|{
name|LocalTransactionId
name|id
init|=
operator|new
name|LocalTransactionId
argument_list|(
name|info
operator|.
name|getSessionId
argument_list|()
operator|.
name|getParentId
argument_list|()
argument_list|,
operator|++
name|txGenerator
argument_list|)
decl_stmt|;
return|return
name|id
return|;
block|}
specifier|protected
name|XATransactionId
name|createXATransaction
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|id
init|=
name|txGenerator
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
operator|++
name|txGenerator
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|XATransactionId
name|xid
init|=
operator|new
name|XATransactionId
argument_list|()
decl_stmt|;
name|xid
operator|.
name|setBranchQualifier
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|xid
operator|.
name|setGlobalTransactionId
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|xid
operator|.
name|setFormatId
argument_list|(
literal|55
argument_list|)
expr_stmt|;
return|return
name|xid
return|;
block|}
specifier|protected
name|TransactionInfo
name|createBeginTransaction
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|TransactionId
name|txid
parameter_list|)
block|{
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|txid
argument_list|,
name|TransactionInfo
operator|.
name|BEGIN
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|TransactionInfo
name|createPrepareTransaction
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|TransactionId
name|txid
parameter_list|)
block|{
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|txid
argument_list|,
name|TransactionInfo
operator|.
name|PREPARE
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|TransactionInfo
name|createCommitTransaction1Phase
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|TransactionId
name|txid
parameter_list|)
block|{
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|txid
argument_list|,
name|TransactionInfo
operator|.
name|COMMIT_ONE_PHASE
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|TransactionInfo
name|createCommitTransaction2Phase
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|TransactionId
name|txid
parameter_list|)
block|{
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|txid
argument_list|,
name|TransactionInfo
operator|.
name|COMMIT_TWO_PHASE
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|TransactionInfo
name|createRollbackTransaction
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|TransactionId
name|txid
parameter_list|)
block|{
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|txid
argument_list|,
name|TransactionInfo
operator|.
name|ROLLBACK
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|int
name|countMessagesInQueue
parameter_list|(
name|StubConnection
name|connection
parameter_list|,
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|SessionInfo
name|sessionInfo
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
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
name|consumerInfo
operator|.
name|setBrowser
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
name|ArrayList
name|skipped
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// Now get the messages.
name|Object
name|m
init|=
name|connection
operator|.
name|getDispatchQueue
argument_list|()
operator|.
name|poll
argument_list|(
name|MAX_WAIT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|m
operator|instanceof
name|MessageDispatch
operator|&&
operator|(
operator|(
name|MessageDispatch
operator|)
name|m
operator|)
operator|.
name|getConsumerId
argument_list|()
operator|.
name|equals
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
condition|)
block|{
name|MessageDispatch
name|md
init|=
operator|(
name|MessageDispatch
operator|)
name|m
decl_stmt|;
if|if
condition|(
name|md
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|createAck
argument_list|(
name|consumerInfo
argument_list|,
name|md
operator|.
name|getMessage
argument_list|()
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
else|else
block|{
break|break;
block|}
block|}
else|else
block|{
name|skipped
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|m
operator|=
name|connection
operator|.
name|getDispatchQueue
argument_list|()
operator|.
name|poll
argument_list|(
name|MAX_WAIT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|skipped
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|connection
operator|.
name|getDispatchQueue
argument_list|()
operator|.
name|put
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|send
argument_list|(
name|closeSessionInfo
argument_list|(
name|sessionInfo
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|i
return|;
block|}
specifier|protected
name|DestinationInfo
name|createTempDestinationInfo
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|,
name|byte
name|destinationType
parameter_list|)
block|{
name|DestinationInfo
name|info
init|=
operator|new
name|DestinationInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setConnectionId
argument_list|(
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setOperationType
argument_list|(
name|DestinationInfo
operator|.
name|ADD_OPERATION_TYPE
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
operator|+
literal|":"
operator|+
operator|(
operator|++
name|tempDestGenerator
operator|)
argument_list|,
name|destinationType
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|ActiveMQDestination
name|createDestinationInfo
parameter_list|(
name|StubConnection
name|connection
parameter_list|,
name|ConnectionInfo
name|connectionInfo1
parameter_list|,
name|byte
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|(
name|destinationType
operator|&
name|ActiveMQDestination
operator|.
name|TEMP_MASK
operator|)
operator|!=
literal|0
condition|)
block|{
name|DestinationInfo
name|info
init|=
name|createTempDestinationInfo
argument_list|(
name|connectionInfo1
argument_list|,
name|destinationType
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|info
operator|.
name|getDestination
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"TEST"
argument_list|,
name|destinationType
argument_list|)
return|;
block|}
block|}
specifier|protected
name|DestinationInfo
name|closeDestinationInfo
parameter_list|(
name|DestinationInfo
name|info
parameter_list|)
block|{
name|info
operator|.
name|setOperationType
argument_list|(
name|DestinationInfo
operator|.
name|REMOVE_OPERATION_TYPE
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTimeout
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|public
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|f
operator|.
name|listFiles
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|recursiveDelete
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|StubConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|StubConnection
argument_list|(
name|broker
argument_list|)
return|;
block|}
comment|/**      * @param connection      * @return      * @throws InterruptedException      */
specifier|public
name|Message
name|receiveMessage
parameter_list|(
name|StubConnection
name|connection
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|receiveMessage
argument_list|(
name|connection
argument_list|,
name|MAX_WAIT
argument_list|)
return|;
block|}
specifier|public
name|Message
name|receiveMessage
parameter_list|(
name|StubConnection
name|connection
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|o
init|=
name|connection
operator|.
name|getDispatchQueue
argument_list|()
operator|.
name|poll
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|o
operator|instanceof
name|MessageDispatch
condition|)
block|{
name|MessageDispatch
name|dispatch
init|=
operator|(
name|MessageDispatch
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|dispatch
operator|.
name|getMessage
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|dispatch
operator|.
name|setMessage
argument_list|(
name|dispatch
operator|.
name|getMessage
argument_list|()
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
name|dispatch
operator|.
name|getMessage
argument_list|()
operator|.
name|setRedeliveryCounter
argument_list|(
name|dispatch
operator|.
name|getRedeliveryCounter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dispatch
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
block|}
empty_stmt|;
specifier|protected
name|void
name|assertNoMessagesLeft
parameter_list|(
name|StubConnection
name|connection
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|wait
init|=
name|FAST_NO_MESSAGE_LEFT_ASSERT
condition|?
literal|0
else|:
name|MAX_WAIT
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|o
init|=
name|connection
operator|.
name|getDispatchQueue
argument_list|()
operator|.
name|poll
argument_list|(
name|wait
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|o
operator|instanceof
name|MessageDispatch
operator|&&
operator|(
operator|(
name|MessageDispatch
operator|)
name|o
operator|)
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Received a message."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit


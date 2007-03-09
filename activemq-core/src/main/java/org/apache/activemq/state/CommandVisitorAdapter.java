begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
package|;
end_package

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
name|BrokerInfo
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
name|ConnectionError
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
name|ConsumerControl
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
name|ConsumerId
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
name|ControlCommand
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
name|FlushCommand
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
name|KeepAliveInfo
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
name|MessageDispatchNotification
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
name|MessagePull
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
name|ProducerAck
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
name|ProducerId
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
name|RemoveSubscriptionInfo
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
name|command
operator|.
name|SessionId
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
name|ShutdownInfo
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
name|WireFormatInfo
import|;
end_import

begin_class
specifier|public
class|class
name|CommandVisitorAdapter
implements|implements
name|CommandVisitor
block|{
specifier|public
name|Response
name|processAddConnection
parameter_list|(
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processAddConsumer
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processAddDestination
parameter_list|(
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processAddProducer
parameter_list|(
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processAddSession
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processBeginTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processBrokerInfo
parameter_list|(
name|BrokerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processCommitTransactionOnePhase
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processCommitTransactionTwoPhase
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processEndTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processFlush
parameter_list|(
name|FlushCommand
name|command
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processForgetTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processKeepAlive
parameter_list|(
name|KeepAliveInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processMessage
parameter_list|(
name|Message
name|send
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processMessageAck
parameter_list|(
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processMessageDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|notification
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processMessagePull
parameter_list|(
name|MessagePull
name|pull
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processPrepareTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processProducerAck
parameter_list|(
name|ProducerAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRecoverTransactions
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRemoveConnection
parameter_list|(
name|ConnectionId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRemoveConsumer
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRemoveDestination
parameter_list|(
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRemoveProducer
parameter_list|(
name|ProducerId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRemoveSession
parameter_list|(
name|SessionId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRemoveSubscription
parameter_list|(
name|RemoveSubscriptionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRollbackTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processShutdown
parameter_list|(
name|ShutdownInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processWireFormat
parameter_list|(
name|WireFormatInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processMessageDispatch
parameter_list|(
name|MessageDispatch
name|dispatch
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processControlCommand
parameter_list|(
name|ControlCommand
name|command
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processConnectionControl
parameter_list|(
name|ConnectionControl
name|control
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processConnectionError
parameter_list|(
name|ConnectionError
name|error
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processConsumerControl
parameter_list|(
name|ConsumerControl
name|control
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit


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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Subscription
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
name|BrokerId
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

begin_comment
comment|/**  * Allows you to intercept broker operation so that features such as security can be   * implemented as a pluggable filter.  *   * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|BrokerFilter
implements|implements
name|Broker
block|{
specifier|final
specifier|protected
name|Broker
name|next
decl_stmt|;
specifier|public
name|BrokerFilter
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
specifier|public
name|Broker
name|getAdaptor
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|next
operator|.
name|getAdaptor
argument_list|(
name|type
argument_list|)
return|;
block|}
specifier|public
name|Map
name|getDestinationMap
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDestinationMap
argument_list|()
return|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|next
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
return|;
block|}
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|RemoveSubscriptionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TransactionId
index|[]
name|getPreparedTransactions
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|next
operator|.
name|getPreparedTransactions
argument_list|(
name|context
argument_list|)
return|;
block|}
specifier|public
name|int
name|prepareTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|next
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|rollbackTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|forgetTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|forgetTransaction
argument_list|(
name|context
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Connection
index|[]
name|getClients
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|next
operator|.
name|getClients
argument_list|()
return|;
block|}
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|next
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
index|[]
name|getDestinations
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|next
operator|.
name|getDestinations
argument_list|()
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|addSession
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|addSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeSession
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BrokerId
name|getBrokerId
parameter_list|()
block|{
return|return
name|next
operator|.
name|getBrokerId
argument_list|()
return|;
block|}
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getBrokerName
argument_list|()
return|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|next
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|addBroker
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|BrokerInfo
name|info
parameter_list|)
block|{
name|next
operator|.
name|addBroker
argument_list|(
name|connection
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeBroker
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|BrokerInfo
name|info
parameter_list|)
block|{
name|next
operator|.
name|removeBroker
argument_list|(
name|connection
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BrokerInfo
index|[]
name|getPeerBrokerInfos
parameter_list|()
block|{
return|return
name|next
operator|.
name|getPeerBrokerInfos
argument_list|()
return|;
block|}
specifier|public
name|void
name|processDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
name|next
operator|.
name|processDispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|processDispatchNotification
argument_list|(
name|messageDispatchNotification
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSlaveBroker
parameter_list|()
block|{
return|return
name|next
operator|.
name|isSlaveBroker
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|next
operator|.
name|isStopped
argument_list|()
return|;
block|}
specifier|public
name|Set
name|getDurableDestinations
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDurableDestinations
argument_list|()
return|;
block|}
specifier|public
name|void
name|addDestinationInfo
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|addDestinationInfo
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeDestinationInfo
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeDestinationInfo
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFaultTolerantConfiguration
parameter_list|()
block|{
return|return
name|next
operator|.
name|isFaultTolerantConfiguration
argument_list|()
return|;
block|}
block|}
end_class

end_unit


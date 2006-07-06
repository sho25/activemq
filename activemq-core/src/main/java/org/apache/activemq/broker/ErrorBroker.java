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
name|Collections
import|;
end_import

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
comment|/**  * Implementation of the broker where all it's methods throw an BrokerStoppedException.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ErrorBroker
implements|implements
name|Broker
block|{
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
specifier|public
name|ErrorBroker
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|Map
name|getDestinationMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
block|}
specifier|public
name|Set
name|getDestinations
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
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
literal|null
return|;
block|}
specifier|public
name|BrokerId
name|getBrokerId
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|Connection
index|[]
name|getClients
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|ActiveMQDestination
index|[]
name|getDestinations
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|BrokerInfo
index|[]
name|getPeerBrokerInfos
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|void
name|processDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isSlaveBroker
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|Set
name|getDurableDestinations
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
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
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isFaultTolerantConfiguration
parameter_list|()
block|{
throw|throw
operator|new
name|BrokerStoppedException
argument_list|(
name|this
operator|.
name|message
argument_list|)
throw|;
block|}
block|}
end_class

end_unit


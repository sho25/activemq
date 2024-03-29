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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
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
name|MessageReference
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
name|broker
operator|.
name|region
operator|.
name|virtual
operator|.
name|VirtualDestination
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
name|store
operator|.
name|PListStore
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
name|thread
operator|.
name|Scheduler
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
name|Usage
import|;
end_import

begin_comment
comment|/**  * Dumb implementation - used to be overriden by listeners  *  *  */
end_comment

begin_class
specifier|public
class|class
name|EmptyBroker
implements|implements
name|Broker
block|{
annotation|@
name|Override
specifier|public
name|BrokerId
name|getBrokerId
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
name|getBrokerName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Broker
name|getAdaptor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|type
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|?
name|this
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Destination
argument_list|>
name|getDestinationMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Destination
argument_list|>
name|getDestinationMap
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Destination
argument_list|>
name|getDestinations
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
specifier|public
name|Connection
index|[]
name|getClients
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
index|[]
name|getDestinations
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
literal|0
return|;
block|}
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|boolean
name|flag
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|gc
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
specifier|public
name|BrokerInfo
index|[]
name|getPeerBrokerInfos
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
name|preProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|postProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDurableDestinations
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
block|{     }
annotation|@
name|Override
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
block|{     }
annotation|@
name|Override
specifier|public
name|boolean
name|isFaultTolerantConfiguration
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConnectionContext
name|getAdminConnectionContext
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
name|setAdminConnectionContext
parameter_list|(
name|ConnectionContext
name|adminConnectionContext
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|Response
name|messagePull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
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
annotation|@
name|Override
specifier|public
name|PListStore
name|getTempDataStore
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|URI
name|getVmConnectorURI
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
name|brokerServiceStarted
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|BrokerService
name|getBrokerService
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExpired
parameter_list|(
name|MessageReference
name|messageReference
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageExpired
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|message
parameter_list|,
name|Subscription
name|subscription
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|boolean
name|sendToDeadLetterQueue
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|,
name|Subscription
name|subscription
parameter_list|,
name|Throwable
name|poisonCause
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Broker
name|getRoot
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBrokerSequenceId
parameter_list|()
block|{
return|return
operator|-
literal|1l
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fastProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|producerInfo
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|isFull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|Usage
argument_list|<
name|?
argument_list|>
name|usage
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|messageConsumed
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|messageDiscarded
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|Subscription
name|subs
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|virtualDestinationAdded
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|VirtualDestination
name|virtualDestination
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|virtualDestinationRemoved
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|VirtualDestination
name|virtualDestination
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|nowMasterBroker
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|networkBridgeStarted
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|,
name|boolean
name|createdByDuplex
parameter_list|,
name|String
name|remoteIp
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|networkBridgeStopped
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|processConsumerControl
parameter_list|(
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|ConsumerControl
name|control
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|reapplyInterceptor
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|Scheduler
name|getScheduler
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ThreadPoolExecutor
name|getExecutor
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit


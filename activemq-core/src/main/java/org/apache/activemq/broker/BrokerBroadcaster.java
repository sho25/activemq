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
name|List
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
comment|/**  * Used to add listeners for Broker actions  *   * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|BrokerBroadcaster
extends|extends
name|BrokerFilter
block|{
specifier|protected
specifier|volatile
name|Broker
index|[]
name|listeners
init|=
operator|new
name|Broker
index|[
literal|0
index|]
decl_stmt|;
specifier|public
name|BrokerBroadcaster
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
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
block|{
name|next
operator|.
name|acknowledge
argument_list|(
name|consumerExchange
argument_list|,
name|ack
argument_list|)
expr_stmt|;
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|acknowledge
argument_list|(
name|consumerExchange
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Subscription
name|answer
init|=
name|next
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
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
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
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
name|int
name|result
init|=
name|next
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
decl_stmt|;
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// TODO decide what to do with return values
name|brokers
index|[
name|i
index|]
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|rollbackTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
block|}
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
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|forgetTransaction
argument_list|(
name|context
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
block|}
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
name|createIfTemporary
parameter_list|)
throws|throws
name|Exception
block|{
name|Destination
name|result
init|=
name|next
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|createIfTemporary
argument_list|)
decl_stmt|;
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|createIfTemporary
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
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
block|}
annotation|@
name|Override
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|addSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|removeSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
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
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|addBroker
argument_list|(
name|connection
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Broker
index|[]
name|getListeners
parameter_list|()
block|{
return|return
name|listeners
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addListener
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|List
argument_list|<
name|Broker
argument_list|>
name|tmp
init|=
name|getListenersAsList
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|listeners
operator|=
name|tmp
operator|.
name|toArray
argument_list|(
operator|new
name|Broker
index|[
name|tmp
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|removeListener
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|List
argument_list|<
name|Broker
argument_list|>
name|tmp
init|=
name|getListenersAsList
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|remove
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|listeners
operator|=
name|tmp
operator|.
name|toArray
argument_list|(
operator|new
name|Broker
index|[
name|tmp
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|List
argument_list|<
name|Broker
argument_list|>
name|getListenersAsList
parameter_list|()
block|{
name|List
argument_list|<
name|Broker
argument_list|>
name|tmp
init|=
operator|new
name|ArrayList
argument_list|<
name|Broker
argument_list|>
argument_list|()
decl_stmt|;
name|Broker
name|brokers
index|[]
init|=
name|getListeners
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|brokers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|tmp
return|;
block|}
block|}
end_class

end_unit


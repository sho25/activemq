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
operator|.
name|ft
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicBoolean
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
name|Connection
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
name|ConnectionContext
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
name|ConsumerBrokerExchange
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
name|InsertableMutableBrokerFilter
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
name|MutableBrokerFilter
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
name|ProducerBrokerExchange
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
name|Command
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
name|ExceptionResponse
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
name|transport
operator|.
name|MutexTransport
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
name|ResponseCorrelator
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
name|Transport
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

begin_comment
comment|/**  * The Message Broker which passes messages to a slave  *   * @version $Revision: 1.8 $  */
end_comment

begin_class
specifier|public
class|class
name|MasterBroker
extends|extends
name|InsertableMutableBrokerFilter
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MasterBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Transport
name|slave
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ConsumerId
argument_list|,
name|ConsumerId
argument_list|>
name|consumers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ConsumerId
argument_list|,
name|ConsumerId
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Constructor      *       * @param parent      * @param transport      */
specifier|public
name|MasterBroker
parameter_list|(
name|MutableBrokerFilter
name|parent
parameter_list|,
name|Transport
name|transport
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|slave
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|slave
operator|=
operator|new
name|MutexTransport
argument_list|(
name|slave
argument_list|)
expr_stmt|;
name|this
operator|.
name|slave
operator|=
operator|new
name|ResponseCorrelator
argument_list|(
name|slave
argument_list|)
expr_stmt|;
name|this
operator|.
name|slave
operator|.
name|setTransportListener
argument_list|(
name|transport
operator|.
name|getTransportListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * start processing this broker      */
specifier|public
name|void
name|startProcessing
parameter_list|()
block|{
name|started
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|Connection
index|[]
name|connections
init|=
name|getClients
argument_list|()
decl_stmt|;
name|ConnectionControl
name|command
init|=
operator|new
name|ConnectionControl
argument_list|()
decl_stmt|;
name|command
operator|.
name|setFaultTolerant
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|connections
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|connections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|connections
index|[
name|i
index|]
operator|.
name|isActive
argument_list|()
operator|&&
name|connections
index|[
name|i
index|]
operator|.
name|isManageable
argument_list|()
condition|)
block|{
name|connections
index|[
name|i
index|]
operator|.
name|dispatchAsync
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get Connections"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * stop the broker      *       * @throws Exception      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopProcessing
argument_list|()
expr_stmt|;
block|}
comment|/**      * stop processing this broker      */
specifier|public
name|void
name|stopProcessing
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * A client is establishing a connection with the broker.      *       * @param context      * @param info      * @throws Exception      */
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
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * A client is disconnecting from the broker.      *       * @param context the environment the operation is being executed under.      * @param info      * @param error null if the client requested the disconnect or the error      *                that caused the client to disconnect.      * @throws Exception      */
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
name|super
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
name|sendAsyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a session.      *       * @param context      * @param info      * @throws Exception      */
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
name|super
operator|.
name|addSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a session.      *       * @param context      * @param info      * @throws Exception      */
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
name|super
operator|.
name|removeSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getSessionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a producer.      *       * @param context the enviorment the operation is being executed under.      * @param info      * @throws Exception      */
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
name|super
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a producer.      *       * @param context the environment the operation is being executed under.      * @param info      * @throws Exception      */
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
name|super
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * add a consumer      *       * @param context      * @param info      * @return the associated subscription      * @throws Exception      */
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
name|sendSyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
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
block|{
name|super
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|sendSyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * remove a subscription      *       * @param context      * @param info      * @throws Exception      */
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
name|super
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
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
block|{
name|super
operator|.
name|addDestinationInfo
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
name|super
operator|.
name|removeDestinationInfo
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * begin a transaction      *       * @param context      * @param xid      * @throws Exception      */
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
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|BEGIN
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|super
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
comment|/**      * Prepares a transaction. Only valid for xa transactions.      *       * @param context      * @param xid      * @return the state      * @throws Exception      */
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
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|PREPARE
argument_list|)
decl_stmt|;
name|sendSyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Rollsback a transaction.      *       * @param context      * @param xid      * @throws Exception      */
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
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|ROLLBACK
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|super
operator|.
name|rollbackTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
comment|/**      * Commits a transaction.      *       * @param context      * @param xid      * @param onePhase      * @throws Exception      */
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
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|COMMIT_ONE_PHASE
argument_list|)
decl_stmt|;
name|sendSyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|super
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
comment|/**      * Forgets a transaction.      *       * @param context      * @param xid      * @throws Exception      */
specifier|public
name|void
name|forgetTransaction
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
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|FORGET
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|super
operator|.
name|forgetTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
comment|/**      * Notifiy the Broker that a dispatch will happen      * Do in 'pre' so that slave will avoid getting ack before dispatch      * similar logic to send() below.      * @param messageDispatch      */
specifier|public
name|void
name|preProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
name|super
operator|.
name|preProcessDispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
name|MessageDispatchNotification
name|mdn
init|=
operator|new
name|MessageDispatchNotification
argument_list|()
decl_stmt|;
name|mdn
operator|.
name|setConsumerId
argument_list|(
name|messageDispatch
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|mdn
operator|.
name|setDeliverySequenceId
argument_list|(
name|messageDispatch
operator|.
name|getDeliverySequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|mdn
operator|.
name|setDestination
argument_list|(
name|messageDispatch
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageDispatch
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Message
name|msg
init|=
name|messageDispatch
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|mdn
operator|.
name|setMessageId
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumers
operator|.
name|containsKey
argument_list|(
name|messageDispatch
operator|.
name|getConsumerId
argument_list|()
argument_list|)
condition|)
block|{
name|sendSyncToSlave
argument_list|(
name|mdn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @param context      * @param message      * @throws Exception      */
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
block|{
comment|/**          * A message can be dispatched before the super.send() method returns so -          * here the order is switched to avoid problems on the slave with          * receiving acks for messages not received yet          */
name|sendSyncToSlave
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param context      * @param ack      * @throws Exception      */
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
name|sendToSlave
argument_list|(
name|ack
argument_list|)
expr_stmt|;
name|super
operator|.
name|acknowledge
argument_list|(
name|consumerExchange
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFaultTolerantConfiguration
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|void
name|sendToSlave
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
name|sendSyncToSlave
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendAsyncToSlave
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|sendToSlave
parameter_list|(
name|MessageAck
name|ack
parameter_list|)
block|{
if|if
condition|(
name|ack
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
name|sendAsyncToSlave
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendSyncToSlave
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|sendAsyncToSlave
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
name|slave
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Slave Failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|stopProcessing
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|sendSyncToSlave
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
name|Response
name|response
init|=
operator|(
name|Response
operator|)
name|slave
operator|.
name|request
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Slave Failed"
argument_list|,
name|er
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Slave Failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


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
name|transport
operator|.
name|amqp
operator|.
name|protocol
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|AmqpSupport
operator|.
name|toBytes
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|AmqpSupport
operator|.
name|toLong
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
name|util
operator|.
name|HashSet
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
name|amqp
operator|.
name|AmqpProtocolConverter
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
name|amqp
operator|.
name|ResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|Proton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Symbol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Accepted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|AmqpValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Rejected
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|transaction
operator|.
name|Declare
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|transaction
operator|.
name|Declared
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|transaction
operator|.
name|Discharge
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|transport
operator|.
name|ErrorCondition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Delivery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Receiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|message
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|Buffer
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

begin_comment
comment|/**  * Implements the AMQP Transaction Coordinator support to manage local  * transactions between an AMQP client and the broker.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransactionCoordinator
extends|extends
name|AmqpAbstractReceiver
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpTransactionCoordinator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|AmqpSession
argument_list|>
name|txSessions
init|=
operator|new
name|HashSet
argument_list|<
name|AmqpSession
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Creates a new Transaction coordinator used to manage AMQP transactions.      *      * @param session      *        the AmqpSession under which the coordinator was created.      * @param receiver      *        the AMQP receiver link endpoint for this coordinator.      */
specifier|public
name|AmqpTransactionCoordinator
parameter_list|(
name|AmqpSession
name|session
parameter_list|,
name|Receiver
name|endpoint
parameter_list|)
block|{
name|super
argument_list|(
name|session
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|processDelivery
parameter_list|(
specifier|final
name|Delivery
name|delivery
parameter_list|,
name|Buffer
name|deliveryBytes
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|Proton
operator|.
name|message
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|deliveryBytes
operator|.
name|offset
decl_stmt|;
name|int
name|len
init|=
name|deliveryBytes
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|decoded
init|=
name|message
operator|.
name|decode
argument_list|(
name|deliveryBytes
operator|.
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
assert|assert
name|decoded
operator|>
literal|0
operator|:
literal|"Make progress decoding the message"
assert|;
name|offset
operator|+=
name|decoded
expr_stmt|;
name|len
operator|-=
name|decoded
expr_stmt|;
block|}
specifier|final
name|AmqpSession
name|session
init|=
operator|(
name|AmqpSession
operator|)
name|getEndpoint
argument_list|()
operator|.
name|getSession
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
specifier|final
name|ConnectionId
name|connectionId
init|=
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|action
init|=
operator|(
operator|(
name|AmqpValue
operator|)
name|message
operator|.
name|getBody
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"COORDINATOR received: {}, [{}]"
argument_list|,
name|action
argument_list|,
name|deliveryBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|instanceof
name|Declare
condition|)
block|{
name|Declare
name|declare
init|=
operator|(
name|Declare
operator|)
name|action
decl_stmt|;
if|if
condition|(
name|declare
operator|.
name|getGlobalId
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"don't know how to handle a declare /w a set GlobalId"
argument_list|)
throw|;
block|}
name|LocalTransactionId
name|txId
init|=
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|getNextTransactionId
argument_list|()
decl_stmt|;
name|TransactionInfo
name|txInfo
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionId
argument_list|,
name|txId
argument_list|,
name|TransactionInfo
operator|.
name|BEGIN
argument_list|)
decl_stmt|;
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|registerTransaction
argument_list|(
name|txId
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|sendToActiveMQ
argument_list|(
name|txInfo
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"started transaction {}"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|Declared
name|declared
init|=
operator|new
name|Declared
argument_list|()
decl_stmt|;
name|declared
operator|.
name|setTxnId
argument_list|(
operator|new
name|Binary
argument_list|(
name|toBytes
argument_list|(
name|txId
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|disposition
argument_list|(
name|declared
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|action
operator|instanceof
name|Discharge
condition|)
block|{
specifier|final
name|Discharge
name|discharge
init|=
operator|(
name|Discharge
operator|)
name|action
decl_stmt|;
specifier|final
name|LocalTransactionId
name|txId
init|=
operator|new
name|LocalTransactionId
argument_list|(
name|connectionId
argument_list|,
name|toLong
argument_list|(
name|discharge
operator|.
name|getTxnId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|operation
decl_stmt|;
if|if
condition|(
name|discharge
operator|.
name|getFail
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"rollback transaction {}"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|operation
operator|=
name|TransactionInfo
operator|.
name|ROLLBACK
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"commit transaction {}"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|operation
operator|=
name|TransactionInfo
operator|.
name|COMMIT_ONE_PHASE
expr_stmt|;
block|}
for|for
control|(
name|AmqpSession
name|txSession
range|:
name|txSessions
control|)
block|{
if|if
condition|(
name|operation
operator|==
name|TransactionInfo
operator|.
name|ROLLBACK
condition|)
block|{
name|txSession
operator|.
name|rollback
argument_list|(
name|txId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|txSession
operator|.
name|commit
argument_list|(
name|txId
argument_list|)
expr_stmt|;
block|}
block|}
name|txSessions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|unregisterTransaction
argument_list|(
name|txId
argument_list|)
expr_stmt|;
name|TransactionInfo
name|txinfo
init|=
operator|new
name|TransactionInfo
argument_list|(
name|connectionId
argument_list|,
name|txId
argument_list|,
name|operation
argument_list|)
decl_stmt|;
name|sendToActiveMQ
argument_list|(
name|txinfo
argument_list|,
operator|new
name|ResponseHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|AmqpProtocolConverter
name|converter
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
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
name|Rejected
name|rejected
init|=
operator|new
name|Rejected
argument_list|()
decl_stmt|;
name|rejected
operator|.
name|setError
argument_list|(
operator|new
name|ErrorCondition
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"failed"
argument_list|)
argument_list|,
name|er
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|disposition
argument_list|(
name|rejected
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delivery
operator|.
name|disposition
argument_list|(
name|Accepted
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"TX: {} settling {}"
argument_list|,
name|operation
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
name|session
operator|.
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|operation
operator|==
name|TransactionInfo
operator|.
name|ROLLBACK
condition|)
block|{
name|session
operator|.
name|flushPendingMessages
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Expected coordinator message type: "
operator|+
name|action
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|replenishCredit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|replenishCredit
parameter_list|()
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
operator|<=
operator|(
name|getConfiguredReceiverCredit
argument_list|()
operator|*
literal|.2
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending more credit ({}) to transaction coordinator on session {}"
argument_list|,
name|getConfiguredReceiverCredit
argument_list|()
operator|-
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
argument_list|,
name|session
operator|.
name|getSessionId
argument_list|()
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|flow
argument_list|(
name|getConfiguredReceiverCredit
argument_list|()
operator|-
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getDestination
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
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{     }
specifier|public
name|void
name|enlist
parameter_list|(
name|AmqpSession
name|session
parameter_list|)
block|{
name|txSessions
operator|.
name|add
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


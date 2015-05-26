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
name|COPY
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
name|JMS_SELECTOR_FILTER_IDS
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
name|JMS_SELECTOR_NAME
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
name|NO_LOCAL_FILTER_IDS
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
name|NO_LOCAL_NAME
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
name|createDestination
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
name|findFilter
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
name|HashMap
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
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|ActiveMQTempDestination
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
name|selector
operator|.
name|SelectorParser
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
name|AmqpProtocolException
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
name|jms
operator|.
name|provider
operator|.
name|amqp
operator|.
name|AmqpJmsNoLocalType
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
name|jms
operator|.
name|provider
operator|.
name|amqp
operator|.
name|AmqpJmsSelectorType
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
name|DescribedType
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
name|Target
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
name|TerminusDurability
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
name|TerminusExpiryPolicy
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
name|AmqpError
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
name|engine
operator|.
name|Sender
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
name|Session
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
comment|/**  * Wraps the AMQP Session and provides the services needed to manage the remote  * peer requests for link establishment.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpSession
implements|implements
name|AmqpResource
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
name|AmqpSession
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ConsumerId
argument_list|,
name|AmqpSender
argument_list|>
name|consumers
init|=
operator|new
name|HashMap
argument_list|<
name|ConsumerId
argument_list|,
name|AmqpSender
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AmqpConnection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|Session
name|protonSession
decl_stmt|;
specifier|private
specifier|final
name|SessionId
name|sessionId
decl_stmt|;
specifier|private
name|long
name|nextProducerId
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|nextConsumerId
init|=
literal|0
decl_stmt|;
comment|/**      * Create new AmqpSession instance whose parent is the given AmqpConnection.      *      * @param connection      *        the parent connection for this session.      * @param sessionId      *        the ActiveMQ SessionId that is used to identify this session.      * @param session      *        the AMQP Session that this class manages.      */
specifier|public
name|AmqpSession
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|,
name|SessionId
name|sessionId
parameter_list|,
name|Session
name|session
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|sessionId
operator|=
name|sessionId
expr_stmt|;
name|this
operator|.
name|protonSession
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Session {} opened"
argument_list|,
name|getSessionId
argument_list|()
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|setContext
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|setIncomingCapacity
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|open
argument_list|()
expr_stmt|;
name|connection
operator|.
name|sendToActiveMQ
argument_list|(
operator|new
name|SessionInfo
argument_list|(
name|getSessionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Session {} closed"
argument_list|,
name|getSessionId
argument_list|()
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|setContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|free
argument_list|()
expr_stmt|;
name|connection
operator|.
name|sendToActiveMQ
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|getSessionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Commits all pending work for all resources managed under this session.      *      * @throws Exception if an error occurs while attempting to commit work.      */
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|AmqpSender
name|consumer
range|:
name|consumers
operator|.
name|values
argument_list|()
control|)
block|{
name|consumer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Rolls back any pending work being down under this session.      *      * @throws Exception if an error occurs while attempting to roll back work.      */
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|AmqpSender
name|consumer
range|:
name|consumers
operator|.
name|values
argument_list|()
control|)
block|{
name|consumer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Used to direct all Session managed Senders to push any queued Messages      * out to the remote peer.      *      * @throws Exception if an error occurs while flushing the messages.      */
specifier|public
name|void
name|flushPendingMessages
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|AmqpSender
name|consumer
range|:
name|consumers
operator|.
name|values
argument_list|()
control|)
block|{
name|consumer
operator|.
name|pumpOutbound
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|createCoordinator
parameter_list|(
specifier|final
name|Receiver
name|protonReceiver
parameter_list|)
throws|throws
name|Exception
block|{
name|AmqpTransactionCoordinator
name|txCoordinator
init|=
operator|new
name|AmqpTransactionCoordinator
argument_list|(
name|this
argument_list|,
name|protonReceiver
argument_list|)
decl_stmt|;
name|txCoordinator
operator|.
name|flow
argument_list|(
name|connection
operator|.
name|getConfiguredReceiverCredit
argument_list|()
argument_list|)
expr_stmt|;
name|txCoordinator
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|createReceiver
parameter_list|(
specifier|final
name|Receiver
name|protonReceiver
parameter_list|)
throws|throws
name|Exception
block|{
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
name|Target
name|remoteTarget
init|=
name|protonReceiver
operator|.
name|getRemoteTarget
argument_list|()
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
operator|new
name|ProducerInfo
argument_list|(
name|getNextProducerId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AmqpReceiver
name|receiver
init|=
operator|new
name|AmqpReceiver
argument_list|(
name|this
argument_list|,
name|protonReceiver
argument_list|,
name|producerInfo
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"opening new receiver {} on link: {}"
argument_list|,
name|producerInfo
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|protonReceiver
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Target
name|target
init|=
operator|(
name|Target
operator|)
name|remoteTarget
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
literal|null
decl_stmt|;
name|String
name|targetNodeName
init|=
name|target
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|.
name|getDynamic
argument_list|()
condition|)
block|{
name|destination
operator|=
name|connection
operator|.
name|createTemporaryDestination
argument_list|(
name|protonReceiver
argument_list|,
name|target
operator|.
name|getCapabilities
argument_list|()
argument_list|)
expr_stmt|;
name|Target
name|actualTarget
init|=
operator|new
name|Target
argument_list|()
decl_stmt|;
name|actualTarget
operator|.
name|setAddress
argument_list|(
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|actualTarget
operator|.
name|setDynamic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|protonReceiver
operator|.
name|setTarget
argument_list|(
name|actualTarget
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|addCloseAction
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|connection
operator|.
name|deleteTemporaryDestination
argument_list|(
operator|(
name|ActiveMQTempDestination
operator|)
name|receiver
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|targetNodeName
operator|!=
literal|null
operator|&&
operator|!
name|targetNodeName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|destination
operator|=
name|createDestination
argument_list|(
name|remoteTarget
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|String
name|connectionId
init|=
operator|(
operator|(
name|ActiveMQTempDestination
operator|)
name|destination
operator|)
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AmqpProtocolException
argument_list|(
name|AmqpError
operator|.
name|PRECONDITION_FAILED
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Not a broker created temp destination"
argument_list|)
throw|;
block|}
block|}
block|}
name|receiver
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|connection
operator|.
name|sendToActiveMQ
argument_list|(
name|producerInfo
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
name|ErrorCondition
name|error
init|=
literal|null
decl_stmt|;
name|Throwable
name|exception
init|=
operator|(
operator|(
name|ExceptionResponse
operator|)
name|response
operator|)
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|exception
operator|instanceof
name|SecurityException
condition|)
block|{
name|error
operator|=
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|UNAUTHORIZED_ACCESS
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|error
operator|=
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|INTERNAL_ERROR
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|close
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|.
name|flow
argument_list|(
name|connection
operator|.
name|getConfiguredReceiverCredit
argument_list|()
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmqpProtocolException
name|exception
parameter_list|)
block|{
name|receiver
operator|.
name|close
argument_list|(
operator|new
name|ErrorCondition
argument_list|(
name|Symbol
operator|.
name|getSymbol
argument_list|(
name|exception
operator|.
name|getSymbolicName
argument_list|()
argument_list|)
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|createSender
parameter_list|(
specifier|final
name|Sender
name|protonSender
parameter_list|)
throws|throws
name|Exception
block|{
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
name|Source
name|source
init|=
operator|(
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
name|Source
operator|)
name|protonSender
operator|.
name|getRemoteSource
argument_list|()
decl_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
operator|new
name|ConsumerInfo
argument_list|(
name|getNextConsumerId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AmqpSender
name|sender
init|=
operator|new
name|AmqpSender
argument_list|(
name|this
argument_list|,
name|protonSender
argument_list|,
name|consumerInfo
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"opening new sender {} on link: {}"
argument_list|,
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|protonSender
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|supportedFilters
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|protonSender
operator|.
name|setContext
argument_list|(
name|sender
argument_list|)
expr_stmt|;
name|boolean
name|noLocal
init|=
literal|false
decl_stmt|;
name|String
name|selector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Symbol
argument_list|,
name|DescribedType
argument_list|>
name|filter
init|=
name|findFilter
argument_list|(
name|source
operator|.
name|getFilter
argument_list|()
argument_list|,
name|JMS_SELECTOR_FILTER_IDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|selector
operator|=
name|filter
operator|.
name|getValue
argument_list|()
operator|.
name|getDescribed
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// Validate the Selector.
try|try
block|{
name|SelectorParser
operator|.
name|parse
argument_list|(
name|selector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidSelectorException
name|e
parameter_list|)
block|{
name|sender
operator|.
name|close
argument_list|(
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|INVALID_FIELD
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|supportedFilters
operator|.
name|put
argument_list|(
name|filter
operator|.
name|getKey
argument_list|()
argument_list|,
name|filter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|filter
operator|=
name|findFilter
argument_list|(
name|source
operator|.
name|getFilter
argument_list|()
argument_list|,
name|NO_LOCAL_FILTER_IDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|noLocal
operator|=
literal|true
expr_stmt|;
name|supportedFilters
operator|.
name|put
argument_list|(
name|filter
operator|.
name|getKey
argument_list|()
argument_list|,
name|filter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ActiveMQDestination
name|destination
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
comment|// Attempt to recover previous subscription
name|ConsumerInfo
name|storedInfo
init|=
name|connection
operator|.
name|lookupSubscription
argument_list|(
name|protonSender
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedInfo
operator|!=
literal|null
condition|)
block|{
name|destination
operator|=
name|storedInfo
operator|.
name|getDestination
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
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
name|Source
argument_list|()
expr_stmt|;
name|source
operator|.
name|setAddress
argument_list|(
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDurable
argument_list|(
name|TerminusDurability
operator|.
name|UNSETTLED_STATE
argument_list|)
expr_stmt|;
name|source
operator|.
name|setExpiryPolicy
argument_list|(
name|TerminusExpiryPolicy
operator|.
name|NEVER
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDistributionMode
argument_list|(
name|COPY
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|DescribedType
argument_list|>
name|filters
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|DescribedType
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|storedInfo
operator|.
name|isNoLocal
argument_list|()
condition|)
block|{
name|filters
operator|.
name|put
argument_list|(
name|NO_LOCAL_NAME
argument_list|,
name|AmqpJmsNoLocalType
operator|.
name|NO_LOCAL
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storedInfo
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|storedInfo
operator|.
name|getSelector
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|filters
operator|.
name|put
argument_list|(
name|JMS_SELECTOR_NAME
argument_list|,
operator|new
name|AmqpJmsSelectorType
argument_list|(
name|storedInfo
operator|.
name|getSelector
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|filters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|source
operator|.
name|setFilter
argument_list|(
name|filters
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sender
operator|.
name|close
argument_list|(
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|NOT_FOUND
argument_list|,
literal|"Unknown subscription link: "
operator|+
name|protonSender
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|source
operator|.
name|getDynamic
argument_list|()
condition|)
block|{
comment|// lets create a temp dest.
name|destination
operator|=
name|connection
operator|.
name|createTemporaryDestination
argument_list|(
name|protonSender
argument_list|,
name|source
operator|.
name|getCapabilities
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|=
operator|new
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
name|Source
argument_list|()
expr_stmt|;
name|source
operator|.
name|setAddress
argument_list|(
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDynamic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sender
operator|.
name|addCloseAction
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|connection
operator|.
name|deleteTemporaryDestination
argument_list|(
operator|(
name|ActiveMQTempDestination
operator|)
name|sender
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
name|createDestination
argument_list|(
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|String
name|connectionId
init|=
operator|(
operator|(
name|ActiveMQTempDestination
operator|)
name|destination
operator|)
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AmqpProtocolException
argument_list|(
name|AmqpError
operator|.
name|INVALID_FIELD
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Not a broker created temp destination"
argument_list|)
throw|;
block|}
block|}
block|}
name|source
operator|.
name|setFilter
argument_list|(
name|supportedFilters
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|supportedFilters
argument_list|)
expr_stmt|;
name|protonSender
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|int
name|senderCredit
init|=
name|protonSender
operator|.
name|getRemoteCredit
argument_list|()
decl_stmt|;
name|consumerInfo
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setNoRangeAcks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|senderCredit
operator|>=
literal|0
condition|?
name|senderCredit
else|:
literal|0
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setNoLocal
argument_list|(
name|noLocal
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|.
name|getDistributionMode
argument_list|()
operator|==
name|COPY
operator|&&
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|consumerInfo
operator|.
name|setBrowser
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|TerminusDurability
operator|.
name|UNSETTLED_STATE
operator|.
name|equals
argument_list|(
name|source
operator|.
name|getDurable
argument_list|()
argument_list|)
operator|||
name|TerminusDurability
operator|.
name|CONFIGURATION
operator|.
name|equals
argument_list|(
name|source
operator|.
name|getDurable
argument_list|()
argument_list|)
operator|)
operator|&&
name|destination
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|consumerInfo
operator|.
name|setSubscriptionName
argument_list|(
name|protonSender
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|sendToActiveMQ
argument_list|(
name|consumerInfo
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
name|ErrorCondition
name|error
init|=
literal|null
decl_stmt|;
name|Throwable
name|exception
init|=
operator|(
operator|(
name|ExceptionResponse
operator|)
name|response
operator|)
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|exception
operator|instanceof
name|SecurityException
condition|)
block|{
name|error
operator|=
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|UNAUTHORIZED_ACCESS
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exception
operator|instanceof
name|InvalidSelectorException
condition|)
block|{
name|error
operator|=
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|INVALID_FIELD
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|error
operator|=
operator|new
name|ErrorCondition
argument_list|(
name|AmqpError
operator|.
name|INTERNAL_ERROR
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sender
operator|.
name|close
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sender
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmqpProtocolException
name|e
parameter_list|)
block|{
name|sender
operator|.
name|close
argument_list|(
operator|new
name|ErrorCondition
argument_list|(
name|Symbol
operator|.
name|getSymbol
argument_list|(
name|e
operator|.
name|getSymbolicName
argument_list|()
argument_list|)
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Send all pending work out to the remote peer.      */
specifier|public
name|void
name|pumpProtonToSocket
parameter_list|()
block|{
name|connection
operator|.
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|registerSender
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|,
name|AmqpSender
name|sender
parameter_list|)
block|{
name|consumers
operator|.
name|put
argument_list|(
name|consumerId
argument_list|,
name|sender
argument_list|)
expr_stmt|;
name|connection
operator|.
name|registerSender
argument_list|(
name|consumerId
argument_list|,
name|sender
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|unregisterSender
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|consumers
operator|.
name|remove
argument_list|(
name|consumerId
argument_list|)
expr_stmt|;
name|connection
operator|.
name|unregisterSender
argument_list|(
name|consumerId
argument_list|)
expr_stmt|;
block|}
comment|//----- Configuration accessors ------------------------------------------//
specifier|public
name|AmqpConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|SessionId
name|getSessionId
parameter_list|()
block|{
return|return
name|sessionId
return|;
block|}
specifier|public
name|Session
name|getEndpoint
parameter_list|()
block|{
return|return
name|protonSession
return|;
block|}
specifier|public
name|long
name|getMaxFrameSize
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getMaxFrameSize
argument_list|()
return|;
block|}
comment|//----- Internal Implementation ------------------------------------------//
specifier|private
name|ConsumerId
name|getNextConsumerId
parameter_list|()
block|{
return|return
operator|new
name|ConsumerId
argument_list|(
name|sessionId
argument_list|,
name|nextConsumerId
operator|++
argument_list|)
return|;
block|}
specifier|private
name|ProducerId
name|getNextProducerId
parameter_list|()
block|{
return|return
operator|new
name|ProducerId
argument_list|(
name|sessionId
argument_list|,
name|nextProducerId
operator|++
argument_list|)
return|;
block|}
block|}
end_class

end_unit


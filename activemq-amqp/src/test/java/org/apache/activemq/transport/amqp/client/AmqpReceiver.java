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
name|client
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
name|NO_LOCAL_NAME
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingDeque
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
name|TimeUnit
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
name|javax
operator|.
name|jms
operator|.
name|InvalidDestinationException
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
name|client
operator|.
name|util
operator|.
name|ClientFuture
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
name|client
operator|.
name|util
operator|.
name|UnmodifiableReceiver
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
name|util
operator|.
name|IOExceptionSupport
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
name|Modified
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
name|Released
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
name|Source
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
name|ReceiverSettleMode
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
name|SenderSettleMode
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
comment|/**  * Receiver class that manages a Proton receiver endpoint.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpReceiver
extends|extends
name|AmqpAbstractResource
argument_list|<
name|Receiver
argument_list|>
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
name|AmqpReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO: Use constants available from Proton 0.9
specifier|private
specifier|static
specifier|final
name|Symbol
name|ACCEPTED_DESCRIPTOR_SYMBOL
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"amqp:accepted:list"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Symbol
name|REJECTED_DESCRIPTOR_SYMBOL
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"amqp:rejected:list"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Symbol
name|MODIFIED_DESCRIPTOR_SYMBOL
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"amqp:modified:list"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Symbol
name|RELEASED_DESCRIPTOR_SYMBOL
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"amqp:released:list"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|AmqpMessage
argument_list|>
name|prefetch
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<
name|AmqpMessage
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AmqpSession
name|session
decl_stmt|;
specifier|private
specifier|final
name|String
name|address
decl_stmt|;
specifier|private
specifier|final
name|String
name|receiverId
decl_stmt|;
specifier|private
name|String
name|subscriptionName
decl_stmt|;
specifier|private
name|String
name|selector
decl_stmt|;
specifier|private
name|boolean
name|presettle
decl_stmt|;
specifier|private
name|boolean
name|noLocal
decl_stmt|;
specifier|private
name|Source
name|userSpecifiedSource
decl_stmt|;
comment|/**      * Create a new receiver instance.      *      * @param session      * 		  The parent session that created the receiver.      * @param address      *        The address that this receiver should listen on.      * @param receiverId      *        The unique ID assigned to this receiver.      */
specifier|public
name|AmqpReceiver
parameter_list|(
name|AmqpSession
name|session
parameter_list|,
name|String
name|address
parameter_list|,
name|String
name|receiverId
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|receiverId
operator|=
name|receiverId
expr_stmt|;
block|}
comment|/**      * Create a new receiver instance.      *      * @param session      *        The parent session that created the receiver.      * @param source      *        The Source instance to use instead of creating and configuring one.      * @param receiverId      *        The unique ID assigned to this receiver.      */
specifier|public
name|AmqpReceiver
parameter_list|(
name|AmqpSession
name|session
parameter_list|,
name|Source
name|source
parameter_list|,
name|String
name|receiverId
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"User specified Source cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|userSpecifiedSource
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|source
operator|.
name|getAddress
argument_list|()
expr_stmt|;
name|this
operator|.
name|receiverId
operator|=
name|receiverId
expr_stmt|;
block|}
comment|/**      * Close the receiver, a closed receiver will throw exceptions if any further send      * calls are made.      *      * @throws IOException if an error occurs while closing the receiver.      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
name|close
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Detach the receiver, a closed receiver will throw exceptions if any further send      * calls are made.      *      * @throws IOException if an error occurs while closing the receiver.      */
specifier|public
name|void
name|detach
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
name|detach
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return this session's parent AmqpSession.      */
specifier|public
name|AmqpSession
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
comment|/**      * @return the address that this receiver has been configured to listen on.      */
specifier|public
name|String
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
comment|/**      * Attempts to wait on a message to be delivered to this receiver.  The receive      * call will wait indefinitely for a message to be delivered.      *      * @return a newly received message sent to this receiver.      *      * @throws Exception if an error occurs during the receive attempt.      */
specifier|public
name|AmqpMessage
name|receive
parameter_list|()
throws|throws
name|Exception
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|prefetch
operator|.
name|take
argument_list|()
return|;
block|}
comment|/**      * Attempts to receive a message sent to this receiver, waiting for the given      * timeout value before giving up and returning null.      *      * @param timeout      * 	      the time to wait for a new message to arrive.      * @param unit      * 		  the unit of time that the timeout value represents.      *      * @return a newly received message or null if the time to wait period expires.      *      * @throws Exception if an error occurs during the receive attempt.      */
specifier|public
name|AmqpMessage
name|receive
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|Exception
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|prefetch
operator|.
name|poll
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
return|;
block|}
comment|/**      * If a message is already available in this receiver's prefetch buffer then      * it is returned immediately otherwise this methods return null without waiting.      *      * @return a newly received message or null if there is no currently available message.      *      * @throws Exception if an error occurs during the receive attempt.      */
specifier|public
name|AmqpMessage
name|receiveNoWait
parameter_list|()
throws|throws
name|Exception
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|prefetch
operator|.
name|poll
argument_list|()
return|;
block|}
comment|/**      * Controls the amount of credit given to the receiver link.      *      * @param credit      *        the amount of credit to grant.      *      * @throws IOException if an error occurs while sending the flow.      */
specifier|public
name|void
name|flow
parameter_list|(
specifier|final
name|int
name|credit
parameter_list|)
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
try|try
block|{
name|getEndpoint
argument_list|()
operator|.
name|flow
argument_list|(
name|credit
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
name|request
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * Attempts to drain a given amount of credit from the link.      *      * @param credit      *        the amount of credit to drain.      *      * @throws IOException if an error occurs while sending the drain.      */
specifier|public
name|void
name|drain
parameter_list|(
specifier|final
name|int
name|credit
parameter_list|)
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
try|try
block|{
name|getEndpoint
argument_list|()
operator|.
name|drain
argument_list|(
name|credit
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
name|request
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * Accepts a message that was dispatched under the given Delivery instance.      *      * @param delivery      *        the Delivery instance to accept.      *      * @throws IOException if an error occurs while sending the accept.      */
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|Delivery
name|delivery
parameter_list|)
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|delivery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Delivery to accept cannot be null"
argument_list|)
throw|;
block|}
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|delivery
operator|.
name|isSettled
argument_list|()
condition|)
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
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
block|}
name|request
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * Reject a message that was dispatched under the given Delivery instance.      *      * @param delivery      *        the Delivery instance to reject.      * @param undeliverableHere      *        marks the delivery as not being able to be process by link it was sent to.      * @param deliveryFailed      *        indicates that the delivery failed for some reason.      *      * @throws IOException if an error occurs while sending the reject.      */
specifier|public
name|void
name|reject
parameter_list|(
specifier|final
name|Delivery
name|delivery
parameter_list|,
specifier|final
name|boolean
name|undeliverableHere
parameter_list|,
specifier|final
name|boolean
name|deliveryFailed
parameter_list|)
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|delivery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Delivery to reject cannot be null"
argument_list|)
throw|;
block|}
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|delivery
operator|.
name|isSettled
argument_list|()
condition|)
block|{
name|Modified
name|disposition
init|=
operator|new
name|Modified
argument_list|()
decl_stmt|;
name|disposition
operator|.
name|setUndeliverableHere
argument_list|(
name|undeliverableHere
argument_list|)
expr_stmt|;
name|disposition
operator|.
name|setDeliveryFailed
argument_list|(
name|deliveryFailed
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|disposition
argument_list|(
name|disposition
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
block|}
name|request
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * Release a message that was dispatched under the given Delivery instance.      *      * @param delivery      *        the Delivery instance to release.      *      * @throws IOException if an error occurs while sending the release.      */
specifier|public
name|void
name|release
parameter_list|(
specifier|final
name|Delivery
name|delivery
parameter_list|)
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|delivery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Delivery to release cannot be null"
argument_list|)
throw|;
block|}
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|()
decl_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
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
name|checkClosed
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|delivery
operator|.
name|isSettled
argument_list|()
condition|)
block|{
name|delivery
operator|.
name|disposition
argument_list|(
name|Released
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|()
expr_stmt|;
block|}
name|request
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return an unmodifiable view of the underlying Receiver instance.      */
specifier|public
name|Receiver
name|getReceiver
parameter_list|()
block|{
return|return
operator|new
name|UnmodifiableReceiver
argument_list|(
name|getEndpoint
argument_list|()
argument_list|)
return|;
block|}
comment|//----- Receiver configuration properties --------------------------------//
specifier|public
name|boolean
name|isPresettle
parameter_list|()
block|{
return|return
name|presettle
return|;
block|}
specifier|public
name|void
name|setPresettle
parameter_list|(
name|boolean
name|presettle
parameter_list|)
block|{
name|this
operator|.
name|presettle
operator|=
name|presettle
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDurable
parameter_list|()
block|{
return|return
name|subscriptionName
operator|!=
literal|null
return|;
block|}
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
specifier|public
name|void
name|setSubscriptionName
parameter_list|(
name|String
name|subscriptionName
parameter_list|)
block|{
name|this
operator|.
name|subscriptionName
operator|=
name|subscriptionName
expr_stmt|;
block|}
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|selector
return|;
block|}
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
specifier|public
name|boolean
name|isNoLocal
parameter_list|()
block|{
return|return
name|noLocal
return|;
block|}
specifier|public
name|void
name|setNoLocal
parameter_list|(
name|boolean
name|noLocal
parameter_list|)
block|{
name|this
operator|.
name|noLocal
operator|=
name|noLocal
expr_stmt|;
block|}
comment|//----- Internal implementation ------------------------------------------//
annotation|@
name|Override
specifier|protected
name|void
name|doOpen
parameter_list|()
block|{
name|Source
name|source
init|=
name|userSpecifiedSource
decl_stmt|;
name|Target
name|target
init|=
operator|new
name|Target
argument_list|()
decl_stmt|;
if|if
condition|(
name|userSpecifiedSource
operator|==
literal|null
condition|)
block|{
name|source
operator|=
operator|new
name|Source
argument_list|()
expr_stmt|;
name|source
operator|.
name|setAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|configureSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
name|String
name|receiverName
init|=
name|receiverId
operator|+
literal|":"
operator|+
name|address
decl_stmt|;
if|if
condition|(
name|getSubscriptionName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getSubscriptionName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// In the case of Durable Topic Subscriptions the client must use the same
comment|// receiver name which is derived from the subscription name property.
name|receiverName
operator|=
name|getSubscriptionName
argument_list|()
expr_stmt|;
block|}
name|Receiver
name|receiver
init|=
name|session
operator|.
name|getEndpoint
argument_list|()
operator|.
name|receiver
argument_list|(
name|receiverName
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|setTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPresettle
argument_list|()
condition|)
block|{
name|receiver
operator|.
name|setSenderSettleMode
argument_list|(
name|SenderSettleMode
operator|.
name|SETTLED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|.
name|setSenderSettleMode
argument_list|(
name|SenderSettleMode
operator|.
name|UNSETTLED
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|setReceiverSettleMode
argument_list|(
name|ReceiverSettleMode
operator|.
name|FIRST
argument_list|)
expr_stmt|;
name|setEndpoint
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|super
operator|.
name|doOpen
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doOpenCompletion
parameter_list|()
block|{
comment|// Verify the attach response contained a non-null Source
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
name|Source
name|s
init|=
name|getEndpoint
argument_list|()
operator|.
name|getRemoteSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|doOpenCompletion
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// No link terminus was created, the peer will now detach/close us.
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|getEndpoint
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doDetach
parameter_list|()
block|{
name|getEndpoint
argument_list|()
operator|.
name|detach
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Exception
name|getOpenAbortException
parameter_list|()
block|{
comment|// Verify the attach response contained a non-null Source
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
name|Source
name|s
init|=
name|getEndpoint
argument_list|()
operator|.
name|getRemoteSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|getOpenAbortException
argument_list|()
return|;
block|}
else|else
block|{
comment|// No link terminus was created, the peer has detach/closed us, create IDE.
return|return
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Link creation was refused"
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doOpenInspection
parameter_list|()
block|{
name|getStateInspector
argument_list|()
operator|.
name|inspectOpenedResource
argument_list|(
name|getReceiver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doClosedInspection
parameter_list|()
block|{
name|getStateInspector
argument_list|()
operator|.
name|inspectClosedResource
argument_list|(
name|getReceiver
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doDetachedInspection
parameter_list|()
block|{
name|getStateInspector
argument_list|()
operator|.
name|inspectDetachedResource
argument_list|(
name|getReceiver
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|configureSource
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
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
name|Symbol
index|[]
name|outcomes
init|=
operator|new
name|Symbol
index|[]
block|{
name|ACCEPTED_DESCRIPTOR_SYMBOL
block|,
name|REJECTED_DESCRIPTOR_SYMBOL
block|,
name|RELEASED_DESCRIPTOR_SYMBOL
block|,
name|MODIFIED_DESCRIPTOR_SYMBOL
block|}
decl_stmt|;
if|if
condition|(
name|getSubscriptionName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getSubscriptionName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|setDurable
argument_list|(
name|TerminusDurability
operator|.
name|UNSETTLED_STATE
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDistributionMode
argument_list|(
name|COPY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|.
name|setDurable
argument_list|(
name|TerminusDurability
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|source
operator|.
name|setExpiryPolicy
argument_list|(
name|TerminusExpiryPolicy
operator|.
name|LINK_DETACH
argument_list|)
expr_stmt|;
block|}
name|source
operator|.
name|setOutcomes
argument_list|(
name|outcomes
argument_list|)
expr_stmt|;
name|Modified
name|modified
init|=
operator|new
name|Modified
argument_list|()
decl_stmt|;
name|modified
operator|.
name|setDeliveryFailed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|modified
operator|.
name|setUndeliverableHere
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDefaultOutcome
argument_list|(
name|modified
argument_list|)
expr_stmt|;
if|if
condition|(
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
name|AmqpNoLocalFilter
operator|.
name|NO_LOCAL
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getSelector
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
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
name|AmqpJmsSelectorFilter
argument_list|(
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
annotation|@
name|Override
specifier|public
name|void
name|processDeliveryUpdates
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|Delivery
name|incoming
init|=
literal|null
decl_stmt|;
do|do
block|{
name|incoming
operator|=
name|getEndpoint
argument_list|()
operator|.
name|current
argument_list|()
expr_stmt|;
if|if
condition|(
name|incoming
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|incoming
operator|.
name|isReadable
argument_list|()
operator|&&
operator|!
name|incoming
operator|.
name|isPartial
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} has incoming Message(s)."
argument_list|,
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|processDelivery
argument_list|(
name|incoming
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|getEndpoint
argument_list|()
operator|.
name|advance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} has a partial incoming Message(s), deferring."
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|incoming
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|incoming
operator|!=
literal|null
condition|)
do|;
name|super
operator|.
name|processDeliveryUpdates
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|processDelivery
parameter_list|(
name|Delivery
name|incoming
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
literal|null
decl_stmt|;
try|try
block|{
name|message
operator|=
name|decodeIncomingMessage
argument_list|(
name|incoming
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error on transform: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|deliveryFailed
argument_list|(
name|incoming
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
name|AmqpMessage
name|amqpMessage
init|=
operator|new
name|AmqpMessage
argument_list|(
name|this
argument_list|,
name|message
argument_list|,
name|incoming
argument_list|)
decl_stmt|;
comment|// Store reference to envelope in delivery context for recovery
name|incoming
operator|.
name|setContext
argument_list|(
name|amqpMessage
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|add
argument_list|(
name|amqpMessage
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Message
name|decodeIncomingMessage
parameter_list|(
name|Delivery
name|incoming
parameter_list|)
block|{
name|int
name|count
decl_stmt|;
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|stream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|getEndpoint
argument_list|()
operator|.
name|recv
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|chunk
operator|.
name|length
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|messageBytes
init|=
name|stream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
try|try
block|{
name|Message
name|protonMessage
init|=
name|Message
operator|.
name|Factory
operator|.
name|create
argument_list|()
decl_stmt|;
name|protonMessage
operator|.
name|decode
argument_list|(
name|messageBytes
argument_list|,
literal|0
argument_list|,
name|messageBytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|protonMessage
return|;
block|}
finally|finally
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|deliveryFailed
parameter_list|(
name|Delivery
name|incoming
parameter_list|,
name|boolean
name|expandCredit
parameter_list|)
block|{
name|Modified
name|disposition
init|=
operator|new
name|Modified
argument_list|()
decl_stmt|;
name|disposition
operator|.
name|setUndeliverableHere
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|disposition
operator|.
name|setDeliveryFailed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|incoming
operator|.
name|disposition
argument_list|(
name|disposition
argument_list|)
expr_stmt|;
name|incoming
operator|.
name|settle
argument_list|()
expr_stmt|;
if|if
condition|(
name|expandCredit
condition|)
block|{
name|getEndpoint
argument_list|()
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{ address = "
operator|+
name|address
operator|+
literal|"}"
return|;
block|}
specifier|private
name|void
name|checkClosed
parameter_list|()
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Receiver is already closed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit


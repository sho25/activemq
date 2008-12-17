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
package|;
end_package

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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
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
name|management
operator|.
name|JMSProducerStatsImpl
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
name|management
operator|.
name|StatsCapable
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
name|management
operator|.
name|StatsImpl
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
name|MemoryUsage
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
name|IntrospectionSupport
import|;
end_import

begin_comment
comment|/**  * A client uses a<CODE>MessageProducer</CODE> object to send messages to a  * destination. A<CODE>MessageProducer</CODE> object is created by passing a  *<CODE>Destination</CODE> object to a message-producer creation method  * supplied by a session.  *<P>  *<CODE>MessageProducer</CODE> is the parent interface for all message  * producers.  *<P>  * A client also has the option of creating a message producer without supplying  * a destination. In this case, a destination must be provided with every send  * operation. A typical use for this kind of message producer is to send replies  * to requests using the request's<CODE>JMSReplyTo</CODE> destination.  *<P>  * A client can specify a default delivery mode, priority, and time to live for  * messages sent by a message producer. It can also specify the delivery mode,  * priority, and time to live for an individual message.  *<P>  * A client can specify a time-to-live value in milliseconds for each message it  * sends. This value defines a message expiration time that is the sum of the  * message's time-to-live and the GMT when it is sent (for transacted sends,  * this is the time the client sends the message, not the time the transaction  * is committed).  *<P>  * A JMS provider should do its best to expire messages accurately; however, the  * JMS API does not define the accuracy provided.  *   * @version $Revision: 1.14 $  * @see javax.jms.TopicPublisher  * @see javax.jms.QueueSender  * @see javax.jms.Session#createProducer  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQMessageProducer
extends|extends
name|ActiveMQMessageProducerSupport
implements|implements
name|StatsCapable
implements|,
name|Disposable
block|{
specifier|protected
name|ProducerInfo
name|info
decl_stmt|;
specifier|protected
name|boolean
name|closed
decl_stmt|;
specifier|private
name|JMSProducerStatsImpl
name|stats
decl_stmt|;
specifier|private
name|AtomicLong
name|messageSequence
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
name|MessageTransformer
name|transformer
decl_stmt|;
specifier|private
name|MemoryUsage
name|producerWindow
decl_stmt|;
specifier|protected
name|ActiveMQMessageProducer
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|ProducerId
name|producerId
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|int
name|sendTimeout
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
operator|new
name|ProducerInfo
argument_list|(
name|producerId
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|.
name|setWindowSize
argument_list|(
name|session
operator|.
name|connection
operator|.
name|getProducerWindowSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
name|destination
operator|.
name|getOptions
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|destination
operator|.
name|getOptions
argument_list|()
argument_list|)
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
operator|.
name|info
argument_list|,
name|options
argument_list|,
literal|"producer."
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// Enable producer window flow control if protocol> 3 and the window
comment|// size> 0
if|if
condition|(
name|session
operator|.
name|connection
operator|.
name|getProtocolVersion
argument_list|()
operator|>=
literal|3
operator|&&
name|this
operator|.
name|info
operator|.
name|getWindowSize
argument_list|()
operator|>
literal|0
condition|)
block|{
name|producerWindow
operator|=
operator|new
name|MemoryUsage
argument_list|(
literal|"Producer Window: "
operator|+
name|producerId
argument_list|)
expr_stmt|;
name|producerWindow
operator|.
name|setLimit
argument_list|(
name|this
operator|.
name|info
operator|.
name|getWindowSize
argument_list|()
argument_list|)
expr_stmt|;
name|producerWindow
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|defaultDeliveryMode
operator|=
name|Message
operator|.
name|DEFAULT_DELIVERY_MODE
expr_stmt|;
name|this
operator|.
name|defaultPriority
operator|=
name|Message
operator|.
name|DEFAULT_PRIORITY
expr_stmt|;
name|this
operator|.
name|defaultTimeToLive
operator|=
name|Message
operator|.
name|DEFAULT_TIME_TO_LIVE
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|messageSequence
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|stats
operator|=
operator|new
name|JMSProducerStatsImpl
argument_list|(
name|session
operator|.
name|getSessionStats
argument_list|()
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|session
operator|.
name|addProducer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|session
operator|.
name|asyncSendPacket
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|setSendTimeout
argument_list|(
name|sendTimeout
argument_list|)
expr_stmt|;
name|setTransformer
argument_list|(
name|session
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StatsImpl
name|getStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
specifier|public
name|JMSProducerStatsImpl
name|getProducerStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
comment|/**      * Gets the destination associated with this<CODE>MessageProducer</CODE>.      *       * @return this producer's<CODE>Destination/<CODE>      * @throws JMSException if the JMS provider fails to close the producer due to      *                      some internal error.      * @since 1.1      */
specifier|public
name|Destination
name|getDestination
parameter_list|()
throws|throws
name|JMSException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|info
operator|.
name|getDestination
argument_list|()
return|;
block|}
comment|/**      * Closes the message producer.      *<P>      * Since a provider may allocate some resources on behalf of a<CODE>      * MessageProducer</CODE>      * outside the Java virtual machine, clients should close them when they are      * not needed. Relying on garbage collection to eventually reclaim these      * resources may not be timely enough.      *       * @throws JMSException if the JMS provider fails to close the producer due      *                 to some internal error.      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|dispose
argument_list|()
expr_stmt|;
name|this
operator|.
name|session
operator|.
name|asyncSendPacket
argument_list|(
name|info
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|this
operator|.
name|session
operator|.
name|removeProducer
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|producerWindow
operator|!=
literal|null
condition|)
block|{
name|producerWindow
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**      * Check if the instance of this producer has been closed.      *       * @throws IllegalStateException      */
specifier|protected
name|void
name|checkClosed
parameter_list|()
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The producer is closed"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sends a message to a destination for an unidentified message producer,      * specifying delivery mode, priority and time to live.      *<P>      * Typically, a message producer is assigned a destination at creation time;      * however, the JMS API also supports unidentified message producers, which      * require that the destination be supplied every time a message is sent.      *       * @param destination the destination to send this message to      * @param message the message to send      * @param deliveryMode the delivery mode to use      * @param priority the priority for this message      * @param timeToLive the message's lifetime (in milliseconds)      * @throws JMSException if the JMS provider fails to send the message due to      *                 some internal error.      * @throws UnsupportedOperationException if an invalid destination is      *                 specified.      * @throws InvalidDestinationException if a client uses this method with an      *                 invalid destination.      * @see javax.jms.Session#createProducer      * @since 1.1      */
specifier|public
name|void
name|send
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Message
name|message
parameter_list|,
name|int
name|deliveryMode
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeToLive
parameter_list|)
throws|throws
name|JMSException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"A destination must be specified."
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Don't understand null destinations"
argument_list|)
throw|;
block|}
name|ActiveMQDestination
name|dest
decl_stmt|;
if|if
condition|(
name|destination
operator|==
name|info
operator|.
name|getDestination
argument_list|()
condition|)
block|{
name|dest
operator|=
operator|(
name|ActiveMQDestination
operator|)
name|destination
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|==
literal|null
condition|)
block|{
name|dest
operator|=
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This producer can only send messages to: "
operator|+
name|this
operator|.
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"No destination specified"
argument_list|)
throw|;
block|}
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
name|Message
name|transformedMessage
init|=
name|transformer
operator|.
name|producerTransform
argument_list|(
name|session
argument_list|,
name|this
argument_list|,
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|transformedMessage
operator|!=
literal|null
condition|)
block|{
name|message
operator|=
name|transformedMessage
expr_stmt|;
block|}
block|}
if|if
condition|(
name|producerWindow
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|producerWindow
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Send aborted due to thread interrupt."
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|session
operator|.
name|send
argument_list|(
name|this
argument_list|,
name|dest
argument_list|,
name|message
argument_list|,
name|deliveryMode
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|,
name|producerWindow
argument_list|,
name|sendTimeout
argument_list|)
expr_stmt|;
name|stats
operator|.
name|onMessage
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MessageTransformer
name|getTransformer
parameter_list|()
block|{
return|return
name|transformer
return|;
block|}
comment|/**      * Sets the transformer used to transform messages before they are sent on      * to the JMS bus      */
specifier|public
name|void
name|setTransformer
parameter_list|(
name|MessageTransformer
name|transformer
parameter_list|)
block|{
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
block|}
comment|/**      * @return the time in milli second when this object was created.      */
specifier|protected
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
comment|/**      * @return Returns the messageSequence.      */
specifier|protected
name|long
name|getMessageSequence
parameter_list|()
block|{
return|return
name|messageSequence
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**      * @param messageSequence The messageSequence to set.      */
specifier|protected
name|void
name|setMessageSequence
parameter_list|(
name|AtomicLong
name|messageSequence
parameter_list|)
block|{
name|this
operator|.
name|messageSequence
operator|=
name|messageSequence
expr_stmt|;
block|}
comment|/**      * @return Returns the info.      */
specifier|protected
name|ProducerInfo
name|getProducerInfo
parameter_list|()
block|{
return|return
name|this
operator|.
name|info
operator|!=
literal|null
condition|?
name|this
operator|.
name|info
else|:
literal|null
return|;
block|}
comment|/**      * @param info The info to set      */
specifier|protected
name|void
name|setProducerInfo
parameter_list|(
name|ProducerInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ActiveMQMessageProducer { value="
operator|+
name|info
operator|.
name|getProducerId
argument_list|()
operator|+
literal|" }"
return|;
block|}
specifier|public
name|void
name|onProducerAck
parameter_list|(
name|ProducerAck
name|pa
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|producerWindow
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|producerWindow
operator|.
name|decreaseUsage
argument_list|(
name|pa
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


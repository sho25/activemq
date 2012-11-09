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
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
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

begin_comment
comment|/**  * A client uses a<CODE>QueueSender</CODE> object to send messages to a  * queue.<p/>  *<P>  * Normally, the<CODE>Queue</CODE> is specified when a<CODE>QueueSender  *</CODE>  * is created. In this case, an attempt to use the<CODE>send</CODE> methods  * for an unidentified<CODE>QueueSender</CODE> will throw a<CODE>  * java.lang.UnsupportedOperationException</CODE>.  *<p/>  *<P>  * If the<CODE>QueueSender</CODE> is created with an unidentified<CODE>  * Queue</CODE>,  * an attempt to use the<CODE>send</CODE> methods that assume that the  *<CODE>Queue</CODE> has been identified will throw a<CODE>  * java.lang.UnsupportedOperationException</CODE>.  *<p/>  *<P>  * During the execution of its<CODE>send</CODE> method, a message must not be  * changed by other threads within the client. If the message is modified, the  * result of the<CODE>send</CODE> is undefined.<p/>  *<P>  * After sending a message, a client may retain and modify it without affecting  * the message that has been sent. The same message object may be sent multiple  * times.<p/>  *<P>  * The following message headers are set as part of sending a message:  *<code>JMSDestination</code>,<code>JMSDeliveryMode</code>,<code>JMSExpiration</code>,<code>JMSPriority</code>,  *<code>JMSMessageID</code> and<code>JMSTimeStamp</code>. When the  * message is sent, the values of these headers are ignored. After the  * completion of the<CODE>send</CODE>, the headers hold the values specified  * by the method sending the message. It is possible for the<code>send</code>  * method not to set<code>JMSMessageID</code> and<code>JMSTimeStamp</code>  * if the setting of these headers is explicitly disabled by the  *<code>MessageProducer.setDisableMessageID</code> or  *<code>MessageProducer.setDisableMessageTimestamp</code> method.<p/>  *<P>  * Creating a<CODE>MessageProducer</CODE> provides the same features as  * creating a<CODE>QueueSender</CODE>. A<CODE>MessageProducer</CODE>  * object is recommended when creating new code. The<CODE>QueueSender</CODE>  * is provided to support existing code.  *   * @see javax.jms.MessageProducer  * @see javax.jms.QueueSession#createSender(Queue)  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQQueueSender
extends|extends
name|ActiveMQMessageProducer
implements|implements
name|QueueSender
block|{
specifier|protected
name|ActiveMQQueueSender
parameter_list|(
name|ActiveMQSession
name|session
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
argument_list|,
name|session
operator|.
name|getNextProducerId
argument_list|()
argument_list|,
name|destination
argument_list|,
name|sendTimeout
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the queue associated with this<CODE>QueueSender</CODE>.      *       * @return this sender's queue      * @throws JMSException if the JMS provider fails to get the queue for this      *<CODE>QueueSender</CODE> due to some internal error.      */
specifier|public
name|Queue
name|getQueue
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|Queue
operator|)
name|super
operator|.
name|getDestination
argument_list|()
return|;
block|}
comment|/**      * Sends a message to a queue for an unidentified message producer. Uses the      *<CODE>QueueSender</CODE>'s default delivery mode, priority, and time      * to live.<p/>      *<P>      * Typically, a message producer is assigned a queue at creation time;      * however, the JMS API also supports unidentified message producers, which      * require that the queue be supplied every time a message is sent.      *       * @param queue the queue to send this message to      * @param message the message to send      * @throws JMSException if the JMS provider fails to send the message due to      *                 some internal error.      * @see javax.jms.MessageProducer#getDeliveryMode()      * @see javax.jms.MessageProducer#getTimeToLive()      * @see javax.jms.MessageProducer#getPriority()      */
specifier|public
name|void
name|send
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
operator|.
name|send
argument_list|(
name|queue
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sends a message to a queue for an unidentified message producer,      * specifying delivery mode, priority and time to live.<p/>      *<P>      * Typically, a message producer is assigned a queue at creation time;      * however, the JMS API also supports unidentified message producers, which      * require that the queue be supplied every time a message is sent.      *       * @param queue the queue to send this message to      * @param message the message to send      * @param deliveryMode the delivery mode to use      * @param priority the priority for this message      * @param timeToLive the message's lifetime (in milliseconds)      * @throws JMSException if the JMS provider fails to send the message due to      *                 some internal error.      */
specifier|public
name|void
name|send
parameter_list|(
name|Queue
name|queue
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
name|super
operator|.
name|send
argument_list|(
name|queue
argument_list|,
name|message
argument_list|,
name|deliveryMode
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

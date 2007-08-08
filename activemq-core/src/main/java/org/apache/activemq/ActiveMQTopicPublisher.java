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
name|javax
operator|.
name|jms
operator|.
name|MessageFormatException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
import|;
end_import

begin_comment
comment|/**  * A client uses a<CODE>TopicPublisher</CODE> object to publish messages on  * a topic. A<CODE>TopicPublisher</CODE> object is the publish-subscribe  * form of a message producer.  *<p/>  *<P>  * Normally, the<CODE>Topic</CODE> is specified when a<CODE>TopicPublisher  *</CODE> is created. In this case, an attempt to use the<CODE>publish  *</CODE> methods for an unidentified<CODE>TopicPublisher</CODE> will throw  * a<CODE>java.lang.UnsupportedOperationException</CODE>.  *<p/>  *<P>  * If the<CODE>TopicPublisher</CODE> is created with an unidentified<CODE>  * Topic</CODE>, an attempt to use the<CODE>publish</CODE> methods that  * assume that the<CODE>Topic</CODE> has been identified will throw a<CODE>  * java.lang.UnsupportedOperationException</CODE>.  *<p/>  *<P>  * During the execution of its<CODE>publish</CODE> method, a message must  * not be changed by other threads within the client. If the message is  * modified, the result of the<CODE>publish</CODE> is undefined.  *<p/>  *<P>  * After publishing a message, a client may retain and modify it without  * affecting the message that has been published. The same message object may  * be published multiple times.  *<p/>  *<P>  * The following message headers are set as part of publishing a message:  *<code>JMSDestination</code>,<code>JMSDeliveryMode</code>,<code>JMSExpiration</code>,  *<code>JMSPriority</code>,<code>JMSMessageID</code> and<code>JMSTimeStamp</code>.  * When the message is published, the values of these headers are ignored.  * After completion of the<CODE>publish</CODE>, the headers hold the values  * specified by the method publishing the message. It is possible for the  *<CODE>publish</CODE> method not to set<code>JMSMessageID</code> and  *<code>JMSTimeStamp</code> if the setting of these headers is explicitly  * disabled by the<code>MessageProducer.setDisableMessageID</code> or<code>MessageProducer.setDisableMessageTimestamp</code>  * method.  *<p/>  *<P>  * Creating a<CODE>MessageProducer</CODE> provides the same features as  * creating a<CODE>TopicPublisher</CODE>. A<CODE>MessageProducer</CODE>  * object is recommended when creating new code. The<CODE>TopicPublisher  *</CODE> is provided to support existing code.  *<p/>  *<p/>  *<P>  * Because<CODE>TopicPublisher</CODE> inherits from<CODE>MessageProducer  *</CODE>, it inherits the<CODE>send</CODE> methods that are a part of the  *<CODE>MessageProducer</CODE> interface. Using the<CODE>send</CODE>  * methods will have the same effect as using the<CODE>publish</CODE>  * methods: they are functionally the same.  *  * @see Session#createProducer(Destination)  * @see TopicSession#createPublisher(Topic)  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQTopicPublisher
extends|extends
name|ActiveMQMessageProducer
implements|implements
name|TopicPublisher
block|{
specifier|protected
name|ActiveMQTopicPublisher
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|ActiveMQDestination
name|destination
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
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the topic associated with this<CODE>TopicPublisher</CODE>.      *      * @return this publisher's topic      * @throws JMSException if the JMS provider fails to get the topic for this      *<CODE>TopicPublisher</CODE> due to some internal error.      */
specifier|public
name|Topic
name|getTopic
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|Topic
operator|)
name|super
operator|.
name|getDestination
argument_list|()
return|;
block|}
comment|/**      * Publishes a message to the topic. Uses the<CODE>TopicPublisher</CODE>'s      * default delivery mode, priority, and time to live.      *      * @param message the message to publish      * @throws JMSException                if the JMS provider fails to publish the message due to      *                                     some internal error.      * @throws MessageFormatException      if an invalid message is specified.      * @throws InvalidDestinationException if a client uses this method with a<CODE>TopicPublisher      *</CODE> with an invalid topic.      * @throws java.lang.UnsupportedOperationException      *                                     if a client uses this method with a<CODE>TopicPublisher      *</CODE> that did not specify a topic at creation time.      * @see javax.jms.MessageProducer#getDeliveryMode()      * @see javax.jms.MessageProducer#getTimeToLive()      * @see javax.jms.MessageProducer#getPriority()      */
specifier|public
name|void
name|publish
parameter_list|(
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
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Publishes a message to the topic, specifying delivery mode, priority,      * and time to live.      *      * @param message      the message to publish      * @param deliveryMode the delivery mode to use      * @param priority     the priority for this message      * @param timeToLive   the message's lifetime (in milliseconds)      * @throws JMSException                if the JMS provider fails to publish the message due to      *                                     some internal error.      * @throws MessageFormatException      if an invalid message is specified.      * @throws InvalidDestinationException if a client uses this method with a<CODE>TopicPublisher      *</CODE> with an invalid topic.      * @throws java.lang.UnsupportedOperationException      *                                     if a client uses this method with a<CODE>TopicPublisher      *</CODE> that did not specify a topic at creation time.      */
specifier|public
name|void
name|publish
parameter_list|(
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
comment|/**      * Publishes a message to a topic for an unidentified message producer.      * Uses the<CODE>TopicPublisher</CODE>'s default delivery mode,      * priority, and time to live.      *<p/>      *<P>      * Typically, a message producer is assigned a topic at creation time;      * however, the JMS API also supports unidentified message producers, which      * require that the topic be supplied every time a message is published.      *      * @param topic   the topic to publish this message to      * @param message the message to publish      * @throws JMSException                if the JMS provider fails to publish the message due to      *                                     some internal error.      * @throws MessageFormatException      if an invalid message is specified.      * @throws InvalidDestinationException if a client uses this method with an invalid topic.      * @see javax.jms.MessageProducer#getDeliveryMode()      * @see javax.jms.MessageProducer#getTimeToLive()      * @see javax.jms.MessageProducer#getPriority()      */
specifier|public
name|void
name|publish
parameter_list|(
name|Topic
name|topic
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
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Publishes a message to a topic for an unidentified message producer,      * specifying delivery mode, priority and time to live.      *<p/>      *<P>      * Typically, a message producer is assigned a topic at creation time;      * however, the JMS API also supports unidentified message producers, which      * require that the topic be supplied every time a message is published.      *      * @param topic        the topic to publish this message to      * @param message      the message to publish      * @param deliveryMode the delivery mode to use      * @param priority     the priority for this message      * @param timeToLive   the message's lifetime (in milliseconds)      * @throws JMSException                if the JMS provider fails to publish the message due to      *                                     some internal error.      * @throws MessageFormatException      if an invalid message is specified.      * @throws InvalidDestinationException if a client uses this method with an invalid topic.      */
specifier|public
name|void
name|publish
parameter_list|(
name|Topic
name|topic
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
name|topic
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


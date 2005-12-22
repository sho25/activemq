begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|MapMessage
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
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
name|QueueBrowser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|StreamMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_comment
comment|/**  * A TopicSession implementation that throws IllegalStateExceptions  * when Queue operations are attempted but which delegates   * to another TopicSession for all other operations.  *   * The ActiveMQSessions implement both Topic and Queue Sessions   * methods but the spec states that TopicSession should throw Exceptions   * if queue operations are attempted on it.    *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQTopicSession
implements|implements
name|TopicSession
block|{
specifier|private
specifier|final
name|TopicSession
name|next
decl_stmt|;
specifier|public
name|ActiveMQTopicSession
parameter_list|(
name|TopicSession
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
comment|/** 	 * @throws JMSException 	 */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
name|next
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @throws JMSException 	 */
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|JMSException
block|{
name|next
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param queue 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|QueueBrowser
name|createBrowser
parameter_list|(
name|Queue
name|queue
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Operation not supported by a TopicSession"
argument_list|)
throw|;
block|}
comment|/** 	 * @param queue 	 * @param messageSelector 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|QueueBrowser
name|createBrowser
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|messageSelector
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Operation not supported by a TopicSession"
argument_list|)
throw|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createBytesMessage
argument_list|()
return|;
block|}
comment|/** 	 * @param destination 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Queue
condition|)
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Queues are not supported by a TopicSession"
argument_list|)
throw|;
return|return
name|next
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
comment|/** 	 * @param destination 	 * @param messageSelector 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|messageSelector
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Queue
condition|)
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Queues are not supported by a TopicSession"
argument_list|)
throw|;
return|return
name|next
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|messageSelector
argument_list|)
return|;
block|}
comment|/** 	 * @param destination 	 * @param messageSelector 	 * @param NoLocal 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|NoLocal
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Queue
condition|)
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Queues are not supported by a TopicSession"
argument_list|)
throw|;
return|return
name|next
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|messageSelector
argument_list|,
name|NoLocal
argument_list|)
return|;
block|}
comment|/** 	 * @param topic 	 * @param name 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @param topic 	 * @param name 	 * @param messageSelector 	 * @param noLocal 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|name
argument_list|,
name|messageSelector
argument_list|,
name|noLocal
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createMapMessage
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|Message
name|createMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createMessage
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createObjectMessage
argument_list|()
return|;
block|}
comment|/** 	 * @param object 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|(
name|Serializable
name|object
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createObjectMessage
argument_list|(
name|object
argument_list|)
return|;
block|}
comment|/** 	 * @param destination 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|MessageProducer
name|createProducer
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Queue
condition|)
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Queues are not supported by a TopicSession"
argument_list|)
throw|;
return|return
name|next
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
return|;
block|}
comment|/** 	 * @param topic 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TopicPublisher
name|createPublisher
parameter_list|(
name|Topic
name|topic
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createPublisher
argument_list|(
name|topic
argument_list|)
return|;
block|}
comment|/** 	 * @param queueName 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|Queue
name|createQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Operation not supported by a TopicSession"
argument_list|)
throw|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createStreamMessage
argument_list|()
return|;
block|}
comment|/** 	 * @param topic 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TopicSubscriber
name|createSubscriber
parameter_list|(
name|Topic
name|topic
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|)
return|;
block|}
comment|/** 	 * @param topic 	 * @param messageSelector 	 * @param noLocal 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TopicSubscriber
name|createSubscriber
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|,
name|messageSelector
argument_list|,
name|noLocal
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TemporaryQueue
name|createTemporaryQueue
parameter_list|()
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Operation not supported by a TopicSession"
argument_list|)
throw|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TemporaryTopic
name|createTemporaryTopic
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createTemporaryTopic
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createTextMessage
argument_list|()
return|;
block|}
comment|/** 	 * @param text 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
return|;
block|}
comment|/** 	 * @param topicName 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|Topic
name|createTopic
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#equals(java.lang.Object) 	 */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
return|return
name|next
operator|.
name|equals
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|int
name|getAcknowledgeMode
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|getAcknowledgeMode
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|MessageListener
name|getMessageListener
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|getMessageListener
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 * @throws JMSException 	 */
specifier|public
name|boolean
name|getTransacted
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|next
operator|.
name|getTransacted
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#hashCode() 	 */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|next
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** 	 * @throws JMSException 	 */
specifier|public
name|void
name|recover
parameter_list|()
throws|throws
name|JMSException
block|{
name|next
operator|.
name|recover
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @throws JMSException 	 */
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|JMSException
block|{
name|next
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  	 */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|next
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param listener 	 * @throws JMSException 	 */
specifier|public
name|void
name|setMessageListener
parameter_list|(
name|MessageListener
name|listener
parameter_list|)
throws|throws
name|JMSException
block|{
name|next
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#toString() 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|next
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * @param name 	 * @throws JMSException 	 */
specifier|public
name|void
name|unsubscribe
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
block|{
name|next
operator|.
name|unsubscribe
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


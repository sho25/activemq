begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|pool
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
name|ActiveMQMessageProducer
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
name|ActiveMQQueueSender
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
name|ActiveMQSession
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
name|ActiveMQTopicPublisher
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
name|AlreadyClosedException
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
name|JMSExceptionSupport
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|ObjectPool
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
name|QueueReceiver
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
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PooledSession
implements|implements
name|TopicSession
implements|,
name|QueueSession
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PooledSession
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQSession
name|session
decl_stmt|;
specifier|private
name|SessionPool
name|sessionPool
decl_stmt|;
specifier|private
name|ActiveMQMessageProducer
name|messageProducer
decl_stmt|;
specifier|private
name|ActiveMQQueueSender
name|queueSender
decl_stmt|;
specifier|private
name|ActiveMQTopicPublisher
name|topicPublisher
decl_stmt|;
specifier|private
name|boolean
name|transactional
init|=
literal|true
decl_stmt|;
specifier|public
name|PooledSession
parameter_list|(
name|ActiveMQSession
name|aSession
parameter_list|,
name|SessionPool
name|sessionPool
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|aSession
expr_stmt|;
name|this
operator|.
name|sessionPool
operator|=
name|sessionPool
expr_stmt|;
name|this
operator|.
name|transactional
operator|=
name|session
operator|.
name|isTransacted
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// TODO a cleaner way to reset??
comment|// lets reset the session
name|getSession
argument_list|()
operator|.
name|setMessageListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// maybe do a rollback?
if|if
condition|(
name|transactional
condition|)
block|{
try|try
block|{
name|getSession
argument_list|()
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Caught exception trying rollback() when putting session back into the pool: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// lets close the session and not put the session back into the pool
try|try
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e1
parameter_list|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Ignoring exception as discarding session: "
operator|+
name|e1
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
name|session
operator|=
literal|null
expr_stmt|;
return|return;
block|}
block|}
name|sessionPool
operator|.
name|returnSession
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|JMSException
block|{
name|getSession
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createBytesMessage
argument_list|()
return|;
block|}
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createMapMessage
argument_list|()
return|;
block|}
specifier|public
name|Message
name|createMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createMessage
argument_list|()
return|;
block|}
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createObjectMessage
argument_list|()
return|;
block|}
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|(
name|Serializable
name|serializable
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createObjectMessage
argument_list|(
name|serializable
argument_list|)
return|;
block|}
specifier|public
name|Queue
name|createQueue
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|s
argument_list|)
return|;
block|}
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createStreamMessage
argument_list|()
return|;
block|}
specifier|public
name|TemporaryQueue
name|createTemporaryQueue
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTemporaryQueue
argument_list|()
return|;
block|}
specifier|public
name|TemporaryTopic
name|createTemporaryTopic
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTemporaryTopic
argument_list|()
return|;
block|}
specifier|public
name|void
name|unsubscribe
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|JMSException
block|{
name|getSession
argument_list|()
operator|.
name|unsubscribe
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|()
return|;
block|}
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|s
argument_list|)
return|;
block|}
specifier|public
name|Topic
name|createTopic
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|s
argument_list|)
return|;
block|}
specifier|public
name|int
name|getAcknowledgeMode
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|getAcknowledgeMode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getTransacted
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|getTransacted
argument_list|()
return|;
block|}
specifier|public
name|void
name|recover
parameter_list|()
throws|throws
name|JMSException
block|{
name|getSession
argument_list|()
operator|.
name|recover
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|JMSException
block|{
name|getSession
argument_list|()
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Consumer related methods
comment|//-------------------------------------------------------------------------
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
return|return
name|getSession
argument_list|()
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
return|;
block|}
specifier|public
name|QueueBrowser
name|createBrowser
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|)
return|;
block|}
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
return|return
name|getSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|selector
argument_list|)
return|;
block|}
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|selector
argument_list|,
name|noLocal
argument_list|)
return|;
block|}
specifier|public
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|selector
argument_list|)
return|;
block|}
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
name|selector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|name
argument_list|,
name|selector
argument_list|,
name|noLocal
argument_list|)
return|;
block|}
specifier|public
name|MessageListener
name|getMessageListener
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|getMessageListener
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMessageListener
parameter_list|(
name|MessageListener
name|messageListener
parameter_list|)
throws|throws
name|JMSException
block|{
name|getSession
argument_list|()
operator|.
name|setMessageListener
argument_list|(
name|messageListener
argument_list|)
expr_stmt|;
block|}
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
name|getSession
argument_list|()
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|)
return|;
block|}
specifier|public
name|TopicSubscriber
name|createSubscriber
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|local
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|,
name|selector
argument_list|,
name|local
argument_list|)
return|;
block|}
specifier|public
name|QueueReceiver
name|createReceiver
parameter_list|(
name|Queue
name|queue
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
return|;
block|}
specifier|public
name|QueueReceiver
name|createReceiver
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|)
return|;
block|}
comment|// Producer related methods
comment|//-------------------------------------------------------------------------
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
return|return
operator|new
name|PooledProducer
argument_list|(
name|getMessageProducer
argument_list|()
argument_list|,
name|destination
argument_list|)
return|;
block|}
specifier|public
name|QueueSender
name|createSender
parameter_list|(
name|Queue
name|queue
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|PooledQueueSender
argument_list|(
name|getQueueSender
argument_list|()
argument_list|,
name|queue
argument_list|)
return|;
block|}
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
operator|new
name|PooledTopicPublisher
argument_list|(
name|getTopicPublisher
argument_list|()
argument_list|,
name|topic
argument_list|)
return|;
block|}
comment|// Implementation methods
comment|//-------------------------------------------------------------------------
specifier|protected
name|ActiveMQSession
name|getSession
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"The session has already been closed"
argument_list|)
throw|;
block|}
return|return
name|session
return|;
block|}
specifier|public
name|ActiveMQMessageProducer
name|getMessageProducer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|messageProducer
operator|==
literal|null
condition|)
block|{
name|messageProducer
operator|=
operator|(
name|ActiveMQMessageProducer
operator|)
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|messageProducer
return|;
block|}
specifier|public
name|ActiveMQQueueSender
name|getQueueSender
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|queueSender
operator|==
literal|null
condition|)
block|{
name|queueSender
operator|=
operator|(
name|ActiveMQQueueSender
operator|)
name|getSession
argument_list|()
operator|.
name|createSender
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|queueSender
return|;
block|}
specifier|public
name|ActiveMQTopicPublisher
name|getTopicPublisher
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|topicPublisher
operator|==
literal|null
condition|)
block|{
name|topicPublisher
operator|=
operator|(
name|ActiveMQTopicPublisher
operator|)
name|getSession
argument_list|()
operator|.
name|createPublisher
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|topicPublisher
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PooledSession { "
operator|+
name|session
operator|+
literal|" }"
return|;
block|}
block|}
end_class

end_unit


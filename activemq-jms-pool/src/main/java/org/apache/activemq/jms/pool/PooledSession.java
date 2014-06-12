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
name|jms
operator|.
name|pool
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|CopyOnWriteArrayList
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
name|Session
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
name|javax
operator|.
name|jms
operator|.
name|XASession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|KeyedObjectPool
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

begin_class
specifier|public
class|class
name|PooledSession
implements|implements
name|Session
implements|,
name|TopicSession
implements|,
name|QueueSession
implements|,
name|XASession
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PooledSession
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SessionKey
name|key
decl_stmt|;
specifier|private
specifier|final
name|KeyedObjectPool
argument_list|<
name|SessionKey
argument_list|,
name|Session
argument_list|>
name|sessionPool
decl_stmt|;
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|MessageConsumer
argument_list|>
name|consumers
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|MessageConsumer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|QueueBrowser
argument_list|>
name|browsers
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|QueueBrowser
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|PooledSessionEventListener
argument_list|>
name|sessionEventListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|PooledSessionEventListener
argument_list|>
argument_list|()
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
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|TopicPublisher
name|publisher
decl_stmt|;
specifier|private
name|QueueSender
name|sender
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|boolean
name|transactional
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|ignoreClose
decl_stmt|;
specifier|private
name|boolean
name|isXa
decl_stmt|;
specifier|private
name|boolean
name|useAnonymousProducers
init|=
literal|true
decl_stmt|;
specifier|public
name|PooledSession
parameter_list|(
name|SessionKey
name|key
parameter_list|,
name|Session
name|session
parameter_list|,
name|KeyedObjectPool
argument_list|<
name|SessionKey
argument_list|,
name|Session
argument_list|>
name|sessionPool
parameter_list|,
name|boolean
name|transactional
parameter_list|,
name|boolean
name|anonymous
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
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
name|transactional
expr_stmt|;
name|this
operator|.
name|useAnonymousProducers
operator|=
name|anonymous
expr_stmt|;
block|}
specifier|public
name|void
name|addSessionEventListener
parameter_list|(
name|PooledSessionEventListener
name|listener
parameter_list|)
block|{
comment|// only add if really needed
if|if
condition|(
operator|!
name|sessionEventListeners
operator|.
name|contains
argument_list|(
name|listener
argument_list|)
condition|)
block|{
name|this
operator|.
name|sessionEventListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|isIgnoreClose
parameter_list|()
block|{
return|return
name|ignoreClose
return|;
block|}
specifier|protected
name|void
name|setIgnoreClose
parameter_list|(
name|boolean
name|ignoreClose
parameter_list|)
block|{
name|this
operator|.
name|ignoreClose
operator|=
name|ignoreClose
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|ignoreClose
condition|)
block|{
return|return;
block|}
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
name|boolean
name|invalidate
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// lets reset the session
name|getInternalSession
argument_list|()
operator|.
name|setMessageListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// Close any consumers and browsers that may have been created.
for|for
control|(
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|iter
init|=
name|consumers
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageConsumer
name|consumer
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|QueueBrowser
argument_list|>
name|iter
init|=
name|browsers
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|QueueBrowser
name|browser
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|browser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|transactional
operator|&&
operator|!
name|isXa
condition|)
block|{
try|try
block|{
name|getInternalSession
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
name|invalidate
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught exception trying rollback() when putting session back into the pool, will invalidate. "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|invalidate
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught exception trying close() when putting session back into the pool, will invalidate. "
operator|+
name|ex
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|consumers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|browsers
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PooledSessionEventListener
name|listener
range|:
name|this
operator|.
name|sessionEventListeners
control|)
block|{
name|listener
operator|.
name|onSessionClosed
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|sessionEventListeners
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|invalidate
condition|)
block|{
comment|// lets close the session and not put the session back into the pool
comment|// instead invalidate it so the pool can create a new one on demand.
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
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
name|LOG
operator|.
name|trace
argument_list|(
literal|"Ignoring exception on close as discarding session: "
operator|+
name|e1
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|sessionPool
operator|.
name|invalidateObject
argument_list|(
name|key
argument_list|,
name|session
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
name|trace
argument_list|(
literal|"Ignoring exception on invalidateObject as discarding session: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|sessionPool
operator|.
name|returnObject
argument_list|(
name|key
argument_list|,
name|session
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
name|illegalStateException
init|=
operator|new
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|illegalStateException
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|illegalStateException
throw|;
block|}
block|}
name|session
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|JMSException
block|{
name|getInternalSession
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|createBytesMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|createMapMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|createMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|createMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|createObjectMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|getInternalSession
argument_list|()
operator|.
name|createObjectMessage
argument_list|(
name|serializable
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|getInternalSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|createStreamMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TemporaryQueue
name|createTemporaryQueue
parameter_list|()
throws|throws
name|JMSException
block|{
name|TemporaryQueue
name|result
decl_stmt|;
name|result
operator|=
name|getInternalSession
argument_list|()
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
comment|// Notify all of the listeners of the created temporary Queue.
for|for
control|(
name|PooledSessionEventListener
name|listener
range|:
name|this
operator|.
name|sessionEventListeners
control|)
block|{
name|listener
operator|.
name|onTemporaryQueueCreate
argument_list|(
name|result
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
name|TemporaryTopic
name|createTemporaryTopic
parameter_list|()
throws|throws
name|JMSException
block|{
name|TemporaryTopic
name|result
decl_stmt|;
name|result
operator|=
name|getInternalSession
argument_list|()
operator|.
name|createTemporaryTopic
argument_list|()
expr_stmt|;
comment|// Notify all of the listeners of the created temporary Topic.
for|for
control|(
name|PooledSessionEventListener
name|listener
range|:
name|this
operator|.
name|sessionEventListeners
control|)
block|{
name|listener
operator|.
name|onTemporaryTopicCreate
argument_list|(
name|result
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
name|unsubscribe
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|JMSException
block|{
name|getInternalSession
argument_list|()
operator|.
name|unsubscribe
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|createTextMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|getInternalSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|getInternalSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getAcknowledgeMode
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|getAcknowledgeMode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getTransacted
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|getTransacted
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recover
parameter_list|()
throws|throws
name|JMSException
block|{
name|getInternalSession
argument_list|()
operator|.
name|recover
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|JMSException
block|{
name|getInternalSession
argument_list|()
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|XAResource
name|getXAResource
parameter_list|()
block|{
if|if
condition|(
name|session
operator|instanceof
name|XASession
condition|)
block|{
return|return
operator|(
operator|(
name|XASession
operator|)
name|session
operator|)
operator|.
name|getXAResource
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
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
comment|// -------------------------------------------------------------------------
annotation|@
name|Override
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
name|addQueueBrowser
argument_list|(
name|getInternalSession
argument_list|()
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addQueueBrowser
argument_list|(
name|getInternalSession
argument_list|()
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addConsumer
argument_list|(
name|getInternalSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addConsumer
argument_list|(
name|getInternalSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|selector
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addConsumer
argument_list|(
name|getInternalSession
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
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addTopicSubscriber
argument_list|(
name|getInternalSession
argument_list|()
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|selector
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addTopicSubscriber
argument_list|(
name|getInternalSession
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
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MessageListener
name|getMessageListener
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getInternalSession
argument_list|()
operator|.
name|getMessageListener
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|getInternalSession
argument_list|()
operator|.
name|setMessageListener
argument_list|(
name|messageListener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|addTopicSubscriber
argument_list|(
operator|(
operator|(
name|TopicSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addTopicSubscriber
argument_list|(
operator|(
operator|(
name|TopicSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createSubscriber
argument_list|(
name|topic
argument_list|,
name|selector
argument_list|,
name|local
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addQueueReceiver
argument_list|(
operator|(
operator|(
name|QueueSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|addQueueReceiver
argument_list|(
operator|(
operator|(
name|QueueSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|)
argument_list|)
return|;
block|}
comment|// Producer related methods
comment|// -------------------------------------------------------------------------
annotation|@
name|Override
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
argument_list|(
name|destination
argument_list|)
argument_list|,
name|destination
argument_list|)
return|;
block|}
annotation|@
name|Override
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
argument_list|(
name|queue
argument_list|)
argument_list|,
name|queue
argument_list|)
return|;
block|}
annotation|@
name|Override
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
argument_list|(
name|topic
argument_list|)
argument_list|,
name|topic
argument_list|)
return|;
block|}
specifier|public
name|Session
name|getInternalSession
parameter_list|()
throws|throws
name|IllegalStateException
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
name|IllegalStateException
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
name|MessageProducer
name|getMessageProducer
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getMessageProducer
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|MessageProducer
name|getMessageProducer
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useAnonymousProducers
condition|)
block|{
if|if
condition|(
name|producer
operator|==
literal|null
condition|)
block|{
comment|// Don't allow for duplicate anonymous producers.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|producer
operator|==
literal|null
condition|)
block|{
name|producer
operator|=
name|getInternalSession
argument_list|()
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|=
name|producer
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|getInternalSession
argument_list|()
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|QueueSender
name|getQueueSender
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getQueueSender
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|QueueSender
name|getQueueSender
parameter_list|(
name|Queue
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|QueueSender
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useAnonymousProducers
condition|)
block|{
if|if
condition|(
name|sender
operator|==
literal|null
condition|)
block|{
comment|// Don't allow for duplicate anonymous producers.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|sender
operator|==
literal|null
condition|)
block|{
name|sender
operator|=
operator|(
operator|(
name|QueueSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createSender
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|=
name|sender
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|(
operator|(
name|QueueSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createSender
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|TopicPublisher
name|getTopicPublisher
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getTopicPublisher
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|TopicPublisher
name|getTopicPublisher
parameter_list|(
name|Topic
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|TopicPublisher
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useAnonymousProducers
condition|)
block|{
if|if
condition|(
name|publisher
operator|==
literal|null
condition|)
block|{
comment|// Don't allow for duplicate anonymous producers.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|publisher
operator|==
literal|null
condition|)
block|{
name|publisher
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createPublisher
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|=
name|publisher
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|getInternalSession
argument_list|()
operator|)
operator|.
name|createPublisher
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|QueueBrowser
name|addQueueBrowser
parameter_list|(
name|QueueBrowser
name|browser
parameter_list|)
block|{
name|browsers
operator|.
name|add
argument_list|(
name|browser
argument_list|)
expr_stmt|;
return|return
name|browser
return|;
block|}
specifier|private
name|MessageConsumer
name|addConsumer
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
name|consumers
operator|.
name|add
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
comment|// must wrap in PooledMessageConsumer to ensure the onConsumerClose
comment|// method is invoked when the returned consumer is closed, to avoid memory
comment|// leak in this session class in case many consumers is created
return|return
operator|new
name|PooledMessageConsumer
argument_list|(
name|this
argument_list|,
name|consumer
argument_list|)
return|;
block|}
specifier|private
name|TopicSubscriber
name|addTopicSubscriber
parameter_list|(
name|TopicSubscriber
name|subscriber
parameter_list|)
block|{
name|consumers
operator|.
name|add
argument_list|(
name|subscriber
argument_list|)
expr_stmt|;
return|return
name|subscriber
return|;
block|}
specifier|private
name|QueueReceiver
name|addQueueReceiver
parameter_list|(
name|QueueReceiver
name|receiver
parameter_list|)
block|{
name|consumers
operator|.
name|add
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
return|return
name|receiver
return|;
block|}
specifier|public
name|void
name|setIsXa
parameter_list|(
name|boolean
name|isXa
parameter_list|)
block|{
name|this
operator|.
name|isXa
operator|=
name|isXa
expr_stmt|;
block|}
annotation|@
name|Override
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
comment|/**      * Callback invoked when the consumer is closed.      *<p/>      * This is used to keep track of an explicit closed consumer created by this      * session, by which we know do not need to keep track of the consumer, as      * its already closed.      *      * @param consumer      *            the consumer which is being closed      */
specifier|protected
name|void
name|onConsumerClose
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
name|consumers
operator|.
name|remove
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


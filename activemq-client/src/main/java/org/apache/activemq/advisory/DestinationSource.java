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
name|advisory
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArraySet
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
name|Connection
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
name|Session
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
name|ActiveMQMessage
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
name|ActiveMQQueue
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
name|ActiveMQTempQueue
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
name|ActiveMQTempTopic
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
name|ActiveMQTopic
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
comment|/**  * A helper class which keeps track of the Destinations available in a broker and allows you to listen to them  * being created or deleted.  *  *   */
end_comment

begin_class
specifier|public
class|class
name|DestinationSource
implements|implements
name|MessageListener
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
name|ConsumerEventSource
operator|.
name|class
argument_list|)
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
specifier|final
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageConsumer
name|queueConsumer
decl_stmt|;
specifier|private
name|MessageConsumer
name|topicConsumer
decl_stmt|;
specifier|private
name|MessageConsumer
name|tempTopicConsumer
decl_stmt|;
specifier|private
name|MessageConsumer
name|tempQueueConsumer
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|queues
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|ActiveMQQueue
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|ActiveMQTopic
argument_list|>
name|topics
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|ActiveMQTopic
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|ActiveMQTempQueue
argument_list|>
name|temporaryQueues
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|ActiveMQTempQueue
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|ActiveMQTempTopic
argument_list|>
name|temporaryTopics
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|ActiveMQTempTopic
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|DestinationListener
name|listener
decl_stmt|;
specifier|public
name|DestinationSource
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|public
name|DestinationListener
name|getListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
specifier|public
name|void
name|setDestinationListener
parameter_list|(
name|DestinationListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
comment|/**      * Returns the current queues available on the broker      */
specifier|public
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|getQueues
parameter_list|()
block|{
return|return
name|queues
return|;
block|}
comment|/**      * Returns the current topics on the broker      */
specifier|public
name|Set
argument_list|<
name|ActiveMQTopic
argument_list|>
name|getTopics
parameter_list|()
block|{
return|return
name|topics
return|;
block|}
comment|/**      * Returns the current temporary topics available on the broker      */
specifier|public
name|Set
argument_list|<
name|ActiveMQTempQueue
argument_list|>
name|getTemporaryQueues
parameter_list|()
block|{
return|return
name|temporaryQueues
return|;
block|}
comment|/**      * Returns the current temporary queues available on the broker      */
specifier|public
name|Set
argument_list|<
name|ActiveMQTempTopic
argument_list|>
name|getTemporaryTopics
parameter_list|()
block|{
return|return
name|temporaryTopics
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queueConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|QUEUE_ADVISORY_TOPIC
argument_list|)
expr_stmt|;
name|queueConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|topicConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|TOPIC_ADVISORY_TOPIC
argument_list|)
expr_stmt|;
name|topicConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tempQueueConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|TEMP_QUEUE_ADVISORY_TOPIC
argument_list|)
expr_stmt|;
name|tempQueueConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tempTopicConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|TEMP_TOPIC_ADVISORY_TOPIC
argument_list|)
expr_stmt|;
name|tempTopicConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
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
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|instanceof
name|ActiveMQMessage
condition|)
block|{
name|ActiveMQMessage
name|activeMessage
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
decl_stmt|;
name|Object
name|command
init|=
name|activeMessage
operator|.
name|getDataStructure
argument_list|()
decl_stmt|;
if|if
condition|(
name|command
operator|instanceof
name|DestinationInfo
condition|)
block|{
name|DestinationInfo
name|destinationInfo
init|=
operator|(
name|DestinationInfo
operator|)
name|command
decl_stmt|;
name|DestinationEvent
name|event
init|=
operator|new
name|DestinationEvent
argument_list|(
name|this
argument_list|,
name|destinationInfo
argument_list|)
decl_stmt|;
name|fireDestinationEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown dataStructure: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown message type: "
operator|+
name|message
operator|+
literal|". Message ignored"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|fireDestinationEvent
parameter_list|(
name|DestinationEvent
name|event
parameter_list|)
block|{
comment|// now lets update the data structures
name|ActiveMQDestination
name|destination
init|=
name|event
operator|.
name|getDestination
argument_list|()
decl_stmt|;
name|boolean
name|add
init|=
name|event
operator|.
name|isAddOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQQueue
condition|)
block|{
name|ActiveMQQueue
name|queue
init|=
operator|(
name|ActiveMQQueue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|add
condition|)
block|{
name|queues
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queues
operator|.
name|remove
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQTopic
condition|)
block|{
name|ActiveMQTopic
name|topic
init|=
operator|(
name|ActiveMQTopic
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|add
condition|)
block|{
name|topics
operator|.
name|add
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|topics
operator|.
name|remove
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQTempQueue
condition|)
block|{
name|ActiveMQTempQueue
name|queue
init|=
operator|(
name|ActiveMQTempQueue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|add
condition|)
block|{
name|temporaryQueues
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|temporaryQueues
operator|.
name|remove
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQTempTopic
condition|)
block|{
name|ActiveMQTempTopic
name|topic
init|=
operator|(
name|ActiveMQTempTopic
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|add
condition|)
block|{
name|temporaryTopics
operator|.
name|add
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|temporaryTopics
operator|.
name|remove
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown destination type: "
operator|+
name|destination
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onDestinationEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


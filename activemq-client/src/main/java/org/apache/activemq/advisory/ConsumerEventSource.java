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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|AtomicInteger
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
name|Message
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
name|ActiveMQMessageConsumer
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
name|Service
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
name|RemoveInfo
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
comment|/**  * An object which can be used to listen to the number of active consumers  * available on a given destination.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ConsumerEventSource
implements|implements
name|Service
implements|,
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
specifier|final
name|Connection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|private
name|ConsumerListener
name|listener
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
name|AtomicInteger
name|consumerCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|ActiveMQMessageConsumer
name|consumer
decl_stmt|;
specifier|public
name|ConsumerEventSource
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Destination
name|destination
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
name|this
operator|.
name|destination
operator|=
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setConsumerListener
parameter_list|(
name|ConsumerListener
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
specifier|public
name|String
name|getConsumerId
parameter_list|()
block|{
return|return
name|consumer
operator|!=
literal|null
condition|?
name|consumer
operator|.
name|getConsumerId
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|"NOT_SET"
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
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
name|ActiveMQTopic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|consumer
operator|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|session
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
argument_list|)
expr_stmt|;
name|consumer
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
name|Exception
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
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|command
operator|instanceof
name|ConsumerInfo
condition|)
block|{
name|count
operator|=
name|consumerCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|count
operator|=
name|extractConsumerCountFromMessage
argument_list|(
name|message
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|fireConsumerEvent
argument_list|(
operator|new
name|ConsumerStartedEvent
argument_list|(
name|this
argument_list|,
name|destination
argument_list|,
operator|(
name|ConsumerInfo
operator|)
name|command
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|instanceof
name|RemoveInfo
condition|)
block|{
name|RemoveInfo
name|removeInfo
init|=
operator|(
name|RemoveInfo
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|removeInfo
operator|.
name|isConsumerRemove
argument_list|()
condition|)
block|{
name|count
operator|=
name|consumerCount
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|count
operator|=
name|extractConsumerCountFromMessage
argument_list|(
name|message
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|fireConsumerEvent
argument_list|(
operator|new
name|ConsumerStoppedEvent
argument_list|(
name|this
argument_list|,
name|destination
argument_list|,
operator|(
name|ConsumerId
operator|)
name|removeInfo
operator|.
name|getObjectId
argument_list|()
argument_list|,
name|count
argument_list|)
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
literal|"Unknown command: "
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
comment|/**      * Lets rely by default on the broker telling us what the consumer count is      * as it can ensure that we are up to date at all times and have not      * received messages out of order etc.      */
specifier|protected
name|int
name|extractConsumerCountFromMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|int
name|count
parameter_list|)
block|{
try|try
block|{
name|Object
name|value
init|=
name|message
operator|.
name|getObjectProperty
argument_list|(
literal|"consumerCount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|value
decl_stmt|;
return|return
name|n
operator|.
name|intValue
argument_list|()
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"No consumerCount header available on the message: "
operator|+
name|message
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
literal|"Failed to extract consumerCount from message: "
operator|+
name|message
operator|+
literal|".Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|protected
name|void
name|fireConsumerEvent
parameter_list|(
name|ConsumerEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onConsumerEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


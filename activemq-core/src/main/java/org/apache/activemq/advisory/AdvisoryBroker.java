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
name|advisory
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
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
name|broker
operator|.
name|BrokerFilter
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
name|broker
operator|.
name|ConnectionContext
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
name|broker
operator|.
name|region
operator|.
name|Destination
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
name|Command
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
name|ConnectionInfo
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
name|DestinationInfo
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
name|MessageId
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
name|util
operator|.
name|IdGenerator
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
name|LongSequenceGenerator
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * This broker filter handles tracking the state of the broker for purposes of publishing advisory messages  * to advisory consumers.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|AdvisoryBroker
extends|extends
name|BrokerFilter
block|{
comment|//private static final Log log = LogFactory.getLog(AdvisoryBroker.class);
specifier|protected
specifier|final
name|ConcurrentHashMap
name|connections
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|consumers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|producers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|destinations
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|static
specifier|final
specifier|private
name|IdGenerator
name|idGenerator
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ProducerId
name|advisoryProducerId
init|=
operator|new
name|ProducerId
argument_list|()
decl_stmt|;
specifier|final
specifier|private
name|LongSequenceGenerator
name|messageIdGenerator
init|=
operator|new
name|LongSequenceGenerator
argument_list|()
decl_stmt|;
specifier|public
name|AdvisoryBroker
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|advisoryProducerId
operator|.
name|setConnectionId
argument_list|(
name|idGenerator
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getConnectionAdvisoryTopic
argument_list|()
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|connections
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Don't advise advisory topics.
if|if
condition|(
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|fireConsumerAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We need to replay all the previously collected state objects
comment|// for this newly added consumer.
if|if
condition|(
name|AdvisorySupport
operator|.
name|isConnectionAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
comment|// Replay the connections.
for|for
control|(
name|Iterator
name|iter
init|=
name|connections
operator|.
name|values
argument_list|()
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
name|ConnectionInfo
name|value
init|=
operator|(
name|ConnectionInfo
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getConnectionAdvisoryTopic
argument_list|()
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|value
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// We need to replay all the previously collected destination objects
comment|// for this newly added consumer.
if|if
condition|(
name|AdvisorySupport
operator|.
name|isDestinationAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
comment|// Replay the destinations.
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|values
argument_list|()
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
name|DestinationInfo
name|value
init|=
operator|(
name|DestinationInfo
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getDestinationAdvisoryTopic
argument_list|(
name|value
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|value
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Replay the producers.
if|if
condition|(
name|AdvisorySupport
operator|.
name|isProducerAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|producers
operator|.
name|values
argument_list|()
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
name|ProducerInfo
name|value
init|=
operator|(
name|ProducerInfo
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getProducerAdvisoryTopic
argument_list|(
name|value
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|value
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Replay the consumers.
if|if
condition|(
name|AdvisorySupport
operator|.
name|isConsumerAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|consumers
operator|.
name|values
argument_list|()
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
name|ConsumerInfo
name|value
init|=
operator|(
name|ConsumerInfo
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|value
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|fireConsumerAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|value
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Don't advise advisory topics.
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getProducerAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|producers
operator|.
name|put
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Throwable
block|{
name|Destination
name|answer
init|=
name|next
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getDestinationAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|DestinationInfo
name|info
init|=
operator|new
name|DestinationInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|DestinationInfo
operator|.
name|ADD_OPERATION_TYPE
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|destinations
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|removeDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getDestinationAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|DestinationInfo
name|info
init|=
operator|(
name|DestinationInfo
operator|)
name|destinations
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|setOperationType
argument_list|(
name|DestinationInfo
operator|.
name|REMOVE_OPERATION_TYPE
argument_list|)
expr_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getConnectionAdvisoryTopic
argument_list|()
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
name|connections
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Don't advise advisory topics.
if|if
condition|(
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|consumers
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|fireConsumerAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
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
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|next
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Don't advise advisory topics.
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
name|ActiveMQTopic
name|topic
init|=
name|AdvisorySupport
operator|.
name|getProducerAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|info
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
name|producers
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|fireAdvisory
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQTopic
name|topic
parameter_list|,
name|Command
name|command
parameter_list|)
throws|throws
name|Throwable
block|{
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|command
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|fireAdvisory
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQTopic
name|topic
parameter_list|,
name|Command
name|command
parameter_list|,
name|ConsumerId
name|targetConsumerId
parameter_list|)
throws|throws
name|Throwable
block|{
name|ActiveMQMessage
name|advisoryMessage
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|command
argument_list|,
name|targetConsumerId
argument_list|,
name|advisoryMessage
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|fireConsumerAdvisory
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQTopic
name|topic
parameter_list|,
name|Command
name|command
parameter_list|)
throws|throws
name|Throwable
block|{
name|fireConsumerAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|command
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|fireConsumerAdvisory
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQTopic
name|topic
parameter_list|,
name|Command
name|command
parameter_list|,
name|ConsumerId
name|targetConsumerId
parameter_list|)
throws|throws
name|Throwable
block|{
name|ActiveMQMessage
name|advisoryMessage
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|advisoryMessage
operator|.
name|setIntProperty
argument_list|(
literal|"consumerCount"
argument_list|,
name|consumers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|fireAdvisory
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|command
argument_list|,
name|targetConsumerId
argument_list|,
name|advisoryMessage
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|fireAdvisory
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQTopic
name|topic
parameter_list|,
name|Command
name|command
parameter_list|,
name|ConsumerId
name|targetConsumerId
parameter_list|,
name|ActiveMQMessage
name|advisoryMessage
parameter_list|)
throws|throws
name|Throwable
block|{
name|advisoryMessage
operator|.
name|setDataStructure
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setType
argument_list|(
name|AdvisorySupport
operator|.
name|ADIVSORY_MESSAGE_TYPE
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|advisoryProducerId
argument_list|,
name|messageIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setTargetConsumerId
argument_list|(
name|targetConsumerId
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setDestination
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|advisoryMessage
operator|.
name|setProducerId
argument_list|(
name|advisoryProducerId
argument_list|)
expr_stmt|;
name|boolean
name|originalFlowControl
init|=
name|context
operator|.
name|isProducerFlowControl
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|next
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|advisoryMessage
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
name|originalFlowControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


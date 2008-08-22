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
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
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
name|Queue
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
name|Message
import|;
end_import

begin_comment
comment|/**  * Provides a JMX Management view of a Queue.  */
end_comment

begin_class
specifier|public
class|class
name|QueueView
extends|extends
name|DestinationView
implements|implements
name|QueueViewMBean
block|{
specifier|public
name|QueueView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|Queue
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CompositeData
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|Message
name|rc
init|=
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|getMessage
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|OpenTypeSupport
operator|.
name|convert
argument_list|(
name|rc
argument_list|)
return|;
block|}
specifier|public
name|void
name|purge
parameter_list|()
throws|throws
name|Exception
block|{
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|removeMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|removeMessage
argument_list|(
name|messageId
argument_list|)
return|;
block|}
specifier|public
name|int
name|removeMatchingMessages
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|removeMatchingMessages
argument_list|(
name|selector
argument_list|)
return|;
block|}
specifier|public
name|int
name|removeMatchingMessages
parameter_list|(
name|String
name|selector
parameter_list|,
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|removeMatchingMessages
argument_list|(
name|selector
argument_list|,
name|maximumMessages
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|copyMessageTo
parameter_list|(
name|String
name|messageId
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|toDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|copyMessageTo
argument_list|(
name|context
argument_list|,
name|messageId
argument_list|,
name|toDestination
argument_list|)
return|;
block|}
specifier|public
name|int
name|copyMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|toDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|copyMatchingMessagesTo
argument_list|(
name|context
argument_list|,
name|selector
argument_list|,
name|toDestination
argument_list|)
return|;
block|}
specifier|public
name|int
name|copyMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|toDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|copyMatchingMessagesTo
argument_list|(
name|context
argument_list|,
name|selector
argument_list|,
name|toDestination
argument_list|,
name|maximumMessages
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|moveMessageTo
parameter_list|(
name|String
name|messageId
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|toDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|moveMessageTo
argument_list|(
name|context
argument_list|,
name|messageId
argument_list|,
name|toDestination
argument_list|)
return|;
block|}
specifier|public
name|int
name|moveMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|toDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|moveMatchingMessagesTo
argument_list|(
name|context
argument_list|,
name|selector
argument_list|,
name|toDestination
argument_list|)
return|;
block|}
specifier|public
name|int
name|moveMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|toDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|moveMatchingMessagesTo
argument_list|(
name|context
argument_list|,
name|selector
argument_list|,
name|toDestination
argument_list|,
name|maximumMessages
argument_list|)
return|;
block|}
comment|/**      * Moves a message back to its original destination      */
specifier|public
name|boolean
name|retryMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
name|Message
name|rc
init|=
name|queue
operator|.
name|getMessage
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|originalDestination
init|=
name|rc
operator|.
name|getOriginalDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|originalDestination
operator|!=
literal|null
condition|)
block|{
name|ConnectionContext
name|context
init|=
name|BrokerView
operator|.
name|getConnectionContext
argument_list|(
name|broker
operator|.
name|getContextBroker
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|queue
operator|.
name|moveMessageTo
argument_list|(
name|context
argument_list|,
name|messageId
argument_list|,
name|originalDestination
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|cursorSize
parameter_list|()
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|doesCursorHaveMessagesBuffered
parameter_list|()
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|hasMessagesBufferedToDeliver
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|doesCursorHaveSpace
parameter_list|()
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|hasSpace
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|long
name|getCursorMemoryUsage
parameter_list|()
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
operator|&&
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getCursorPercentUsage
parameter_list|()
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
operator|&&
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isCursorFull
parameter_list|()
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|queue
operator|.
name|getMessages
argument_list|()
operator|.
name|isFull
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit


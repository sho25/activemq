begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|region
operator|.
name|policy
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
name|AtomicLong
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
name|ActiveMQMessageTransformation
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|Subscription
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
name|SubscriptionRecovery
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
name|Topic
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
name|ConnectionId
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
name|SessionId
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
name|filter
operator|.
name|MessageEvaluationContext
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

begin_comment
comment|/**  * This implementation of {@link SubscriptionRecoveryPolicy} will perform a user  * specific query mechanism to load any messages they may have missed.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|QueryBasedSubscriptionRecoveryPolicy
implements|implements
name|SubscriptionRecoveryPolicy
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|QueryBasedSubscriptionRecoveryPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|MessageQuery
name|query
decl_stmt|;
specifier|private
name|AtomicLong
name|messageSequence
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|IdGenerator
name|idGenerator
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|private
name|ProducerId
name|producerId
init|=
name|createProducerId
argument_list|()
decl_stmt|;
specifier|public
name|SubscriptionRecoveryPolicy
name|copy
parameter_list|()
block|{
name|QueryBasedSubscriptionRecoveryPolicy
name|rc
init|=
operator|new
name|QueryBasedSubscriptionRecoveryPolicy
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|boolean
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|query
operator|.
name|validateUpdate
argument_list|(
name|message
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|recover
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Topic
name|topic
parameter_list|,
specifier|final
name|SubscriptionRecovery
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|sub
operator|.
name|getActiveMQDestination
argument_list|()
decl_stmt|;
name|query
operator|.
name|execute
argument_list|(
name|destination
argument_list|,
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|dispatchInitialMessage
argument_list|(
name|message
argument_list|,
name|topic
argument_list|,
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No query property configured"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|MessageQuery
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
comment|/**      * Sets the query strategy to load initial messages      */
specifier|public
name|void
name|setQuery
parameter_list|(
name|MessageQuery
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
specifier|public
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
index|[]
name|browse
parameter_list|(
name|ActiveMQDestination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
index|[
literal|0
index|]
return|;
block|}
specifier|protected
name|void
name|dispatchInitialMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|Destination
name|regionDestination
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|SubscriptionRecovery
name|sub
parameter_list|)
block|{
try|try
block|{
name|ActiveMQMessage
name|activeMessage
init|=
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|activeMessage
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|destination
operator|=
name|sub
operator|.
name|getActiveMQDestination
argument_list|()
expr_stmt|;
name|activeMessage
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|activeMessage
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
name|configure
argument_list|(
name|activeMessage
argument_list|)
expr_stmt|;
name|sub
operator|.
name|addRecoveredMessage
argument_list|(
name|context
argument_list|,
name|activeMessage
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to dispatch initial message: "
operator|+
name|message
operator|+
literal|" into subscription. Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|configure
parameter_list|(
name|ActiveMQMessage
name|msg
parameter_list|)
block|{
name|long
name|sequenceNumber
init|=
name|messageSequence
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|producerId
argument_list|,
name|sequenceNumber
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|msg
operator|.
name|setProducerId
argument_list|(
name|producerId
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ProducerId
name|createProducerId
parameter_list|()
block|{
name|String
name|id
init|=
name|idGenerator
operator|.
name|generateId
argument_list|()
decl_stmt|;
name|ConnectionId
name|connectionId
init|=
operator|new
name|ConnectionId
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|SessionId
name|sessionId
init|=
operator|new
name|SessionId
argument_list|(
name|connectionId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|ProducerId
argument_list|(
name|sessionId
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit


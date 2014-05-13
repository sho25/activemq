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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Message
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
name|DestinationFilter
import|;
end_import

begin_comment
comment|/**  * This implementation of {@link org.apache.activemq.broker.region.policy.SubscriptionRecoveryPolicy} will only keep the  * last non-zero length message with the {@link org.apache.activemq.command.ActiveMQMessage}.RETAIN_PROPERTY.  *  * @org.apache.xbean.XBean  *  */
end_comment

begin_class
specifier|public
class|class
name|RetainedMessageSubscriptionRecoveryPolicy
implements|implements
name|SubscriptionRecoveryPolicy
block|{
specifier|public
specifier|static
specifier|final
name|String
name|RETAIN_PROPERTY
init|=
literal|"ActiveMQ.Retain"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|RETAINED_PROPERTY
init|=
literal|"ActiveMQ.Retained"
decl_stmt|;
specifier|private
specifier|volatile
name|MessageReference
name|retainedMessage
decl_stmt|;
specifier|private
name|SubscriptionRecoveryPolicy
name|wrapped
decl_stmt|;
specifier|public
name|RetainedMessageSubscriptionRecoveryPolicy
parameter_list|(
name|SubscriptionRecoveryPolicy
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
specifier|public
name|boolean
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Message
name|message
init|=
name|node
operator|.
name|getMessage
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|retainValue
init|=
name|message
operator|.
name|getProperty
argument_list|(
name|RETAIN_PROPERTY
argument_list|)
decl_stmt|;
comment|// retain property set to true
specifier|final
name|boolean
name|retain
init|=
name|retainValue
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|retainValue
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|retain
condition|)
block|{
if|if
condition|(
name|message
operator|.
name|getContent
argument_list|()
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// non zero length message content
name|retainedMessage
operator|=
name|message
operator|.
name|copy
argument_list|()
expr_stmt|;
name|retainedMessage
operator|.
name|getMessage
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|RETAIN_PROPERTY
argument_list|)
expr_stmt|;
name|retainedMessage
operator|.
name|getMessage
argument_list|()
operator|.
name|setProperty
argument_list|(
name|RETAINED_PROPERTY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// clear retained message
name|retainedMessage
operator|=
literal|null
expr_stmt|;
block|}
comment|// TODO should we remove the publisher's retain property??
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|RETAIN_PROPERTY
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapped
operator|==
literal|null
condition|?
literal|true
else|:
name|wrapped
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|public
name|void
name|recover
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Topic
name|topic
parameter_list|,
name|SubscriptionRecovery
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Re-dispatch the last retained message seen.
if|if
condition|(
name|retainedMessage
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|addRecoveredMessage
argument_list|(
name|context
argument_list|,
name|retainedMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wrapped
operator|!=
literal|null
condition|)
block|{
name|wrapped
operator|.
name|recover
argument_list|(
name|context
argument_list|,
name|topic
argument_list|,
name|sub
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
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|Message
index|[]
name|browse
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Message
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|retainedMessage
operator|!=
literal|null
condition|)
block|{
name|DestinationFilter
name|filter
init|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|(
name|retainedMessage
operator|.
name|getMessage
argument_list|()
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|retainedMessage
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Message
index|[]
name|messages
init|=
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|Message
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapped
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Message
index|[]
name|wrappedMessages
init|=
name|wrapped
operator|.
name|browse
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrappedMessages
operator|!=
literal|null
operator|&&
name|wrappedMessages
operator|.
name|length
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|origLen
init|=
name|messages
operator|.
name|length
decl_stmt|;
name|messages
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|messages
argument_list|,
name|origLen
operator|+
name|wrappedMessages
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|wrappedMessages
argument_list|,
literal|0
argument_list|,
name|messages
argument_list|,
name|origLen
argument_list|,
name|wrappedMessages
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|messages
return|;
block|}
specifier|public
name|SubscriptionRecoveryPolicy
name|copy
parameter_list|()
block|{
return|return
operator|new
name|RetainedMessageSubscriptionRecoveryPolicy
argument_list|(
name|wrapped
argument_list|)
return|;
block|}
specifier|public
name|void
name|setBroker
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{             }
specifier|public
name|void
name|setWrapped
parameter_list|(
name|SubscriptionRecoveryPolicy
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
block|}
end_class

end_unit

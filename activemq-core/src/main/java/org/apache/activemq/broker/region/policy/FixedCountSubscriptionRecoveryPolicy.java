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
comment|/**  * This implementation of {@link SubscriptionRecoveryPolicy} will keep a fixed  * count of last messages.  *   * @org.apache.xbean.XBean  *   */
end_comment

begin_class
specifier|public
class|class
name|FixedCountSubscriptionRecoveryPolicy
implements|implements
name|SubscriptionRecoveryPolicy
block|{
specifier|private
specifier|volatile
name|MessageReference
name|messages
index|[]
decl_stmt|;
specifier|private
name|int
name|maximumSize
init|=
literal|100
decl_stmt|;
specifier|private
name|int
name|tail
decl_stmt|;
specifier|public
name|SubscriptionRecoveryPolicy
name|copy
parameter_list|()
block|{
name|FixedCountSubscriptionRecoveryPolicy
name|rc
init|=
operator|new
name|FixedCountSubscriptionRecoveryPolicy
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setMaximumSize
argument_list|(
name|maximumSize
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
specifier|synchronized
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
name|messages
index|[
name|tail
operator|++
index|]
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|tail
operator|>=
name|messages
operator|.
name|length
condition|)
block|{
name|tail
operator|=
literal|0
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|synchronized
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
comment|// Re-dispatch the last message seen.
name|int
name|t
init|=
name|tail
decl_stmt|;
comment|// The buffer may not have rolled over yet..., start from the front
if|if
condition|(
name|messages
index|[
name|t
index|]
operator|==
literal|null
condition|)
block|{
name|t
operator|=
literal|0
expr_stmt|;
block|}
comment|// Well the buffer is really empty then.
if|if
condition|(
name|messages
index|[
name|t
index|]
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Keep dispatching until t hit's tail again.
do|do
block|{
name|MessageReference
name|node
init|=
name|messages
index|[
name|t
index|]
decl_stmt|;
name|sub
operator|.
name|addRecoveredMessage
argument_list|(
name|context
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|t
operator|++
expr_stmt|;
if|if
condition|(
name|t
operator|>=
name|messages
operator|.
name|length
condition|)
block|{
name|t
operator|=
literal|0
expr_stmt|;
block|}
block|}
do|while
condition|(
name|t
operator|!=
name|tail
condition|)
do|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|messages
operator|=
operator|new
name|MessageReference
index|[
name|maximumSize
index|]
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|messages
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|int
name|getMaximumSize
parameter_list|()
block|{
return|return
name|maximumSize
return|;
block|}
comment|/**      * Sets the maximum number of messages that this destination will hold      * around in RAM      */
specifier|public
name|void
name|setMaximumSize
parameter_list|(
name|int
name|maximumSize
parameter_list|)
block|{
name|this
operator|.
name|maximumSize
operator|=
name|maximumSize
expr_stmt|;
block|}
specifier|public
specifier|synchronized
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
name|int
name|t
init|=
name|tail
decl_stmt|;
if|if
condition|(
name|messages
index|[
name|t
index|]
operator|==
literal|null
condition|)
block|{
name|t
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|messages
index|[
name|t
index|]
operator|!=
literal|null
condition|)
block|{
do|do
block|{
name|MessageReference
name|ref
init|=
name|messages
index|[
name|t
index|]
decl_stmt|;
name|Message
name|message
init|=
name|ref
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|(
name|message
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
name|message
argument_list|)
expr_stmt|;
block|}
name|t
operator|++
expr_stmt|;
if|if
condition|(
name|t
operator|>=
name|messages
operator|.
name|length
condition|)
block|{
name|t
operator|=
literal|0
expr_stmt|;
block|}
block|}
do|while
condition|(
name|t
operator|!=
name|tail
condition|)
do|;
block|}
return|return
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
block|}
end_class

end_unit


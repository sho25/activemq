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
name|Iterator
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

begin_comment
comment|/**  * Simple dispatch policy that sends a message to every subscription that  * matches the message.  *   * @org.apache.xbean.XBean  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|RoundRobinDispatchPolicy
implements|implements
name|DispatchPolicy
block|{
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RoundRobinDispatchPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @param node      * @param msgContext      * @param consumers      * @return true if dispatched      * @throws Exception      * @see org.apache.activemq.broker.region.policy.DispatchPolicy#dispatch(org.apache.activemq.broker.region.MessageReference,      *      org.apache.activemq.filter.MessageEvaluationContext, java.util.List)      */
specifier|public
name|boolean
name|dispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|msgContext
parameter_list|,
name|List
name|consumers
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Big synch here so that only 1 message gets dispatched at a time.
comment|// Ensures
comment|// Everyone sees the same order and that the consumer list is not used
comment|// while
comment|// it's being rotated.
synchronized|synchronized
init|(
name|consumers
init|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Subscription
name|firstMatchingConsumer
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
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
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// Only dispatch to interested subscriptions
if|if
condition|(
operator|!
name|sub
operator|.
name|matches
argument_list|(
name|node
argument_list|,
name|msgContext
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|firstMatchingConsumer
operator|==
literal|null
condition|)
block|{
name|firstMatchingConsumer
operator|=
name|sub
expr_stmt|;
block|}
name|sub
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|firstMatchingConsumer
operator|!=
literal|null
condition|)
block|{
comment|// Rotate the consumer list.
try|try
block|{
name|consumers
operator|.
name|remove
argument_list|(
name|firstMatchingConsumer
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|add
argument_list|(
name|firstMatchingConsumer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|bestEffort
parameter_list|)
block|{                 }
block|}
return|return
name|count
operator|>
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit


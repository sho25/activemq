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
name|broker
operator|.
name|policy
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
name|broker
operator|.
name|QueueSubscriptionTest
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
name|BrokerService
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
name|policy
operator|.
name|PolicyEntry
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
name|policy
operator|.
name|PolicyMap
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
name|policy
operator|.
name|SimpleDispatchPolicy
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
name|MessageIdList
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
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleDispatchPolicyTest
extends|extends
name|QueueSubscriptionTest
block|{
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setDispatchPolicy
argument_list|(
operator|new
name|SimpleDispatchPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersSmallMessagesLargePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerTwoConsumersSmallMessagesLargePrefetch
argument_list|()
expr_stmt|;
comment|// One consumer should have received all messages, and the rest none
name|assertOneConsumerReceivedAllMessages
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersLargeMessagesLargePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerTwoConsumersLargeMessagesLargePrefetch
argument_list|()
expr_stmt|;
comment|// One consumer should have received all messages, and the rest none
name|assertOneConsumerReceivedAllMessages
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertOneConsumerReceivedAllMessages
parameter_list|(
name|int
name|messageCount
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageIdList
name|messageIdList
init|=
operator|(
name|MessageIdList
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|messageIdList
operator|.
name|getMessageCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|found
condition|)
block|{
name|fail
argument_list|(
literal|"No other consumers should have received any messages"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Consumer should have received all messages."
argument_list|,
name|messageCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|fail
argument_list|(
literal|"At least one consumer should have received all messages"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


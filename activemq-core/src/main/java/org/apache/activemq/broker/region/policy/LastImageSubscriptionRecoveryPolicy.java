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
comment|/**  * This implementation of {@link SubscriptionRecoveryPolicy} will only keep the  * last message.  *   * @org.apache.xbean.XBean  *   */
end_comment

begin_class
specifier|public
class|class
name|LastImageSubscriptionRecoveryPolicy
implements|implements
name|SubscriptionRecoveryPolicy
block|{
specifier|private
specifier|volatile
name|MessageReference
name|lastImage
decl_stmt|;
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
name|lastImage
operator|=
name|node
expr_stmt|;
return|return
literal|true
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
comment|// Re-dispatch the last message seen.
name|MessageReference
name|node
init|=
name|lastImage
decl_stmt|;
if|if
condition|(
name|node
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
name|node
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
name|lastImage
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
name|lastImage
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
name|lastImage
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|SubscriptionRecoveryPolicy
name|copy
parameter_list|()
block|{
return|return
operator|new
name|LastImageSubscriptionRecoveryPolicy
argument_list|()
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


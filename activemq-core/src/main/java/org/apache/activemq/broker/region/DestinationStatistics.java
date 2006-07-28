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
name|management
operator|.
name|CountStatisticImpl
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
name|management
operator|.
name|PollCountStatisticImpl
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
name|management
operator|.
name|StatsImpl
import|;
end_import

begin_comment
comment|/**  * The J2EE Statistics for the a Destination.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DestinationStatistics
extends|extends
name|StatsImpl
block|{
specifier|protected
name|CountStatisticImpl
name|enqueues
decl_stmt|;
specifier|protected
name|CountStatisticImpl
name|dequeues
decl_stmt|;
specifier|protected
name|CountStatisticImpl
name|consumers
decl_stmt|;
specifier|protected
name|CountStatisticImpl
name|messages
decl_stmt|;
specifier|protected
name|PollCountStatisticImpl
name|messagesCached
decl_stmt|;
specifier|public
name|DestinationStatistics
parameter_list|()
block|{
name|enqueues
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"enqueues"
argument_list|,
literal|"The number of messages that have been sent to the destination"
argument_list|)
expr_stmt|;
name|dequeues
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"dequeues"
argument_list|,
literal|"The number of messages that have been dispatched from the destination"
argument_list|)
expr_stmt|;
name|consumers
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"consumers"
argument_list|,
literal|"The number of consumers that that are subscribing to messages from the destination"
argument_list|)
expr_stmt|;
name|messages
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"messages"
argument_list|,
literal|"The number of messages that that are being held by the destination"
argument_list|)
expr_stmt|;
name|messagesCached
operator|=
operator|new
name|PollCountStatisticImpl
argument_list|(
literal|"messagesCached"
argument_list|,
literal|"The number of messages that are held in the destination's memory cache"
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"enqueues"
argument_list|,
name|enqueues
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"dequeues"
argument_list|,
name|dequeues
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"consumers"
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"messages"
argument_list|,
name|messages
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"messagesCached"
argument_list|,
name|messagesCached
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CountStatisticImpl
name|getEnqueues
parameter_list|()
block|{
return|return
name|enqueues
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getDequeues
parameter_list|()
block|{
return|return
name|dequeues
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getConsumers
parameter_list|()
block|{
return|return
name|consumers
return|;
block|}
specifier|public
name|PollCountStatisticImpl
name|getMessagesCached
parameter_list|()
block|{
return|return
name|messagesCached
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getMessages
parameter_list|()
block|{
return|return
name|messages
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|enqueues
operator|.
name|reset
argument_list|()
expr_stmt|;
name|dequeues
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setParent
parameter_list|(
name|DestinationStatistics
name|parent
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|enqueues
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|enqueues
argument_list|)
expr_stmt|;
name|dequeues
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|dequeues
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|consumers
argument_list|)
expr_stmt|;
name|messagesCached
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|messagesCached
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|messages
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|enqueues
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|dequeues
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|messagesCached
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setMessagesCached
parameter_list|(
name|PollCountStatisticImpl
name|messagesCached
parameter_list|)
block|{
name|this
operator|.
name|messagesCached
operator|=
name|messagesCached
expr_stmt|;
block|}
comment|/**      * Called when a message is enqueued to update the statistics.      */
specifier|public
name|void
name|onMessageEnqueue
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|getEnqueues
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
name|getMessages
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onMessageDequeue
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|getDequeues
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit


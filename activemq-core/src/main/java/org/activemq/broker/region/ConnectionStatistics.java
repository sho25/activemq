begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|management
operator|.
name|StatsImpl
import|;
end_import

begin_comment
comment|/**  * The J2EE Statistics for the Connection.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionStatistics
extends|extends
name|StatsImpl
block|{
specifier|private
name|CountStatisticImpl
name|enqueues
decl_stmt|;
specifier|private
name|CountStatisticImpl
name|dequeues
decl_stmt|;
specifier|public
name|ConnectionStatistics
parameter_list|()
block|{
name|enqueues
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"enqueues"
argument_list|,
literal|"The number of messages that have been sent to the connection"
argument_list|)
expr_stmt|;
name|dequeues
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"dequeues"
argument_list|,
literal|"The number of messages that have been dispatched from the connection"
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
name|ConnectorStatistics
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
name|getEnqueues
argument_list|()
argument_list|)
expr_stmt|;
name|dequeues
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|getDequeues
argument_list|()
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
block|}
block|}
comment|/**      * Updates the statistics as a command is dispatched into the connection      */
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|.
name|isMessageDispatch
argument_list|()
condition|)
block|{
name|enqueues
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onMessageDequeue
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|dequeues
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit


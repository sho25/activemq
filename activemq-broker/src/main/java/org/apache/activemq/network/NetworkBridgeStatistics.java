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
name|network
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
name|StatsImpl
import|;
end_import

begin_comment
comment|/**  * The Statistics for a NetworkBridge.  */
end_comment

begin_class
specifier|public
class|class
name|NetworkBridgeStatistics
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
name|receivedCount
decl_stmt|;
specifier|public
name|NetworkBridgeStatistics
parameter_list|()
block|{
name|enqueues
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"enqueues"
argument_list|,
literal|"The current number of enqueues this bridge has, which is the number of potential messages to be forwarded."
argument_list|)
expr_stmt|;
name|dequeues
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"dequeues"
argument_list|,
literal|"The current number of dequeues this bridge has, which is the number of messages received by the remote broker."
argument_list|)
expr_stmt|;
name|receivedCount
operator|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"receivedCount"
argument_list|,
literal|"The number of messages that have been received by the NetworkBridge from the remote broker.  Only applies for Duplex bridges."
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
literal|"receivedCount"
argument_list|,
name|receivedCount
argument_list|)
expr_stmt|;
block|}
comment|/**      * The current number of enqueues this bridge has, which is the number of potential messages to be forwarded      * Messages may not be forwarded if there is no subscription      *      * @return      */
specifier|public
name|CountStatisticImpl
name|getEnqueues
parameter_list|()
block|{
return|return
name|enqueues
return|;
block|}
comment|/**      * The current number of dequeues this bridge has, which is the number of      * messages actually sent to and received by the remote broker.      *      * @return      */
specifier|public
name|CountStatisticImpl
name|getDequeues
parameter_list|()
block|{
return|return
name|dequeues
return|;
block|}
comment|/**      * The number of messages that have been received by the NetworkBridge from the remote broker.      * Only applies for Duplex bridges.      *      * @return      */
specifier|public
name|CountStatisticImpl
name|getReceivedCount
parameter_list|()
block|{
return|return
name|receivedCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|isDoReset
argument_list|()
condition|)
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
name|receivedCount
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|super
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|enqueues
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|dequeues
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|receivedCount
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setParent
parameter_list|(
name|NetworkBridgeStatistics
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
name|receivedCount
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|receivedCount
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
name|receivedCount
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


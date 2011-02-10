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
name|management
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|IndentPrinter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Statistics for a JMS endpoint, typically a MessageProducer or MessageConsumer  * but this class can also be used to represent statistics on a  * {@link Destination} as well.  *   * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JMSEndpointStatsImpl
extends|extends
name|StatsImpl
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JMSEndpointStatsImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|CountStatisticImpl
name|messageCount
decl_stmt|;
specifier|protected
name|CountStatisticImpl
name|pendingMessageCount
decl_stmt|;
specifier|protected
name|CountStatisticImpl
name|expiredMessageCount
decl_stmt|;
specifier|protected
name|TimeStatisticImpl
name|messageWaitTime
decl_stmt|;
specifier|protected
name|TimeStatisticImpl
name|messageRateTime
decl_stmt|;
comment|/**      * This constructor is used to create statistics for a      * {@link MessageProducer} or {@link MessageConsumer} as it passes in a      * {@link Session} parent statistic.      *       * @param sessionStats      */
specifier|public
name|JMSEndpointStatsImpl
parameter_list|(
name|JMSSessionStatsImpl
name|sessionStats
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setParent
argument_list|(
name|messageCount
argument_list|,
name|sessionStats
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|setParent
argument_list|(
name|pendingMessageCount
argument_list|,
name|sessionStats
operator|.
name|getPendingMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|setParent
argument_list|(
name|expiredMessageCount
argument_list|,
name|sessionStats
operator|.
name|getExpiredMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|setParent
argument_list|(
name|messageWaitTime
argument_list|,
name|sessionStats
operator|.
name|getMessageWaitTime
argument_list|()
argument_list|)
expr_stmt|;
name|setParent
argument_list|(
name|messageRateTime
argument_list|,
name|sessionStats
operator|.
name|getMessageRateTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * This constructor is typically used to create a statistics object for a      * {@link Destination}      */
specifier|public
name|JMSEndpointStatsImpl
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|CountStatisticImpl
argument_list|(
literal|"messageCount"
argument_list|,
literal|"Number of messages processed"
argument_list|)
argument_list|,
operator|new
name|CountStatisticImpl
argument_list|(
literal|"pendingMessageCount"
argument_list|,
literal|"Number of pending messages"
argument_list|)
argument_list|,
operator|new
name|CountStatisticImpl
argument_list|(
literal|"expiredMessageCount"
argument_list|,
literal|"Number of expired messages"
argument_list|)
argument_list|,
operator|new
name|TimeStatisticImpl
argument_list|(
literal|"messageWaitTime"
argument_list|,
literal|"Time spent by a message before being delivered"
argument_list|)
argument_list|,
operator|new
name|TimeStatisticImpl
argument_list|(
literal|"messageRateTime"
argument_list|,
literal|"Time taken to process a message (thoughtput rate)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JMSEndpointStatsImpl
parameter_list|(
name|CountStatisticImpl
name|messageCount
parameter_list|,
name|CountStatisticImpl
name|pendingMessageCount
parameter_list|,
name|CountStatisticImpl
name|expiredMessageCount
parameter_list|,
name|TimeStatisticImpl
name|messageWaitTime
parameter_list|,
name|TimeStatisticImpl
name|messageRateTime
parameter_list|)
block|{
name|this
operator|.
name|messageCount
operator|=
name|messageCount
expr_stmt|;
name|this
operator|.
name|pendingMessageCount
operator|=
name|pendingMessageCount
expr_stmt|;
name|this
operator|.
name|expiredMessageCount
operator|=
name|expiredMessageCount
expr_stmt|;
name|this
operator|.
name|messageWaitTime
operator|=
name|messageWaitTime
expr_stmt|;
name|this
operator|.
name|messageRateTime
operator|=
name|messageRateTime
expr_stmt|;
comment|// lets add named stats
name|addStatistic
argument_list|(
literal|"messageCount"
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"pendingMessageCount"
argument_list|,
name|pendingMessageCount
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"expiredMessageCount"
argument_list|,
name|expiredMessageCount
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"messageWaitTime"
argument_list|,
name|messageWaitTime
argument_list|)
expr_stmt|;
name|addStatistic
argument_list|(
literal|"messageRateTime"
argument_list|,
name|messageRateTime
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|messageCount
operator|.
name|reset
argument_list|()
expr_stmt|;
name|messageRateTime
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pendingMessageCount
operator|.
name|reset
argument_list|()
expr_stmt|;
name|expiredMessageCount
operator|.
name|reset
argument_list|()
expr_stmt|;
name|messageWaitTime
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|CountStatisticImpl
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getPendingMessageCount
parameter_list|()
block|{
return|return
name|pendingMessageCount
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getExpiredMessageCount
parameter_list|()
block|{
return|return
name|expiredMessageCount
return|;
block|}
specifier|public
name|TimeStatisticImpl
name|getMessageRateTime
parameter_list|()
block|{
return|return
name|messageRateTime
return|;
block|}
specifier|public
name|TimeStatisticImpl
name|getMessageWaitTime
parameter_list|()
block|{
return|return
name|messageWaitTime
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|messageRateTime
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|pendingMessageCount
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|expiredMessageCount
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|messageWaitTime
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|()
block|{
if|if
condition|(
name|enabled
condition|)
block|{
name|long
name|start
init|=
name|messageCount
operator|.
name|getLastSampleTime
argument_list|()
decl_stmt|;
name|messageCount
operator|.
name|increment
argument_list|()
expr_stmt|;
name|long
name|end
init|=
name|messageCount
operator|.
name|getLastSampleTime
argument_list|()
decl_stmt|;
name|messageRateTime
operator|.
name|addTime
argument_list|(
name|end
operator|-
name|start
argument_list|)
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
name|messageCount
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|messageRateTime
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|pendingMessageCount
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|expiredMessageCount
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|messageWaitTime
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|IndentPrinter
name|out
parameter_list|)
block|{
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|messageRateTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|pendingMessageCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|messageRateTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|expiredMessageCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|messageWaitTime
argument_list|)
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|setParent
parameter_list|(
name|CountStatisticImpl
name|child
parameter_list|,
name|CountStatisticImpl
name|parent
parameter_list|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|CountStatisticImpl
operator|&&
name|parent
operator|instanceof
name|CountStatisticImpl
condition|)
block|{
name|CountStatisticImpl
name|c
init|=
operator|(
name|CountStatisticImpl
operator|)
name|child
decl_stmt|;
name|c
operator|.
name|setParent
argument_list|(
operator|(
name|CountStatisticImpl
operator|)
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot associate endpoint counters with session level counters as they are not both CountStatisticImpl clases. Endpoint: "
operator|+
name|child
operator|+
literal|" session: "
operator|+
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setParent
parameter_list|(
name|TimeStatisticImpl
name|child
parameter_list|,
name|TimeStatisticImpl
name|parent
parameter_list|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|TimeStatisticImpl
operator|&&
name|parent
operator|instanceof
name|TimeStatisticImpl
condition|)
block|{
name|TimeStatisticImpl
name|c
init|=
operator|(
name|TimeStatisticImpl
operator|)
name|child
decl_stmt|;
name|c
operator|.
name|setParent
argument_list|(
operator|(
name|TimeStatisticImpl
operator|)
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot associate endpoint counters with session level counters as they are not both TimeStatisticImpl clases. Endpoint: "
operator|+
name|child
operator|+
literal|" session: "
operator|+
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


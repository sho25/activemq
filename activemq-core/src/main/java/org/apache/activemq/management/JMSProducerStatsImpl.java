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

begin_comment
comment|/**  * Statistics for a JMS producer  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|JMSProducerStatsImpl
extends|extends
name|JMSEndpointStatsImpl
block|{
specifier|private
name|String
name|destination
decl_stmt|;
specifier|public
name|JMSProducerStatsImpl
parameter_list|(
name|JMSSessionStatsImpl
name|sessionStats
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|sessionStats
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|JMSProducerStatsImpl
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
parameter_list|,
name|String
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|messageCount
argument_list|,
name|pendingMessageCount
argument_list|,
name|expiredMessageCount
argument_list|,
name|messageWaitTime
argument_list|,
name|messageRateTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|String
name|getDestination
parameter_list|()
block|{
return|return
name|destination
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
literal|"producer "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" { "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" }"
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
name|print
argument_list|(
literal|"producer "
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|incrementIndent
argument_list|()
expr_stmt|;
name|super
operator|.
name|dump
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|decrementIndent
argument_list|()
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
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
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
name|Message
import|;
end_import

begin_comment
comment|/**  * Statistics for a {@link javax.jms.Topic}  *  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JMSTopicStatsImpl
extends|extends
name|JMSEndpointStatsImpl
implements|implements
name|JMSDestinationStats
block|{
specifier|public
name|JMSTopicStatsImpl
parameter_list|()
block|{     }
specifier|public
name|void
name|setPendingMessageCountOnStartup
parameter_list|(
name|long
name|count
parameter_list|)
block|{
comment|// we don't calculate pending counts for topics
block|}
specifier|public
name|void
name|onMessageSend
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|onMessage
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onMessageAck
parameter_list|()
block|{
comment|// we don't calculate pending counts for topics
block|}
block|}
end_class

end_unit


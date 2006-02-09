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
name|region
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
name|ActiveMQQueue
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
name|ActiveMQTopic
import|;
end_import

begin_comment
comment|/**  * A {@link DeadLetterStrategy} where each destination has its own individual  * DLQ using the subject naming hierarchy.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|IndividualDeadLetterStrategy
implements|implements
name|DeadLetterStrategy
block|{
specifier|private
name|String
name|topicPrefix
init|=
literal|"ActiveMQ.DLQ.Topic."
decl_stmt|;
specifier|private
name|String
name|queuePrefix
init|=
literal|"ActiveMQ.DLQ.Queue."
decl_stmt|;
specifier|private
name|boolean
name|useQueueForQueueMessages
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useQueueForTopicMessages
init|=
literal|true
decl_stmt|;
specifier|public
name|ActiveMQDestination
name|getDeadLetterQueueFor
parameter_list|(
name|ActiveMQDestination
name|originalDestination
parameter_list|)
block|{
if|if
condition|(
name|originalDestination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
return|return
name|createDestination
argument_list|(
name|originalDestination
argument_list|,
name|queuePrefix
argument_list|,
name|useQueueForQueueMessages
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createDestination
argument_list|(
name|originalDestination
argument_list|,
name|topicPrefix
argument_list|,
name|useQueueForTopicMessages
argument_list|)
return|;
block|}
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|String
name|getQueuePrefix
parameter_list|()
block|{
return|return
name|queuePrefix
return|;
block|}
comment|/**      * Sets the prefix to use for all dead letter queues for queue messages      */
specifier|public
name|void
name|setQueuePrefix
parameter_list|(
name|String
name|queuePrefix
parameter_list|)
block|{
name|this
operator|.
name|queuePrefix
operator|=
name|queuePrefix
expr_stmt|;
block|}
specifier|public
name|String
name|getTopicPrefix
parameter_list|()
block|{
return|return
name|topicPrefix
return|;
block|}
comment|/**      * Sets the prefix to use for all dead letter queues for topic messages      */
specifier|public
name|void
name|setTopicPrefix
parameter_list|(
name|String
name|topicPrefix
parameter_list|)
block|{
name|this
operator|.
name|topicPrefix
operator|=
name|topicPrefix
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseQueueForQueueMessages
parameter_list|()
block|{
return|return
name|useQueueForQueueMessages
return|;
block|}
comment|/**      * Sets whether a queue or topic should be used for queue messages sent to a      * DLQ. The default is to use a Queue      */
specifier|public
name|void
name|setUseQueueForQueueMessages
parameter_list|(
name|boolean
name|useQueueForQueueMessages
parameter_list|)
block|{
name|this
operator|.
name|useQueueForQueueMessages
operator|=
name|useQueueForQueueMessages
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseQueueForTopicMessages
parameter_list|()
block|{
return|return
name|useQueueForTopicMessages
return|;
block|}
comment|/**      * Sets whether a queue or topic should be used for topic messages sent to a      * DLQ. The default is to use a Queue      */
specifier|public
name|void
name|setUseQueueForTopicMessages
parameter_list|(
name|boolean
name|useQueueForTopicMessages
parameter_list|)
block|{
name|this
operator|.
name|useQueueForTopicMessages
operator|=
name|useQueueForTopicMessages
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|ActiveMQDestination
name|originalDestination
parameter_list|,
name|String
name|prefix
parameter_list|,
name|boolean
name|useQueue
parameter_list|)
block|{
name|String
name|name
init|=
name|prefix
operator|+
name|originalDestination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|useQueue
condition|)
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit


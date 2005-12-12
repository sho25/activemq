begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
package|;
end_package

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
comment|/**  * Defines the pretech message policies for different types of consumers  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQPrefetchPolicy
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ActiveMQPrefetchPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_PREFETCH_SIZE
init|=
operator|(
name|Short
operator|.
name|MAX_VALUE
operator|-
literal|1
operator|)
decl_stmt|;
specifier|private
name|int
name|queuePrefetch
decl_stmt|;
specifier|private
name|int
name|queueBrowserPrefetch
decl_stmt|;
specifier|private
name|int
name|topicPrefetch
decl_stmt|;
specifier|private
name|int
name|durableTopicPrefetch
decl_stmt|;
specifier|private
name|int
name|inputStreamPrefetch
decl_stmt|;
comment|/**      * Initialize default prefetch policies      */
specifier|public
name|ActiveMQPrefetchPolicy
parameter_list|()
block|{
name|this
operator|.
name|queuePrefetch
operator|=
literal|1000
expr_stmt|;
name|this
operator|.
name|queueBrowserPrefetch
operator|=
literal|500
expr_stmt|;
name|this
operator|.
name|topicPrefetch
operator|=
name|MAX_PREFETCH_SIZE
expr_stmt|;
name|this
operator|.
name|durableTopicPrefetch
operator|=
literal|100
expr_stmt|;
name|this
operator|.
name|inputStreamPrefetch
operator|=
literal|100
expr_stmt|;
block|}
comment|/**      * @return Returns the durableTopicPrefetch.      */
specifier|public
name|int
name|getDurableTopicPrefetch
parameter_list|()
block|{
return|return
name|durableTopicPrefetch
return|;
block|}
comment|/**      * @param durableTopicPrefetch The durableTopicPrefetch to set.      */
specifier|public
name|void
name|setDurableTopicPrefetch
parameter_list|(
name|int
name|durableTopicPrefetch
parameter_list|)
block|{
name|this
operator|.
name|durableTopicPrefetch
operator|=
name|getMaxPrefetchLimit
argument_list|(
name|durableTopicPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the queuePrefetch.      */
specifier|public
name|int
name|getQueuePrefetch
parameter_list|()
block|{
return|return
name|queuePrefetch
return|;
block|}
comment|/**      * @param queuePrefetch The queuePrefetch to set.      */
specifier|public
name|void
name|setQueuePrefetch
parameter_list|(
name|int
name|queuePrefetch
parameter_list|)
block|{
name|this
operator|.
name|queuePrefetch
operator|=
name|getMaxPrefetchLimit
argument_list|(
name|queuePrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the queueBrowserPrefetch.      */
specifier|public
name|int
name|getQueueBrowserPrefetch
parameter_list|()
block|{
return|return
name|queueBrowserPrefetch
return|;
block|}
comment|/**      * @param queueBrowserPrefetch The queueBrowserPrefetch to set.      */
specifier|public
name|void
name|setQueueBrowserPrefetch
parameter_list|(
name|int
name|queueBrowserPrefetch
parameter_list|)
block|{
name|this
operator|.
name|queueBrowserPrefetch
operator|=
name|getMaxPrefetchLimit
argument_list|(
name|queueBrowserPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the topicPrefetch.      */
specifier|public
name|int
name|getTopicPrefetch
parameter_list|()
block|{
return|return
name|topicPrefetch
return|;
block|}
comment|/**      * @param topicPrefetch The topicPrefetch to set.      */
specifier|public
name|void
name|setTopicPrefetch
parameter_list|(
name|int
name|topicPrefetch
parameter_list|)
block|{
name|this
operator|.
name|topicPrefetch
operator|=
name|getMaxPrefetchLimit
argument_list|(
name|topicPrefetch
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|getMaxPrefetchLimit
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|int
name|result
init|=
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
name|MAX_PREFETCH_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|<
name|value
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"maximum prefetch limit has been reset from "
operator|+
name|value
operator|+
literal|" to "
operator|+
name|MAX_PREFETCH_SIZE
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|setAll
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|this
operator|.
name|durableTopicPrefetch
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|queueBrowserPrefetch
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|queuePrefetch
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|topicPrefetch
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|inputStreamPrefetch
operator|=
literal|1
expr_stmt|;
block|}
specifier|public
name|int
name|getInputStreamPrefetch
parameter_list|()
block|{
return|return
name|inputStreamPrefetch
return|;
block|}
specifier|public
name|void
name|setInputStreamPrefetch
parameter_list|(
name|int
name|inputStreamPrefetch
parameter_list|)
block|{
name|this
operator|.
name|inputStreamPrefetch
operator|=
name|getMaxPrefetchLimit
argument_list|(
name|inputStreamPrefetch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


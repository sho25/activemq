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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
comment|/**  * Defines the prefetch message policies for different types of consumers  *   * @org.apache.xbean.XBean element="prefetchPolicy"  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQPrefetchPolicy
extends|extends
name|Object
implements|implements
name|Serializable
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MAX_PREFETCH_SIZE
init|=
name|Short
operator|.
name|MAX_VALUE
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_QUEUE_PREFETCH
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_QUEUE_BROWSER_PREFETCH
init|=
literal|500
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DURABLE_TOPIC_PREFETCH
init|=
literal|100
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_OPTIMIZE_DURABLE_TOPIC_PREFETCH
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_INPUT_STREAM_PREFETCH
init|=
literal|100
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_TOPIC_PREFETCH
init|=
name|MAX_PREFETCH_SIZE
decl_stmt|;
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
name|ActiveMQPrefetchPolicy
operator|.
name|class
argument_list|)
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
name|optimizeDurableTopicPrefetch
decl_stmt|;
specifier|private
name|int
name|inputStreamPrefetch
decl_stmt|;
specifier|private
name|int
name|maximumPendingMessageLimit
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
name|DEFAULT_QUEUE_PREFETCH
expr_stmt|;
name|this
operator|.
name|queueBrowserPrefetch
operator|=
name|DEFAULT_QUEUE_BROWSER_PREFETCH
expr_stmt|;
name|this
operator|.
name|topicPrefetch
operator|=
name|DEFAULT_TOPIC_PREFETCH
expr_stmt|;
name|this
operator|.
name|durableTopicPrefetch
operator|=
name|DEFAULT_DURABLE_TOPIC_PREFETCH
expr_stmt|;
name|this
operator|.
name|optimizeDurableTopicPrefetch
operator|=
name|DEFAULT_OPTIMIZE_DURABLE_TOPIC_PREFETCH
expr_stmt|;
name|this
operator|.
name|inputStreamPrefetch
operator|=
name|DEFAULT_INPUT_STREAM_PREFETCH
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
comment|/**      * @return Returns the optimizeDurableTopicPrefetch.      */
specifier|public
name|int
name|getOptimizeDurableTopicPrefetch
parameter_list|()
block|{
return|return
name|optimizeDurableTopicPrefetch
return|;
block|}
comment|/**      * @param optimizeAcknowledgePrefetch The optimizeDurableTopicPrefetch to      *                set.      */
specifier|public
name|void
name|setOptimizeDurableTopicPrefetch
parameter_list|(
name|int
name|optimizeAcknowledgePrefetch
parameter_list|)
block|{
name|this
operator|.
name|optimizeDurableTopicPrefetch
operator|=
name|optimizeAcknowledgePrefetch
expr_stmt|;
block|}
specifier|public
name|int
name|getMaximumPendingMessageLimit
parameter_list|()
block|{
return|return
name|maximumPendingMessageLimit
return|;
block|}
comment|/**      * Sets how many messages a broker will keep around, above the prefetch      * limit, for non-durable topics before starting to discard older messages.      */
specifier|public
name|void
name|setMaximumPendingMessageLimit
parameter_list|(
name|int
name|maximumPendingMessageLimit
parameter_list|)
block|{
name|this
operator|.
name|maximumPendingMessageLimit
operator|=
name|maximumPendingMessageLimit
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
name|LOG
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
name|this
operator|.
name|optimizeDurableTopicPrefetch
operator|=
name|i
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
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|ActiveMQPrefetchPolicy
condition|)
block|{
name|ActiveMQPrefetchPolicy
name|other
init|=
operator|(
name|ActiveMQPrefetchPolicy
operator|)
name|object
decl_stmt|;
return|return
name|this
operator|.
name|queuePrefetch
operator|==
name|other
operator|.
name|queuePrefetch
operator|&&
name|this
operator|.
name|queueBrowserPrefetch
operator|==
name|other
operator|.
name|queueBrowserPrefetch
operator|&&
name|this
operator|.
name|topicPrefetch
operator|==
name|other
operator|.
name|topicPrefetch
operator|&&
name|this
operator|.
name|durableTopicPrefetch
operator|==
name|other
operator|.
name|durableTopicPrefetch
operator|&&
name|this
operator|.
name|optimizeDurableTopicPrefetch
operator|==
name|other
operator|.
name|optimizeDurableTopicPrefetch
operator|&&
name|this
operator|.
name|inputStreamPrefetch
operator|==
name|other
operator|.
name|inputStreamPrefetch
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit


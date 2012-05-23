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
name|virtual
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
name|broker
operator|.
name|*
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
name|broker
operator|.
name|region
operator|.
name|DestinationFilter
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
name|DestinationInterceptor
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
name|ActiveMQTopic
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
comment|/**  * Creates<a href="http://activemq.org/site/mirrored-queues.html">Mirrored  * Queue</a> using a prefix and postfix to define the topic name on which to mirror the queue to.  *  *   * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|MirroredQueue
implements|implements
name|DestinationInterceptor
implements|,
name|BrokerServiceAware
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MirroredQueue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|prefix
init|=
literal|"VirtualTopic.Mirror."
decl_stmt|;
specifier|private
name|String
name|postfix
init|=
literal|""
decl_stmt|;
specifier|private
name|boolean
name|copyMessage
init|=
literal|true
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|public
name|Destination
name|intercept
parameter_list|(
specifier|final
name|Destination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|isQueue
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
operator|||
name|brokerService
operator|.
name|isUseTempMirroredQueues
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|Destination
name|mirrorDestination
init|=
name|getMirrorDestination
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|mirrorDestination
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|DestinationFilter
argument_list|(
name|destination
argument_list|)
block|{
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|message
operator|.
name|setDestination
argument_list|(
name|mirrorDestination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|mirrorDestination
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCopyMessage
argument_list|()
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
name|message
operator|.
name|setDestination
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMemoryUsage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// set this to null so that it will use the queue memoryUsage instance instead of the topic.
name|super
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to lookup the mirror destination for: "
operator|+
name|destination
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|brokerService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No brokerService injected!"
argument_list|)
throw|;
block|}
name|ActiveMQDestination
name|topic
init|=
name|getMirrorTopic
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|topic
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|brokerService
operator|.
name|removeDestination
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to remove mirror destination for "
operator|+
name|destination
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|create
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|String
name|getPostfix
parameter_list|()
block|{
return|return
name|postfix
return|;
block|}
comment|/**      * Sets any postix used to identify the queue consumers      */
specifier|public
name|void
name|setPostfix
parameter_list|(
name|String
name|postfix
parameter_list|)
block|{
name|this
operator|.
name|postfix
operator|=
name|postfix
expr_stmt|;
block|}
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**      * Sets the prefix wildcard used to identify the queue consumers for a given      * topic      */
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCopyMessage
parameter_list|()
block|{
return|return
name|copyMessage
return|;
block|}
comment|/**      * Sets whether a copy of the message will be sent to each destination.      * Defaults to true so that the forward destination is set as the      * destination of the message      */
specifier|public
name|void
name|setCopyMessage
parameter_list|(
name|boolean
name|copyMessage
parameter_list|)
block|{
name|this
operator|.
name|copyMessage
operator|=
name|copyMessage
expr_stmt|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
comment|// Implementation methods
comment|//-------------------------------------------------------------------------
specifier|protected
name|Destination
name|getMirrorDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No brokerService injected!"
argument_list|)
throw|;
block|}
name|ActiveMQDestination
name|topic
init|=
name|getMirrorTopic
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|brokerService
operator|.
name|getDestination
argument_list|(
name|topic
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQDestination
name|getMirrorTopic
parameter_list|(
name|ActiveMQDestination
name|original
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|prefix
operator|+
name|original
operator|.
name|getPhysicalName
argument_list|()
operator|+
name|postfix
argument_list|)
return|;
block|}
block|}
end_class

end_unit


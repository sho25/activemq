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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|Broker
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
name|ConnectionContext
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationFilter
import|;
end_import

begin_comment
comment|/**  * Creates<a href="http://activemq.org/site/virtual-destinations.html">Virtual  * Topics</a> using a prefix and postfix. The virtual destination creates a  * wildcard that is then used to look up all active queue subscriptions which  * match.  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|VirtualTopic
implements|implements
name|VirtualDestination
block|{
specifier|private
name|String
name|prefix
init|=
literal|"Consumer.*."
decl_stmt|;
specifier|private
name|String
name|postfix
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|">"
decl_stmt|;
specifier|private
name|boolean
name|selectorAware
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|local
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getVirtualDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|intercept
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
return|return
name|selectorAware
condition|?
operator|new
name|SelectorAwareVirtualTopicInterceptor
argument_list|(
name|destination
argument_list|,
name|getPrefix
argument_list|()
argument_list|,
name|getPostfix
argument_list|()
argument_list|,
name|isLocal
argument_list|()
argument_list|)
else|:
operator|new
name|VirtualTopicInterceptor
argument_list|(
name|destination
argument_list|,
name|getPrefix
argument_list|()
argument_list|,
name|getPostfix
argument_list|()
argument_list|,
name|isLocal
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getMappedDestinations
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|prefix
operator|+
name|name
operator|+
name|postfix
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|interceptMappedDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
comment|// do a reverse map from destination to get actual virtual destination
specifier|final
name|String
name|physicalName
init|=
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|getRegex
argument_list|(
name|prefix
argument_list|)
operator|+
literal|"(.*)"
operator|+
name|getRegex
argument_list|(
name|postfix
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|physicalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
specifier|final
name|String
name|virtualName
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|MappedQueueFilter
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|virtualName
argument_list|)
argument_list|,
name|destination
argument_list|)
return|;
block|}
return|return
name|destination
return|;
block|}
specifier|private
name|String
name|getRegex
parameter_list|(
name|String
name|part
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|part
operator|.
name|toCharArray
argument_list|()
control|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'.'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"\\."
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'*'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"[^\\.]*"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|builder
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
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
throws|throws
name|Exception
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|&&
name|destination
operator|.
name|isPattern
argument_list|()
condition|)
block|{
name|DestinationFilter
name|filter
init|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|prefix
operator|+
name|DestinationFilter
operator|.
name|ANY_DESCENDENT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|broker
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{     }
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
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Indicates whether the selectors of consumers are used to determine      * dispatch to a virtual destination, when true only messages matching an      * existing consumer will be dispatched.      *      * @param selectorAware      *            when true take consumer selectors into consideration      */
specifier|public
name|void
name|setSelectorAware
parameter_list|(
name|boolean
name|selectorAware
parameter_list|)
block|{
name|this
operator|.
name|selectorAware
operator|=
name|selectorAware
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSelectorAware
parameter_list|()
block|{
return|return
name|selectorAware
return|;
block|}
specifier|public
name|boolean
name|isLocal
parameter_list|()
block|{
return|return
name|local
return|;
block|}
specifier|public
name|void
name|setLocal
parameter_list|(
name|boolean
name|local
parameter_list|)
block|{
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
literal|"VirtualTopic:"
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|postfix
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|selectorAware
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|local
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit


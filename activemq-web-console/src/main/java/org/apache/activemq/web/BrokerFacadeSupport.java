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
name|web
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerInvocationHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|jmx
operator|.
name|BrokerViewMBean
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
name|jmx
operator|.
name|DestinationViewMBean
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
name|jmx
operator|.
name|DurableSubscriptionViewMBean
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
name|jmx
operator|.
name|ManagementContext
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
name|jmx
operator|.
name|QueueViewMBean
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
name|jmx
operator|.
name|TopicViewMBean
import|;
end_import

begin_comment
comment|/**  * A useful base class for an implementation of {@link BrokerFacade}  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BrokerFacadeSupport
implements|implements
name|BrokerFacade
block|{
specifier|public
specifier|abstract
name|ManagementContext
name|getManagementContext
parameter_list|()
function_decl|;
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|getQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|queues
init|=
name|broker
operator|.
name|getQueues
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queues
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|getTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|queues
init|=
name|broker
operator|.
name|getTopics
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queues
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|getDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|queues
init|=
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queues
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|QueueViewMBean
name|getQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
name|QueueViewMBean
operator|)
name|getDestinationByName
argument_list|(
name|getQueues
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|TopicViewMBean
name|getTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
name|TopicViewMBean
operator|)
name|getDestinationByName
argument_list|(
name|getTopics
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|protected
name|DestinationViewMBean
name|getDestinationByName
parameter_list|(
name|Collection
argument_list|<
name|Object
argument_list|>
name|collection
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iter
init|=
name|collection
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DestinationViewMBean
name|destinationViewMBean
init|=
operator|(
name|DestinationViewMBean
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|destinationViewMBean
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|destinationViewMBean
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|Collection
argument_list|<
name|Object
argument_list|>
name|getManagedObjects
parameter_list|(
name|ObjectName
index|[]
name|names
parameter_list|,
name|Class
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|MBeanServer
name|mbeanServer
init|=
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbeanServer
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ObjectName
name|name
init|=
name|names
index|[
name|i
index|]
decl_stmt|;
name|Object
name|value
init|=
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit


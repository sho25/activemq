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
name|web
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
name|BrokerService
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
name|BrokerView
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
name|ManagedRegionBroker
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
name|List
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|BrokerFacade
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
name|BrokerFacade
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|public
name|BrokerFacade
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
specifier|public
name|BrokerService
name|getBrokerService
parameter_list|()
block|{
return|return
name|brokerService
return|;
block|}
specifier|public
name|Broker
name|getBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|brokerService
operator|.
name|getBroker
argument_list|()
return|;
block|}
specifier|public
name|ManagementContext
name|getManagementContext
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getManagementContext
argument_list|()
return|;
block|}
specifier|public
name|BrokerViewMBean
name|getBrokerAdmin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO could use JMX to look this up
return|return
name|brokerService
operator|.
name|getAdminView
argument_list|()
return|;
block|}
specifier|public
name|ManagedRegionBroker
name|getManagedBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerView
name|adminView
init|=
name|brokerService
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
if|if
condition|(
name|adminView
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|adminView
operator|.
name|getBroker
argument_list|()
return|;
block|}
comment|// TODO - we should not have to use JMX to implement the following methods...
specifier|public
name|Collection
name|getQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerView
name|broker
init|=
name|brokerService
operator|.
name|getAdminView
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
name|getTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerView
name|broker
init|=
name|brokerService
operator|.
name|getAdminView
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
name|getDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerView
name|broker
init|=
name|brokerService
operator|.
name|getAdminView
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
specifier|protected
name|Collection
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
name|answer
init|=
operator|new
name|ArrayList
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
comment|/**      * public Collection getQueues() throws Exception { ManagedRegionBroker      * broker = getManagedBroker(); if (broker == null) { return new      * ArrayList(); } return      * broker.getQueueRegion().getDestinationMap().values(); }      *       *       * public Collection getTopics() throws Exception { ManagedRegionBroker      * broker = getManagedBroker(); if (broker == null) { return new      * ArrayList(); } return      * broker.getTopicRegion().getDestinationMap().values(); }      */
block|}
end_class

end_unit


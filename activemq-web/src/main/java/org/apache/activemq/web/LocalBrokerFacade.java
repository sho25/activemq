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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|region
operator|.
name|Queue
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

begin_comment
comment|/**  * An implementation of {@link BrokerFacade} which uses a local in JVM broker  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|LocalBrokerFacade
extends|extends
name|BrokerFacadeSupport
block|{
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|public
name|LocalBrokerFacade
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
name|String
name|getBrokerName
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|brokerService
operator|.
name|getBrokerName
argument_list|()
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
specifier|public
name|void
name|purgeQueue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
name|destinations
init|=
name|getManagedBroker
argument_list|()
operator|.
name|getQueueRegion
argument_list|()
operator|.
name|getDestinations
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|destinations
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Queue
name|regionQueue
init|=
operator|(
name|Queue
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|regionQueue
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


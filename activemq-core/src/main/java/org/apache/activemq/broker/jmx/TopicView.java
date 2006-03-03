begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|jmx
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
name|Topic
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
name|ConsumerInfo
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
name|RemoveSubscriptionInfo
import|;
end_import

begin_class
specifier|public
class|class
name|TopicView
extends|extends
name|DestinationView
implements|implements
name|TopicViewMBean
block|{
specifier|public
name|TopicView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|Topic
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|createDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubcriptionName
argument_list|(
name|subscriberName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|destroyDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
block|{
name|RemoveSubscriptionInfo
name|info
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubcriptionName
argument_list|(
name|subscriberName
argument_list|)
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


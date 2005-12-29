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
name|DestinationStatistics
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
name|QueueRegion
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
name|policy
operator|.
name|PolicyMap
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
name|memory
operator|.
name|UsageManager
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
name|store
operator|.
name|PersistenceAdapter
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
name|thread
operator|.
name|TaskRunnerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ManagedQueueRegion
extends|extends
name|QueueRegion
block|{
specifier|private
specifier|final
name|ManagedRegionBroker
name|regionBroker
decl_stmt|;
specifier|public
name|ManagedQueueRegion
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|,
name|UsageManager
name|memoryManager
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|,
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|,
name|PolicyMap
name|policyMap
parameter_list|)
block|{
name|super
argument_list|(
name|destinationStatistics
argument_list|,
name|memoryManager
argument_list|,
name|taskRunnerFactory
argument_list|,
name|persistenceAdapter
argument_list|,
name|policyMap
argument_list|)
expr_stmt|;
name|regionBroker
operator|=
name|broker
expr_stmt|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Throwable
block|{
name|Destination
name|rc
init|=
name|super
operator|.
name|createDestination
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|regionBroker
operator|.
name|register
argument_list|(
name|destination
argument_list|,
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|removeDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|regionBroker
operator|.
name|unregister
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


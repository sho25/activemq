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
name|ProducerInfo
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
name|MessageStore
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
name|usage
operator|.
name|MemoryUsage
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.12 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseDestination
implements|implements
name|Destination
block|{
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|Broker
name|broker
decl_stmt|;
specifier|protected
specifier|final
name|MessageStore
name|store
decl_stmt|;
specifier|protected
specifier|final
name|SystemUsage
name|systemUsage
decl_stmt|;
specifier|protected
specifier|final
name|MemoryUsage
name|memoryUsage
decl_stmt|;
specifier|private
name|boolean
name|producerFlowControl
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|maxProducersToAudit
init|=
literal|1024
decl_stmt|;
specifier|private
name|int
name|maxAuditDepth
init|=
literal|1
decl_stmt|;
specifier|private
name|boolean
name|enableAudit
init|=
literal|true
decl_stmt|;
specifier|protected
specifier|final
name|DestinationStatistics
name|destinationStatistics
init|=
operator|new
name|DestinationStatistics
argument_list|()
decl_stmt|;
comment|/**      * @param broker       * @param store       * @param destination      * @param systemUsage       * @param parentStats      */
specifier|public
name|BaseDestination
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|MessageStore
name|store
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|SystemUsage
name|systemUsage
parameter_list|,
name|DestinationStatistics
name|parentStats
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|systemUsage
operator|=
name|systemUsage
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|=
operator|new
name|MemoryUsage
argument_list|(
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
argument_list|,
name|destination
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|.
name|setUsagePortion
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
comment|// Let the store know what usage manager we are using so that he can
comment|// flush messages to disk when usage gets high.
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|setMemoryUsage
argument_list|(
name|this
operator|.
name|memoryUsage
argument_list|)
expr_stmt|;
block|}
comment|// let's copy the enabled property from the parent DestinationStatistics
name|this
operator|.
name|destinationStatistics
operator|.
name|setEnabled
argument_list|(
name|parentStats
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|destinationStatistics
operator|.
name|setParent
argument_list|(
name|parentStats
argument_list|)
expr_stmt|;
block|}
comment|/**      * initialize the destination      * @throws Exception      */
specifier|public
specifier|abstract
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * @return the producerFlowControl      */
specifier|public
name|boolean
name|isProducerFlowControl
parameter_list|()
block|{
return|return
name|producerFlowControl
return|;
block|}
comment|/**      * @param producerFlowControl the producerFlowControl to set      */
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|producerFlowControl
parameter_list|)
block|{
name|this
operator|.
name|producerFlowControl
operator|=
name|producerFlowControl
expr_stmt|;
block|}
comment|/**      * @return the maxProducersToAudit      */
specifier|public
name|int
name|getMaxProducersToAudit
parameter_list|()
block|{
return|return
name|maxProducersToAudit
return|;
block|}
comment|/**      * @param maxProducersToAudit the maxProducersToAudit to set      */
specifier|public
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
block|{
name|this
operator|.
name|maxProducersToAudit
operator|=
name|maxProducersToAudit
expr_stmt|;
block|}
comment|/**      * @return the maxAuditDepth      */
specifier|public
name|int
name|getMaxAuditDepth
parameter_list|()
block|{
return|return
name|maxAuditDepth
return|;
block|}
comment|/**      * @param maxAuditDepth the maxAuditDepth to set      */
specifier|public
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
block|{
name|this
operator|.
name|maxAuditDepth
operator|=
name|maxAuditDepth
expr_stmt|;
block|}
comment|/**      * @return the enableAudit      */
specifier|public
name|boolean
name|isEnableAudit
parameter_list|()
block|{
return|return
name|enableAudit
return|;
block|}
comment|/**      * @param enableAudit the enableAudit to set      */
specifier|public
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
block|{
name|this
operator|.
name|enableAudit
operator|=
name|enableAudit
expr_stmt|;
block|}
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|destinationStatistics
operator|.
name|getProducers
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|destinationStatistics
operator|.
name|getProducers
argument_list|()
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|final
name|MemoryUsage
name|getMemoryUsage
parameter_list|()
block|{
return|return
name|memoryUsage
return|;
block|}
specifier|public
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|()
block|{
return|return
name|destinationStatistics
return|;
block|}
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
specifier|public
specifier|final
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|destinationStatistics
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
operator|!=
literal|0
operator|||
name|destinationStatistics
operator|.
name|getProducers
argument_list|()
operator|.
name|getCount
argument_list|()
operator|!=
literal|0
return|;
block|}
block|}
end_class

end_unit


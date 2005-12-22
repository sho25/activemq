begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
import|;
end_import

begin_class
specifier|public
class|class
name|DestinationView
implements|implements
name|DestinationViewMBean
block|{
specifier|private
specifier|final
name|Destination
name|destination
decl_stmt|;
specifier|public
name|DestinationView
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|destination
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getEnqueueCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getDequeueCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getConsumerCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMessages
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMessagesCached
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessagesCached
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit


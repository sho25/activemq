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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|OpenTypeSupport
operator|.
name|OpenTypeFactory
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
name|ActiveMQMessage
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

begin_class
specifier|public
class|class
name|DestinationView
block|{
specifier|protected
specifier|final
name|Destination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|ManagedRegionBroker
name|broker
decl_stmt|;
specifier|public
name|DestinationView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|Destination
name|destination
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
specifier|public
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|Message
index|[]
name|messages
init|=
name|destination
operator|.
name|browse
argument_list|()
decl_stmt|;
name|CompositeData
name|c
index|[]
init|=
operator|new
name|CompositeData
index|[
name|messages
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|c
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|c
index|[
name|i
index|]
operator|=
name|OpenTypeSupport
operator|.
name|convert
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|c
return|;
block|}
specifier|public
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|OpenTypeFactory
name|factory
init|=
name|OpenTypeSupport
operator|.
name|getFactory
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Message
index|[]
name|messages
init|=
name|destination
operator|.
name|browse
argument_list|()
decl_stmt|;
name|CompositeType
name|ct
init|=
name|factory
operator|.
name|getCompositeType
argument_list|()
decl_stmt|;
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
literal|"MessageList"
argument_list|,
literal|"MessageList"
argument_list|,
name|ct
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"JMSMessageID"
block|}
argument_list|)
decl_stmt|;
name|TabularDataSupport
name|rc
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|rc
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|ct
argument_list|,
name|factory
operator|.
name|getFields
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit


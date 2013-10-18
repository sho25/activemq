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
name|partition
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
name|BrokerPlugin
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
name|partition
operator|.
name|dto
operator|.
name|Partitioning
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A BrokerPlugin which partitions client connections over a cluster of brokers.  *  * @org.apache.xbean.XBean element="partitionBrokerPlugin"  */
end_comment

begin_class
specifier|public
class|class
name|PartitionBrokerPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|protected
name|int
name|minTransferCount
decl_stmt|;
specifier|protected
name|Partitioning
name|config
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|PartitionBroker
argument_list|(
name|broker
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|int
name|getMinTransferCount
parameter_list|()
block|{
return|return
name|minTransferCount
return|;
block|}
specifier|public
name|void
name|setMinTransferCount
parameter_list|(
name|int
name|minTransferCount
parameter_list|)
block|{
name|this
operator|.
name|minTransferCount
operator|=
name|minTransferCount
expr_stmt|;
block|}
specifier|public
name|Partitioning
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
specifier|public
name|void
name|setConfig
parameter_list|(
name|Partitioning
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|void
name|setConfigAsJson
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|config
operator|=
name|Partitioning
operator|.
name|MAPPER
operator|.
name|readValue
argument_list|(
name|config
argument_list|,
name|Partitioning
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getBrokerURL
parameter_list|(
name|PartitionBroker
name|partitionBroker
parameter_list|,
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|brokers
operator|!=
literal|null
condition|)
block|{
return|return
name|config
operator|.
name|brokers
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

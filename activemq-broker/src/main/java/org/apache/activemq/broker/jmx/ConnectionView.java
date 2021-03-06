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
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Connection
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
name|util
operator|.
name|IOExceptionSupport
import|;
end_import

begin_class
specifier|public
class|class
name|ConnectionView
implements|implements
name|ConnectionViewMBean
block|{
specifier|private
specifier|final
name|Connection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|ManagementContext
name|managementContext
decl_stmt|;
specifier|private
name|String
name|userName
decl_stmt|;
specifier|public
name|ConnectionView
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|this
argument_list|(
name|connection
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionView
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|ManagementContext
name|managementContext
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|managementContext
operator|=
name|managementContext
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return true if the Connection is slow      */
annotation|@
name|Override
specifier|public
name|boolean
name|isSlow
parameter_list|()
block|{
return|return
name|connection
operator|.
name|isSlow
argument_list|()
return|;
block|}
comment|/**      * @return if after being marked, the Connection is still writing      */
annotation|@
name|Override
specifier|public
name|boolean
name|isBlocked
parameter_list|()
block|{
return|return
name|connection
operator|.
name|isBlocked
argument_list|()
return|;
block|}
comment|/**      * @return true if the Connection is connected      */
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
comment|/**      * @return true if the Connection is active      */
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|connection
operator|.
name|isActive
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDispatchQueueSize
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getDispatchQueueSize
argument_list|()
return|;
block|}
comment|/**      * Resets the statistics      */
annotation|@
name|Override
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|connection
operator|.
name|getStatistics
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getConnectionId
argument_list|()
return|;
block|}
specifier|public
name|String
name|getConnectionId
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getConnectionId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectName
index|[]
name|getConsumers
parameter_list|()
block|{
name|ObjectName
index|[]
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
name|managementContext
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ObjectName
name|query
init|=
name|createConsumerQueury
argument_list|(
name|connection
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|managementContext
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|result
operator|=
name|names
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectName
index|[]
name|getProducers
parameter_list|()
block|{
name|ObjectName
index|[]
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
name|managementContext
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ObjectName
name|query
init|=
name|createProducerQueury
argument_list|(
name|connection
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|managementContext
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|result
operator|=
name|names
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
return|return
name|result
return|;
block|}
specifier|private
name|ObjectName
name|createConsumerQueury
parameter_list|(
name|String
name|clientId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|BrokerMBeanSupport
operator|.
name|createConsumerQueury
argument_list|(
name|managementContext
operator|.
name|getJmxDomainName
argument_list|()
argument_list|,
name|clientId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|ObjectName
name|createProducerQueury
parameter_list|(
name|String
name|clientId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|BrokerMBeanSupport
operator|.
name|createProducerQueury
argument_list|(
name|managementContext
operator|.
name|getJmxDomainName
argument_list|()
argument_list|,
name|clientId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getActiveTransactionCount
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getActiveTransactionCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Long
name|getOldestActiveTransactionDuration
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getOldestActiveTransactionDuration
argument_list|()
return|;
block|}
block|}
end_class

end_unit


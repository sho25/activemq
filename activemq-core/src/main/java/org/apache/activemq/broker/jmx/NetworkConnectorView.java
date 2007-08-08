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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|NetworkConnector
import|;
end_import

begin_class
specifier|public
class|class
name|NetworkConnectorView
implements|implements
name|NetworkConnectorViewMBean
block|{
specifier|private
specifier|final
name|NetworkConnector
name|connector
decl_stmt|;
specifier|public
name|NetworkConnectorView
parameter_list|(
name|NetworkConnector
name|connector
parameter_list|)
block|{
name|this
operator|.
name|connector
operator|=
name|connector
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|connector
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|int
name|getNetworkTTL
parameter_list|()
block|{
return|return
name|connector
operator|.
name|getNetworkTTL
argument_list|()
return|;
block|}
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|connector
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|connector
operator|.
name|getUserName
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isBridgeTempDestinations
parameter_list|()
block|{
return|return
name|connector
operator|.
name|isBridgeTempDestinations
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isConduitSubscriptions
parameter_list|()
block|{
return|return
name|connector
operator|.
name|isConduitSubscriptions
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isDecreaseNetworkConsumerPriority
parameter_list|()
block|{
return|return
name|connector
operator|.
name|isDecreaseNetworkConsumerPriority
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isDispatchAsync
parameter_list|()
block|{
return|return
name|connector
operator|.
name|isDispatchAsync
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isDynamicOnly
parameter_list|()
block|{
return|return
name|connector
operator|.
name|isDynamicOnly
argument_list|()
return|;
block|}
specifier|public
name|void
name|setBridgeTempDestinations
parameter_list|(
name|boolean
name|bridgeTempDestinations
parameter_list|)
block|{
name|connector
operator|.
name|setBridgeTempDestinations
argument_list|(
name|bridgeTempDestinations
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setConduitSubscriptions
parameter_list|(
name|boolean
name|conduitSubscriptions
parameter_list|)
block|{
name|connector
operator|.
name|setConduitSubscriptions
argument_list|(
name|conduitSubscriptions
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDispatchAsync
parameter_list|(
name|boolean
name|dispatchAsync
parameter_list|)
block|{
name|connector
operator|.
name|setDispatchAsync
argument_list|(
name|dispatchAsync
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDynamicOnly
parameter_list|(
name|boolean
name|dynamicOnly
parameter_list|)
block|{
name|connector
operator|.
name|setDynamicOnly
argument_list|(
name|dynamicOnly
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNetworkTTL
parameter_list|(
name|int
name|networkTTL
parameter_list|)
block|{
name|connector
operator|.
name|setNetworkTTL
argument_list|(
name|networkTTL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|connector
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPrefetchSize
parameter_list|(
name|int
name|prefetchSize
parameter_list|)
block|{
name|connector
operator|.
name|setPrefetchSize
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|connector
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
name|String
name|pw
init|=
name|connector
operator|.
name|getPassword
argument_list|()
decl_stmt|;
comment|// Hide the password for security reasons.
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
name|pw
operator|=
name|pw
operator|.
name|replaceAll
argument_list|(
literal|"."
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
return|return
name|pw
return|;
block|}
specifier|public
name|void
name|setDecreaseNetworkConsumerPriority
parameter_list|(
name|boolean
name|decreaseNetworkConsumerPriority
parameter_list|)
block|{
name|connector
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
name|decreaseNetworkConsumerPriority
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


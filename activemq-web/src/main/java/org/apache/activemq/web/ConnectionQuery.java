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
name|ConnectionViewMBean
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
name|SubscriptionViewMBean
import|;
end_import

begin_comment
comment|/**  * Query for a single connection.  *   * @author ms  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionQuery
block|{
specifier|private
specifier|final
name|BrokerFacade
name|mBrokerFacade
decl_stmt|;
specifier|private
name|String
name|mConnectionID
decl_stmt|;
specifier|public
name|ConnectionQuery
parameter_list|(
name|BrokerFacade
name|brokerFacade
parameter_list|)
block|{
name|mBrokerFacade
operator|=
name|brokerFacade
expr_stmt|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|// empty
block|}
specifier|public
name|void
name|setConnectionID
parameter_list|(
name|String
name|connectionID
parameter_list|)
block|{
name|mConnectionID
operator|=
name|connectionID
expr_stmt|;
block|}
specifier|public
name|String
name|getConnectionID
parameter_list|()
block|{
return|return
name|mConnectionID
return|;
block|}
specifier|public
name|ConnectionViewMBean
name|getConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectionID
init|=
name|getConnectionID
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionID
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|mBrokerFacade
operator|.
name|getConnection
argument_list|(
name|connectionID
argument_list|)
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectionID
init|=
name|getConnectionID
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionID
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
return|return
name|mBrokerFacade
operator|.
name|getConsumersOnConnection
argument_list|(
name|connectionID
argument_list|)
return|;
block|}
block|}
end_class

end_unit


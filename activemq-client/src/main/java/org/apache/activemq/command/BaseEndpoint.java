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
name|command
package|;
end_package

begin_comment
comment|/**  * A default endpoint.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|BaseEndpoint
implements|implements
name|Endpoint
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|BrokerInfo
name|brokerInfo
decl_stmt|;
specifier|public
name|BaseEndpoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|brokerText
init|=
literal|""
decl_stmt|;
name|BrokerId
name|brokerId
init|=
name|getBrokerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerId
operator|!=
literal|null
condition|)
block|{
name|brokerText
operator|=
literal|" broker: "
operator|+
name|brokerId
expr_stmt|;
block|}
return|return
literal|"Endpoint[name:"
operator|+
name|name
operator|+
name|brokerText
operator|+
literal|"]"
return|;
block|}
comment|/**      * Returns the broker ID for this endpoint, if the endpoint is a broker or      * null      */
specifier|public
name|BrokerId
name|getBrokerId
parameter_list|()
block|{
if|if
condition|(
name|brokerInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|brokerInfo
operator|.
name|getBrokerId
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the broker information for this endpoint, if the endpoint is a      * broker or null      */
specifier|public
name|BrokerInfo
name|getBrokerInfo
parameter_list|()
block|{
return|return
name|brokerInfo
return|;
block|}
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{
name|this
operator|.
name|brokerInfo
operator|=
name|brokerInfo
expr_stmt|;
block|}
block|}
end_class

end_unit


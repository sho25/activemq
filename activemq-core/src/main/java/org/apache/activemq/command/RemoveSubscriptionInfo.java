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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="9"  * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
class|class
name|RemoveSubscriptionInfo
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|REMOVE_SUBSCRIPTION_INFO
decl_stmt|;
specifier|protected
name|ConnectionId
name|connectionId
decl_stmt|;
specifier|protected
name|String
name|clientId
decl_stmt|;
specifier|protected
name|String
name|subscriptionName
decl_stmt|;
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ConnectionId
name|getConnectionId
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
specifier|public
name|void
name|setConnectionId
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @deprecated      */
specifier|public
name|String
name|getSubcriptionName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
comment|/**      * @deprecated      */
specifier|public
name|void
name|setSubcriptionName
parameter_list|(
name|String
name|subscriptionName
parameter_list|)
block|{
name|this
operator|.
name|subscriptionName
operator|=
name|subscriptionName
expr_stmt|;
block|}
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
specifier|public
name|void
name|setSubscriptionName
parameter_list|(
name|String
name|subscriptionName
parameter_list|)
block|{
name|this
operator|.
name|subscriptionName
operator|=
name|subscriptionName
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
specifier|public
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processRemoveSubscription
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit


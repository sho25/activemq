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
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * When a client connects to a broker, the broker send the client a BrokerInfo  * so that the client knows which broker node he's talking to and also any peers  * that the node has in his cluster.  This is the broker helping the client out  * in discovering other nodes in the cluster.  *   * @openwire:marshaller  * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
class|class
name|BrokerInfo
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
name|BROKER_INFO
decl_stmt|;
name|BrokerId
name|brokerId
decl_stmt|;
name|String
name|brokerURL
decl_stmt|;
name|RedeliveryPolicy
name|redeliveryPolicy
decl_stmt|;
name|BrokerInfo
name|peerBrokerInfos
index|[]
decl_stmt|;
name|String
name|brokerName
decl_stmt|;
specifier|public
name|boolean
name|isBrokerInfo
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
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
name|BrokerId
name|getBrokerId
parameter_list|()
block|{
return|return
name|brokerId
return|;
block|}
specifier|public
name|void
name|setBrokerId
parameter_list|(
name|BrokerId
name|brokerId
parameter_list|)
block|{
name|this
operator|.
name|brokerId
operator|=
name|brokerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
name|brokerURL
return|;
block|}
specifier|public
name|void
name|setBrokerURL
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|brokerURL
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|BrokerInfo
index|[]
name|getPeerBrokerInfos
parameter_list|()
block|{
return|return
name|peerBrokerInfos
return|;
block|}
specifier|public
name|void
name|setPeerBrokerInfos
parameter_list|(
name|BrokerInfo
index|[]
name|peerBrokerInfos
parameter_list|)
block|{
name|this
operator|.
name|peerBrokerInfos
operator|=
name|peerBrokerInfos
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|RedeliveryPolicy
name|getRedeliveryPolicy
parameter_list|()
block|{
return|return
name|redeliveryPolicy
return|;
block|}
specifier|public
name|void
name|setRedeliveryPolicy
parameter_list|(
name|RedeliveryPolicy
name|redeliveryPolicy
parameter_list|)
block|{
name|this
operator|.
name|redeliveryPolicy
operator|=
name|redeliveryPolicy
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|brokerName
return|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerName
operator|=
name|brokerName
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
name|Throwable
block|{
return|return
name|visitor
operator|.
name|processBrokerInfo
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit


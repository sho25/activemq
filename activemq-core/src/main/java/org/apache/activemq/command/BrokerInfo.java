begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * When a client connects to a broker, the broker send the client a BrokerInfo so that the client knows which broker  * node he's talking to and also any peers that the node has in his cluster. This is the broker helping the client out  * in discovering other nodes in the cluster.  *   * @openwire:marshaller code="2"  * @version $Revision: 1.7 $  */
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
name|boolean
name|slaveBroker
decl_stmt|;
name|boolean
name|masterBroker
decl_stmt|;
name|boolean
name|faultTolerantConfiguration
decl_stmt|;
name|boolean
name|networkConnection
decl_stmt|;
name|boolean
name|duplexConnection
decl_stmt|;
name|BrokerInfo
name|peerBrokerInfos
index|[]
decl_stmt|;
name|String
name|brokerName
decl_stmt|;
name|long
name|connectionId
decl_stmt|;
name|String
name|brokerUploadUrl
decl_stmt|;
name|String
name|networkProperties
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
comment|/**      * @openwire:property version=1 testSize=0      */
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
name|Exception
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isSlaveBroker
parameter_list|()
block|{
return|return
name|slaveBroker
return|;
block|}
specifier|public
name|void
name|setSlaveBroker
parameter_list|(
name|boolean
name|slaveBroker
parameter_list|)
block|{
name|this
operator|.
name|slaveBroker
operator|=
name|slaveBroker
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isMasterBroker
parameter_list|()
block|{
return|return
name|masterBroker
return|;
block|}
comment|/**      * @param masterBroker      *            The masterBroker to set.      */
specifier|public
name|void
name|setMasterBroker
parameter_list|(
name|boolean
name|masterBroker
parameter_list|)
block|{
name|this
operator|.
name|masterBroker
operator|=
name|masterBroker
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the faultTolerantConfiguration.      */
specifier|public
name|boolean
name|isFaultTolerantConfiguration
parameter_list|()
block|{
return|return
name|faultTolerantConfiguration
return|;
block|}
comment|/**      * @param faultTolerantConfiguration      *            The faultTolerantConfiguration to set.      */
specifier|public
name|void
name|setFaultTolerantConfiguration
parameter_list|(
name|boolean
name|faultTolerantConfiguration
parameter_list|)
block|{
name|this
operator|.
name|faultTolerantConfiguration
operator|=
name|faultTolerantConfiguration
expr_stmt|;
block|}
comment|/**      * @openwire:property version=2      * @return the duplexConnection      */
specifier|public
name|boolean
name|isDuplexConnection
parameter_list|()
block|{
return|return
name|this
operator|.
name|duplexConnection
return|;
block|}
comment|/**      * @param duplexConnection the duplexConnection to set      */
specifier|public
name|void
name|setDuplexConnection
parameter_list|(
name|boolean
name|duplexConnection
parameter_list|)
block|{
name|this
operator|.
name|duplexConnection
operator|=
name|duplexConnection
expr_stmt|;
block|}
comment|/**      * @openwire:property version=2      * @return the networkConnection      */
specifier|public
name|boolean
name|isNetworkConnection
parameter_list|()
block|{
return|return
name|this
operator|.
name|networkConnection
return|;
block|}
comment|/**      * @param networkConnection the networkConnection to set      */
specifier|public
name|void
name|setNetworkConnection
parameter_list|(
name|boolean
name|networkConnection
parameter_list|)
block|{
name|this
operator|.
name|networkConnection
operator|=
name|networkConnection
expr_stmt|;
block|}
comment|/**      * The broker assigns a each connection it accepts a connection id.      *       * @openwire:property version=2      */
specifier|public
name|long
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
name|long
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
comment|/**      * The URL to use when uploading BLOBs to the broker or some other external file/http server      *      * @openwire:property version=3      */
specifier|public
name|String
name|getBrokerUploadUrl
parameter_list|()
block|{
return|return
name|brokerUploadUrl
return|;
block|}
specifier|public
name|void
name|setBrokerUploadUrl
parameter_list|(
name|String
name|brokerUploadUrl
parameter_list|)
block|{
name|this
operator|.
name|brokerUploadUrl
operator|=
name|brokerUploadUrl
expr_stmt|;
block|}
comment|/**      *  @openwire:property version=3 cache=false      * @return the networkProperties      */
specifier|public
name|String
name|getNetworkProperties
parameter_list|()
block|{
return|return
name|this
operator|.
name|networkProperties
return|;
block|}
comment|/**      * @param networkProperties the networkProperties to set      */
specifier|public
name|void
name|setNetworkProperties
parameter_list|(
name|String
name|networkProperties
parameter_list|)
block|{
name|this
operator|.
name|networkProperties
operator|=
name|networkProperties
expr_stmt|;
block|}
block|}
end_class

end_unit


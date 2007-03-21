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
name|usecases
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
name|BrokerService
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
name|TransportConnector
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
name|network
operator|.
name|DemandForwardingBridge
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
name|network
operator|.
name|NetworkBridgeConfiguration
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
name|transport
operator|.
name|TransportFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ThreeBrokerTopicNetworkUsingTcpTest
extends|extends
name|ThreeBrokerTopicNetworkTest
block|{
specifier|protected
name|List
name|bridges
decl_stmt|;
specifier|protected
name|void
name|bridgeBrokers
parameter_list|(
name|BrokerService
name|localBroker
parameter_list|,
name|BrokerService
name|remoteBroker
parameter_list|)
throws|throws
name|Exception
block|{
name|List
name|remoteTransports
init|=
name|remoteBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|List
name|localTransports
init|=
name|localBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|URI
name|remoteURI
decl_stmt|,
name|localURI
decl_stmt|;
if|if
condition|(
operator|!
name|remoteTransports
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|localTransports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|remoteURI
operator|=
operator|(
operator|(
name|TransportConnector
operator|)
name|remoteTransports
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
name|localURI
operator|=
operator|(
operator|(
name|TransportConnector
operator|)
name|localTransports
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
comment|// Ensure that we are connecting using tcp
if|if
condition|(
name|remoteURI
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"tcp:"
argument_list|)
operator|&&
name|localURI
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"tcp:"
argument_list|)
condition|)
block|{
name|NetworkBridgeConfiguration
name|config
init|=
operator|new
name|NetworkBridgeConfiguration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setBrokerName
argument_list|(
name|localBroker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|DemandForwardingBridge
name|bridge
init|=
operator|new
name|DemandForwardingBridge
argument_list|(
name|config
argument_list|,
name|TransportFactory
operator|.
name|connect
argument_list|(
name|localURI
argument_list|)
argument_list|,
name|TransportFactory
operator|.
name|connect
argument_list|(
name|remoteURI
argument_list|)
argument_list|)
decl_stmt|;
name|bridges
operator|.
name|add
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker or local broker is not using tcp connectors"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker or local broker has no registered connectors."
argument_list|)
throw|;
block|}
name|MAX_SETUP_TIME
operator|=
literal|2000
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|bridges
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit


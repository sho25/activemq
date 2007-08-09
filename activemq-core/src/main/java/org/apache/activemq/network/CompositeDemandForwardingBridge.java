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
name|network
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerId
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
name|BrokerInfo
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
name|Command
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
name|ConsumerInfo
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
name|Endpoint
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
name|NetworkBridgeFilter
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
name|Transport
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
name|ServiceSupport
import|;
end_import

begin_comment
comment|/**  * A demand forwarding bridge which works with multicast style transports where  * a single Transport could be communicating with multiple remote brokers  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDemandForwardingBridge
extends|extends
name|DemandForwardingBridgeSupport
block|{
specifier|protected
specifier|final
name|BrokerId
name|remoteBrokerPath
index|[]
init|=
operator|new
name|BrokerId
index|[]
block|{
literal|null
block|}
decl_stmt|;
specifier|protected
name|Object
name|brokerInfoMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|CompositeDemandForwardingBridge
parameter_list|(
name|NetworkBridgeConfiguration
name|configuration
parameter_list|,
name|Transport
name|localBroker
parameter_list|,
name|Transport
name|remoteBroker
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|,
name|localBroker
argument_list|,
name|remoteBroker
argument_list|)
expr_stmt|;
name|remoteBrokerName
operator|=
name|remoteBroker
operator|.
name|toString
argument_list|()
expr_stmt|;
name|remoteBrokerNameKnownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|serviceRemoteBrokerInfo
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|brokerInfoMutex
init|)
block|{
name|BrokerInfo
name|remoteBrokerInfo
init|=
operator|(
name|BrokerInfo
operator|)
name|command
decl_stmt|;
name|BrokerId
name|remoteBrokerId
init|=
name|remoteBrokerInfo
operator|.
name|getBrokerId
argument_list|()
decl_stmt|;
comment|// lets associate the incoming endpoint with a broker ID so we can
comment|// refer to it later
name|Endpoint
name|from
init|=
name|command
operator|.
name|getFrom
argument_list|()
decl_stmt|;
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Incoming command does not have a from endpoint: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|from
operator|.
name|setBrokerInfo
argument_list|(
name|remoteBrokerInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|localBrokerId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|localBrokerId
operator|.
name|equals
argument_list|(
name|remoteBrokerId
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Disconnecting loop back connection."
argument_list|)
expr_stmt|;
comment|// waitStarted();
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|disposed
condition|)
block|{
name|triggerLocalStartBridge
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|addRemoteBrokerToBrokerPath
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|info
operator|.
name|setBrokerPath
argument_list|(
name|appendToBrokerPath
argument_list|(
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|getFromBrokerId
argument_list|(
name|info
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the broker ID that the command came from      */
specifier|protected
name|BrokerId
name|getFromBrokerId
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|BrokerId
name|answer
init|=
literal|null
decl_stmt|;
name|Endpoint
name|from
init|=
name|command
operator|.
name|getFrom
argument_list|()
decl_stmt|;
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Incoming command does not have a from endpoint: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|answer
operator|=
name|from
operator|.
name|getBrokerId
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
return|return
name|answer
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No broker ID is available for endpoint: "
operator|+
name|from
operator|+
literal|" from command: "
operator|+
name|command
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|serviceLocalBrokerInfo
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// TODO is there much we can do here?
block|}
specifier|protected
name|NetworkBridgeFilter
name|createNetworkBridgeFilter
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|NetworkBridgeFilter
argument_list|(
name|getFromBrokerId
argument_list|(
name|info
argument_list|)
argument_list|,
name|configuration
operator|.
name|getNetworkTTL
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|BrokerId
index|[]
name|getRemoteBrokerPath
parameter_list|()
block|{
return|return
name|remoteBrokerPath
return|;
block|}
block|}
end_class

end_unit


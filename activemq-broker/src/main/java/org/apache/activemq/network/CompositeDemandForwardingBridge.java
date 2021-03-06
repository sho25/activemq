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
name|transport
operator|.
name|Transport
import|;
end_import

begin_comment
comment|/**  * A demand forwarding bridge which works with multicast style transports where  * a single Transport could be communicating with multiple remote brokers  *  * @org.apache.xbean.XBean  *  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDemandForwardingBridge
extends|extends
name|DemandForwardingBridgeSupport
block|{
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
block|{     }
block|}
end_class

end_unit


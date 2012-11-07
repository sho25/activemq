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
name|transport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
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

begin_comment
comment|/**  * A TransportServer asynchronously accepts {@see Transport} objects and then  * delivers those objects to a {@see TransportAcceptListener}.  *  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransportServer
extends|extends
name|Service
block|{
comment|/**      * Registers an {@see TransportAcceptListener} which is notified of accepted      * channels.      *      * @param acceptListener      */
name|void
name|setAcceptListener
parameter_list|(
name|TransportAcceptListener
name|acceptListener
parameter_list|)
function_decl|;
comment|/**      * Associates a broker info with the transport server so that the transport      * can do discovery advertisements of the broker.      *      * @param brokerInfo      */
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
function_decl|;
name|URI
name|getConnectURI
parameter_list|()
function_decl|;
comment|/**      * @return The socket address that this transport is accepting connections      *         on or null if this does not or is not currently accepting      *         connections on a socket.      */
name|InetSocketAddress
name|getSocketAddress
parameter_list|()
function_decl|;
comment|/**      * For TransportServers that provide SSL connections to their connected peers they should      * return true here if and only if they populate the ConnectionInfo command presented to      * the Broker with the peers certificate chain so that the broker knows it can use that      * information to authenticate the connected peer.      *      * @return true if this transport server provides SSL level security over its      *          connections.      */
name|boolean
name|isSslServer
parameter_list|()
function_decl|;
block|}
end_interface

end_unit


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
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Represents the client side of a transport allowing messages to be sent  * synchronously, asynchronously and consumed.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Transport
extends|extends
name|Service
block|{
comment|/**      * A one way asynchronous send      *      * @param command      * @throws IOException      */
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * An asynchronous request response where the Receipt will be returned in      * the future. If responseCallback is not null, then it will be called when      * the response has been completed.      *      * @param command      * @param responseCallback TODO      * @return the FutureResponse      * @throws IOException      */
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Object
name|command
parameter_list|,
name|ResponseCallback
name|responseCallback
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * A synchronous request response      *      * @param command      * @return the response      * @throws IOException      */
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * A synchronous request response      *      * @param command      * @param timeout      * @return the repsonse or null if timeout      * @throws IOException      */
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the current transport listener      *      * @return      */
name|TransportListener
name|getTransportListener
parameter_list|()
function_decl|;
comment|/**      * Registers an inbound command listener      *      * @param commandListener      */
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|commandListener
parameter_list|)
function_decl|;
comment|/**      * @param target      * @return the target      */
parameter_list|<
name|T
parameter_list|>
name|T
name|narrow
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|target
parameter_list|)
function_decl|;
comment|/**      * @return the remote address for this connection      */
name|String
name|getRemoteAddress
parameter_list|()
function_decl|;
comment|/**      * Indicates if the transport can handle faults      *      * @return true if fault tolerant      */
name|boolean
name|isFaultTolerant
parameter_list|()
function_decl|;
comment|/**      * @return true if the transport is disposed      */
name|boolean
name|isDisposed
parameter_list|()
function_decl|;
comment|/**      * @return true if the transport is connected      */
name|boolean
name|isConnected
parameter_list|()
function_decl|;
comment|/**      * @return true if reconnect is supported      */
name|boolean
name|isReconnectSupported
parameter_list|()
function_decl|;
comment|/**      * @return true if updating uris is supported      */
name|boolean
name|isUpdateURIsSupported
parameter_list|()
function_decl|;
comment|/**      * reconnect to another location      * @param uri      * @throws IOException on failure of if not supported      */
name|void
name|reconnect
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Provide a list of available alternative locations      * @param rebalance      * @param uris      * @throws IOException      */
name|void
name|updateURIs
parameter_list|(
name|boolean
name|rebalance
parameter_list|,
name|URI
index|[]
name|uris
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns a counter which gets incremented as data is read from the transport.      * It should only be used to determine if there is progress being made in reading the next command from the transport.      * The value may wrap into the negative numbers.      *      * @return a counter which gets incremented as data is read from the transport.      */
name|int
name|getReceiveCounter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit


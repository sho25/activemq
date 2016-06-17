begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
operator|.
name|ws
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
name|nio
operator|.
name|ByteBuffer
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
comment|/**  * Interface for a WebSocket Transport which provide hooks that a servlet can  * use to pass along WebSocket data and events.  */
end_comment

begin_interface
specifier|public
interface|interface
name|WSTransport
extends|extends
name|Transport
block|{
comment|/**      * WS Transport output sink, used to give the WS Transport implementation      * a way to produce output back to the WS connection without coupling it      * to the implementation.      */
specifier|public
interface|interface
name|WSTransportSink
block|{
comment|/**          * Called from the Transport when new outgoing String data is ready.          *          * @param data          *      The newly prepared outgoing string data.          *          * @throws IOException if an error occurs or the socket doesn't support text data.          */
name|void
name|onSocketOutboundText
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**          * Called from the Transport when new outgoing String data is ready.          *          * @param data          *      The newly prepared outgoing string data.          *          * @throws IOException if an error occurs or the socket doesn't support text data.          */
name|void
name|onSocketOutboundBinary
parameter_list|(
name|ByteBuffer
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**      * @return the WS sub-protocol that this transport is supplying.      */
name|String
name|getSubProtocol
parameter_list|()
function_decl|;
comment|/**      * Called to provide the WS with the output data sink.      */
name|void
name|setTransportSink
parameter_list|(
name|WSTransportSink
name|outputSink
parameter_list|)
function_decl|;
comment|/**      * Called from the WebSocket framework when new incoming String data is received.      *      * @param data      *      The newly received incoming data.      *      * @throws IOException if an error occurs or the socket doesn't support text data.      */
name|void
name|onWebSocketText
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called from the WebSocket framework when new incoming Binary data is received.      *      * @param data      *      The newly received incoming data.      *      * @throws IOException if an error occurs or the socket doesn't support binary data.      */
name|void
name|onWebSocketBinary
parameter_list|(
name|ByteBuffer
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called from the WebSocket framework when the socket has been closed unexpectedly.      *      * @throws IOException if an error while processing the close.      */
name|void
name|onWebSocketClosed
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit


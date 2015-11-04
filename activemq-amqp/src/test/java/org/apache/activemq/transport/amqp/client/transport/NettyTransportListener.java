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
operator|.
name|amqp
operator|.
name|client
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBuf
import|;
end_import

begin_comment
comment|/**  * Listener interface that should be implemented by users of the various  * QpidJMS Transport classes.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NettyTransportListener
block|{
comment|/**      * Called when new incoming data has become available.      *      * @param incoming      *        the next incoming packet of data.      */
name|void
name|onData
parameter_list|(
name|ByteBuf
name|incoming
parameter_list|)
function_decl|;
comment|/**      * Called if the connection state becomes closed.      */
name|void
name|onTransportClosed
parameter_list|()
function_decl|;
comment|/**      * Called when an error occurs during normal Transport operations.      *      * @param cause      *        the error that triggered this event.      */
name|void
name|onTransportError
parameter_list|(
name|Throwable
name|cause
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


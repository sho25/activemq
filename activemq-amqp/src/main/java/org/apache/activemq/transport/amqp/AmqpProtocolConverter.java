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
name|Command
import|;
end_import

begin_comment
comment|/**  * Interface that defines the API for any AMQP protocol converter ised to  * map AMQP mechanics to ActiveMQ and back.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AmqpProtocolConverter
block|{
comment|/**      * A new incoming data packet from the remote peer is handed off to the      * protocol converter for processing.  The type can vary and be either an      * AmqpHeader at the handshake phase or a byte buffer containing the next      * incoming frame data from the remote.      *      * @param data      *        the next incoming data object from the remote peer.      *      * @throws Exception if an error occurs processing the incoming data packet.      */
name|void
name|onAMQPData
parameter_list|(
name|Object
name|data
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Called when the transport detects an exception that the converter      * needs to respond to.      *      * @param error      *        the error that triggered this call.      */
name|void
name|onAMQPException
parameter_list|(
name|IOException
name|error
parameter_list|)
function_decl|;
comment|/**      * Incoming Command object from ActiveMQ.      *      * @param command      *        the next incoming command from the broker.      *      * @throws Exception if an error occurs processing the command.      */
name|void
name|onActiveMQCommand
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * On changes to the transport tracing options the Protocol Converter      * should update its internal state so that the proper AMQP data is      * logged.      */
name|void
name|updateTracer
parameter_list|()
function_decl|;
comment|/**      * Perform any keep alive processing for the connection such as sending      * empty frames or closing connections due to remote end being inactive      * for to long.      *      * @returns the amount of milliseconds to wait before performing another check.      *      * @throws IOException if an error occurs on writing heart-beats to the wire.      */
name|long
name|keepAlive
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit


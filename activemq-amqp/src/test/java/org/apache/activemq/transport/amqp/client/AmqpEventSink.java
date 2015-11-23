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
name|amqp
operator|.
name|client
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

begin_comment
comment|/**  * Interface used by classes that want to process AMQP events sent from  * the transport layer.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AmqpEventSink
block|{
comment|/**      * Event handler for remote peer open of this resource.      *      * @param connection      *        the AmqpConnection instance for easier access to fire events.      *      * @throws IOException if an error occurs while processing the update.      */
name|void
name|processRemoteOpen
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Event handler for remote peer detach of this resource.      *      * @param connection      *        the AmqpConnection instance for easier access to fire events.      *      * @throws IOException if an error occurs while processing the update.      */
name|void
name|processRemoteDetach
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Event handler for remote peer close of this resource.      *      * @param connection      *        the AmqpConnection instance for easier access to fire events.      *      * @throws IOException if an error occurs while processing the update.      */
name|void
name|processRemoteClose
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called when the Proton Engine signals an Delivery related event has been triggered      * for the given endpoint.      *      * @param connection      *        the AmqpConnection instance for easier access to fire events.      *      * @throws IOException if an error occurs while processing the update.      */
name|void
name|processDeliveryUpdates
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called when the Proton Engine signals an Flow related event has been triggered      * for the given endpoint.      *      * @param connection      *        the AmqpConnection instance for easier access to fire events.      *      * @throws IOException if an error occurs while processing the update.      */
name|void
name|processFlowUpdates
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit


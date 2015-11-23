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
name|util
operator|.
name|AsyncResult
import|;
end_import

begin_comment
comment|/**  * AmqpResource specification.  *  * All AMQP types should implement this interface to allow for control of state  * and configuration details.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AmqpResource
extends|extends
name|AmqpEventSink
block|{
comment|/**      * Perform all the work needed to open this resource and store the request      * until such time as the remote peer indicates the resource has become active.      *      * @param request      *        The initiating request that triggered this open call.      */
name|void
name|open
parameter_list|(
name|AsyncResult
name|request
parameter_list|)
function_decl|;
comment|/**      * @return if the resource has moved to the opened state on the remote.      */
name|boolean
name|isOpen
parameter_list|()
function_decl|;
comment|/**      * Called to indicate that this resource is now remotely opened.  Once opened a      * resource can start accepting incoming requests.      */
name|void
name|opened
parameter_list|()
function_decl|;
comment|/**      * Perform all work needed to close this resource and store the request      * until such time as the remote peer indicates the resource has been closed.      *      * @param request      *        The initiating request that triggered this close call.      */
name|void
name|close
parameter_list|(
name|AsyncResult
name|request
parameter_list|)
function_decl|;
comment|/**      * Perform all work needed to detach this resource and store the request      * until such time as the remote peer indicates the resource has been detached.      *      * @param request      *        The initiating request that triggered this detach call.      */
name|void
name|detach
parameter_list|(
name|AsyncResult
name|request
parameter_list|)
function_decl|;
comment|/**      * @return if the resource has moved to the closed state on the remote.      */
name|boolean
name|isClosed
parameter_list|()
function_decl|;
comment|/**      * Called to indicate that this resource is now remotely closed.  Once closed a      * resource can not accept any incoming requests.      */
name|void
name|closed
parameter_list|()
function_decl|;
comment|/**      * Sets the failed state for this Resource and triggers a failure signal for      * any pending ProduverRequest.      */
name|void
name|failed
parameter_list|()
function_decl|;
comment|/**      * Called to indicate that the remote end has become closed but the resource      * was not awaiting a close.  This could happen during an open request where      * the remote does not set an error condition or during normal operation.      *      * @param connection      *        The connection that owns this resource.      */
name|void
name|remotelyClosed
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
function_decl|;
comment|/**      * Sets the failed state for this Resource and triggers a failure signal for      * any pending ProduverRequest.      *      * @param cause      *        The Exception that triggered the failure.      */
name|void
name|failed
parameter_list|(
name|Exception
name|cause
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


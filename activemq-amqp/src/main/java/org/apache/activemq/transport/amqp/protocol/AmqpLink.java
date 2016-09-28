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
name|protocol
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
name|ActiveMQDestination
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
name|LocalTransactionId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|transport
operator|.
name|ErrorCondition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Delivery
import|;
end_import

begin_comment
comment|/**  * Interface used to define the operations needed to implement an AMQP  * Link based endpoint, i.e. Sender, Receiver or Coordinator.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AmqpLink
extends|extends
name|AmqpResource
block|{
comment|/**      * Close the Link with an error indicating the reson for the close.      *      * @param error      *        the error that prompted the close.      */
name|void
name|close
parameter_list|(
name|ErrorCondition
name|error
parameter_list|)
function_decl|;
comment|/**      * Request from the remote peer to detach this resource.      */
name|void
name|detach
parameter_list|()
function_decl|;
comment|/**      * Handles an incoming flow control.      *      * @throws Excption if an error occurs during the flow processing.      */
name|void
name|flow
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Called when a new Delivery arrives for the given Link.      *      * @param delivery      *        the newly arrived delivery on this link.      *      * @throws Exception if an error occurs while processing the new Delivery.      */
name|void
name|delivery
parameter_list|(
name|Delivery
name|delivery
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Handle work necessary on commit of transacted resources associated with      * this Link instance.      *      * @param txnId      *      The Transaction ID being committed.      *      * @throws Exception if an error occurs while performing the commit.      */
name|void
name|commit
parameter_list|(
name|LocalTransactionId
name|txnId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Handle work necessary on rollback of transacted resources associated with      * this Link instance.      *      * @param txnId      *      The Transaction ID being rolled back.      *      * @throws Exception if an error occurs while performing the rollback.      */
name|void
name|rollback
parameter_list|(
name|LocalTransactionId
name|txnId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return the ActiveMQDestination that this link is servicing.      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
function_decl|;
comment|/**      * Sets the ActiveMQDestination that this link will be servicing.      *      * @param destination      *        the ActiveMQDestination that this link services.      */
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
comment|/**      * Adds a new Runnable that is called on close of this link.      *      * @param action      *        a Runnable that will be executed when the link closes or detaches.      */
specifier|public
name|void
name|addCloseAction
parameter_list|(
name|Runnable
name|action
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


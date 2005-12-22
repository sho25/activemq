begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|broker
package|;
end_package

begin_import
import|import
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|ConnectionStatistics
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|command
operator|.
name|Response
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Connection
extends|extends
name|Service
block|{
comment|/**      * @return the connector that created this connection.      */
specifier|public
name|Connector
name|getConnector
parameter_list|()
function_decl|;
comment|/**      * Sends a message to the client.      *       * @param message      *            the message to send to the client.      */
specifier|public
name|void
name|dispatchSync
parameter_list|(
name|Command
name|message
parameter_list|)
function_decl|;
comment|/**      * Sends a message to the client.      *       * @param command      */
specifier|public
name|void
name|dispatchAsync
parameter_list|(
name|Command
name|command
parameter_list|)
function_decl|;
comment|/**      * Services a client command and submits it to the broker.      *       * @param command      */
specifier|public
name|Response
name|service
parameter_list|(
name|Command
name|command
parameter_list|)
function_decl|;
comment|/**      * Handles an unexpected error associated with a connection.      *       * @param error      */
specifier|public
name|void
name|serviceException
parameter_list|(
name|Throwable
name|error
parameter_list|)
function_decl|;
comment|/**      * @return true if the Connection is slow      */
specifier|public
name|boolean
name|isSlow
parameter_list|()
function_decl|;
comment|/**      * @return if after being marked, the Connection is still writing      */
specifier|public
name|boolean
name|isBlocked
parameter_list|()
function_decl|;
comment|/**      * @return true if the Connection is connected      */
specifier|public
name|boolean
name|isConnected
parameter_list|()
function_decl|;
comment|/**      * @return true if the Connection is active      */
specifier|public
name|boolean
name|isActive
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages to be dispatched to this connection      */
specifier|public
name|int
name|getDispatchQueueSize
parameter_list|()
function_decl|;
comment|/**      * Returns the statistics for this connection      */
specifier|public
name|ConnectionStatistics
name|getStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit


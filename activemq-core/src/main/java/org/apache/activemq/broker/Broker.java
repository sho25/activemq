begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|broker
operator|.
name|region
operator|.
name|Region
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
name|BrokerId
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
name|ConnectionInfo
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
name|MessageDispatch
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
name|MessageDispatchNotification
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
name|ProducerInfo
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
name|SessionInfo
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
name|TransactionId
import|;
end_import

begin_comment
comment|/**  * The Message Broker which routes messages,  * maintains subscriptions and connections, acknowledges messages and handles  * transactions.  *  * @version $Revision: 1.8 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Broker
extends|extends
name|Region
extends|,
name|Service
block|{
comment|/**      * Get a Broker from the Broker Stack that is a particular class      * @param type      * @return      */
specifier|public
name|Broker
name|getAdaptor
parameter_list|(
name|Class
name|type
parameter_list|)
function_decl|;
comment|/**      * Get the id of the broker      * @param context      * @param info       * @param client      */
specifier|public
name|BrokerId
name|getBrokerId
parameter_list|()
function_decl|;
comment|/**      * Get the name of the broker      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
function_decl|;
comment|/**      * A remote Broker connects      * @param contection      * @param info       * @param client      */
specifier|public
name|void
name|addBroker
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|BrokerInfo
name|info
parameter_list|)
function_decl|;
comment|/**      * Remove a BrokerInfo      * @param connection      * @param info      */
specifier|public
name|void
name|removeBroker
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|BrokerInfo
name|info
parameter_list|)
function_decl|;
comment|/**      * A client is establishing a connection with the broker.      * @param context      * @param info       * @param client      */
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * A client is disconnecting from the broker.      * @param context the environment the operation is being executed under.      * @param info       * @param client      * @param error null if the client requested the disconnect or the error that caused the client to disconnect.      */
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Adds a session.      * @param context      * @param info      * @throws Throwable      */
specifier|public
name|void
name|addSession
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Removes a session.      * @param context      * @param info      * @throws Throwable      */
specifier|public
name|void
name|removeSession
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Adds a producer.      * @param context the enviorment the operation is being executed under.      */
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Removes a producer.      * @param context the enviorment the operation is being executed under.      */
specifier|public
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * @return all clients added to the Broker.      * @throws Throwable      */
specifier|public
name|Connection
index|[]
name|getClients
parameter_list|()
throws|throws
name|Throwable
function_decl|;
comment|/**      * @return all destinations added to the Broker.      * @throws Throwable      */
specifier|public
name|ActiveMQDestination
index|[]
name|getDestinations
parameter_list|()
throws|throws
name|Throwable
function_decl|;
comment|/**      * Gets a list of all the prepared xa transactions.      * @param client      */
specifier|public
name|TransactionId
index|[]
name|getPreparedTransactions
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Starts a transaction.      * @param client      * @param xid      */
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Prepares a transaction. Only valid for xa transactions.      * @param client      * @param xid      * @return      */
specifier|public
name|int
name|prepareTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Rollsback a transaction.      * @param client      * @param xid      */
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Commits a transaction.      * @param client      * @param xid      * @param onePhase      */
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Forgets a transaction.      * @param client      * @param xid      * @param onePhase      * @throws Throwable       */
specifier|public
name|void
name|forgetTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Get the BrokerInfo's of any connected Brokers      * @return array of peer BrokerInfos      */
name|BrokerInfo
index|[]
name|getPeerBrokerInfos
parameter_list|()
function_decl|;
comment|/**      * Notify the Broker that a dispatch has happened      * @param messageDispatch      */
specifier|public
name|void
name|processDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
function_decl|;
comment|/**      * Notify the Broker of a MessageDispatchNotification      * @param messageDispatchNotification      * @throws Throwable       */
specifier|public
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * @return true if the broker is running as a slave      */
specifier|public
name|boolean
name|isSlaveBroker
parameter_list|()
function_decl|;
comment|/**      * @return true if the broker has stopped      */
specifier|public
name|boolean
name|isStopped
parameter_list|()
function_decl|;
comment|/**      * @return a Set of all durable destinations      */
specifier|public
name|Set
name|getDurableDestinations
parameter_list|()
function_decl|;
block|}
end_interface

end_unit


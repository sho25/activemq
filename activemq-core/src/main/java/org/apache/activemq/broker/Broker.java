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
name|broker
package|;
end_package

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
name|MessageReference
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
name|DestinationInfo
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|Store
import|;
end_import

begin_comment
comment|/**  * The Message Broker which routes messages, maintains subscriptions and  * connections, acknowledges messages and handles transactions.  *   * @version $Revision: 1.8 $  */
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
comment|/**      * Get a Broker from the Broker Stack that is a particular class      *       * @param type      * @return      */
name|Broker
name|getAdaptor
parameter_list|(
name|Class
name|type
parameter_list|)
function_decl|;
comment|/**      * Get the id of the broker      */
name|BrokerId
name|getBrokerId
parameter_list|()
function_decl|;
comment|/**      * Get the name of the broker      */
name|String
name|getBrokerName
parameter_list|()
function_decl|;
comment|/**      * A remote Broker connects      */
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
comment|/**      * Remove a BrokerInfo      *       * @param connection      * @param info      */
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
comment|/**      * A client is establishing a connection with the broker.      *       * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * A client is disconnecting from the broker.      *       * @param context the environment the operation is being executed under.      * @param info      * @param error null if the client requested the disconnect or the error      *                that caused the client to disconnect.      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Adds a session.      *       * @param context      * @param info      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Removes a session.      *       * @param context      * @param info      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Adds a producer.      *       * @param context the enviorment the operation is being executed under.      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Removes a producer.      *       * @param context the enviorment the operation is being executed under.      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * @return all clients added to the Broker.      * @throws Exception TODO      */
name|Connection
index|[]
name|getClients
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * @return all destinations added to the Broker.      * @throws Exception TODO      */
name|ActiveMQDestination
index|[]
name|getDestinations
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Gets a list of all the prepared xa transactions.      *       * @param context transaction ids      * @return      * @throws Exception TODO      */
name|TransactionId
index|[]
name|getPreparedTransactions
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Starts a transaction.      *       * @param context      * @param xid      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Prepares a transaction. Only valid for xa transactions.      *       * @param context      * @param xid      * @return id      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Rollsback a transaction.      *       * @param context      * @param xid      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Commits a transaction.      *       * @param context      * @param xid      * @param onePhase      * @throws Exception TODO      */
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
name|Exception
function_decl|;
comment|/**      * Forgets a transaction.      *       * @param context      * @param transactionId      * @throws Exception      */
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
name|Exception
function_decl|;
comment|/**      * Get the BrokerInfo's of any connected Brokers      *       * @return array of peer BrokerInfos      */
name|BrokerInfo
index|[]
name|getPeerBrokerInfos
parameter_list|()
function_decl|;
comment|/**      * Notify the Broker that a dispatch is going to happen      *       * @param messageDispatch      */
name|void
name|preProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
function_decl|;
comment|/**      * Notify the Broker that a dispatch has happened      *       * @param messageDispatch      */
name|void
name|postProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
function_decl|;
comment|/**      * @return true if the broker has stopped      */
name|boolean
name|isStopped
parameter_list|()
function_decl|;
comment|/**      * @return a Set of all durable destinations      */
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDurableDestinations
parameter_list|()
function_decl|;
comment|/**      * Add and process a DestinationInfo object      *       * @param context      * @param info      * @throws Exception      */
name|void
name|addDestinationInfo
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Remove and process a DestinationInfo object      *       * @param context      * @param info      * @throws Exception      */
name|void
name|removeDestinationInfo
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if fault tolerant      */
name|boolean
name|isFaultTolerantConfiguration
parameter_list|()
function_decl|;
comment|/**      * @return the connection context used to make administration operations on      *         startup or via JMX MBeans      */
name|ConnectionContext
name|getAdminConnectionContext
parameter_list|()
function_decl|;
comment|/**      * Sets the default administration connection context used when configuring      * the broker on startup or via JMX      *       * @param adminConnectionContext      */
name|void
name|setAdminConnectionContext
parameter_list|(
name|ConnectionContext
name|adminConnectionContext
parameter_list|)
function_decl|;
comment|/**      * @return the temp data store      */
name|Store
name|getTempDataStore
parameter_list|()
function_decl|;
comment|/**      * @return the URI that can be used to connect to the local Broker      */
name|URI
name|getVmConnectorURI
parameter_list|()
function_decl|;
comment|/**      * called when the brokerService starts      */
name|void
name|brokerServiceStarted
parameter_list|()
function_decl|;
comment|/**      * @return the BrokerService      */
name|BrokerService
name|getBrokerService
parameter_list|()
function_decl|;
comment|/**      * Ensure we get the Broker at the top of the Stack      *       * @return the broker at the top of the Stack      */
name|Broker
name|getRoot
parameter_list|()
function_decl|;
comment|/**      * Determine if a message has expired -allows default behaviour to be      * overriden - as the timestamp set by the producer can be out of sync with      * the broker      *       * @param messageReference      * @return true if the message is expired      */
name|boolean
name|isExpired
parameter_list|(
name|MessageReference
name|messageReference
parameter_list|)
function_decl|;
comment|/**      * A Message has Expired      *       * @param context      * @param messageReference      */
name|void
name|messageExpired
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
function_decl|;
comment|/**      * A message needs to go the a DLQ      *       * @param context      * @param messageReference      */
name|void
name|sendToDeadLetterQueue
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

